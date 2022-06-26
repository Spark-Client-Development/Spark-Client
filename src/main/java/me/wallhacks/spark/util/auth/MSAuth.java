package me.wallhacks.spark.util.auth;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.AuthException;
import net.minecraft.util.Session;
import org.lwjgl.Sys;
import me.wallhacks.spark.util.auth.account.MSAccount;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MSAuth implements MC {
    private HttpServer srv;
    public MSAccount acc;
    public int state = 0;
    public boolean failed = false;
    public MSAuth() {
        String done = "<html><body><h1>You can close this tab now</h1></body></html>";
        new Thread(() -> {
            try {
                srv = HttpServer.create(new InetSocketAddress(48375), 0);
                srv.createContext("/", new HttpHandler() {
                    public void handle(HttpExchange ex) throws IOException {
                        try {
                            ex.getResponseHeaders().add("Location", "http://localhost:48375/end");
                            ex.sendResponseHeaders(302, -1);
                            new Thread(() -> auth(ex.getRequestURI().getQuery()), "MS Auth Thread").start();
                        } catch (Throwable t) {
                            Spark.logger.warn("Unable to process request 'auth' on MS auth server", t);
                            stop();
                        }
                    }
                });
                srv.createContext("/end", new HttpHandler() {
                    public void handle(HttpExchange ex) throws IOException {
                        try {
                            byte[] b = done.getBytes(StandardCharsets.UTF_8);
                            ex.getResponseHeaders().put("Content-Type", Arrays.asList("text/html; charset=UTF-8"));
                            ex.sendResponseHeaders(200, b.length);
                            OutputStream os = ex.getResponseBody();
                            os.write(b);
                            os.flush();
                            os.close();
                            stop();
                        } catch (Throwable t) {
                            stop();
                        }
                    }
                });
                srv.start();
                Sys.openURL("https://login.live.com/oauth20_authorize.srf" +
                        "?client_id=f187964d-b663-4d6f-8b35-71f146a1e5b7" +
                        "&response_type=code" +
                        "&scope=XboxLive.signin%20XboxLive.offline_access" +
                        "&redirect_uri=http://localhost:48375" +
                        "&prompt=consent" +
                        "&client_secret=hQH7Q~KLvutybVmaDO8YvJ2HrP_CATs_Lq-Wp");
            } catch (Throwable t) {
                Spark.logger.warn("Unable to start MS auth server", t);
                stop();
            }
        }, "Auth").start();
    }

    public String getStatus() {
        switch (state) {
            case 0:
                return "Open your browser";
            case 1:
                return "Getting msToken" + GuiUtil.getLoadingText(false);
            case 2:
                return "Getting xblToken" + GuiUtil.getLoadingText(false);
            case 3:
                return "Getting xstsToken" + GuiUtil.getLoadingText(false);
            case 4:
                return "Getting mcToken" + GuiUtil.getLoadingText(false);
            case 5:
                return "Getting minecraft profile" + GuiUtil.getLoadingText(false);
        }
        return null;
    }

    private void auth(String code) {
        try {
            if (code == null) throw new AuthException("query=null");
            if (code.equals("error=access_denied&error_description=The user has denied access to the scope requested by the client application."))
                throw new AuthException("Access denied");
            if (!code.startsWith("code=")) throw new IllegalStateException("query=" + code);
            state++; //1
            MSToken msToken = new MSToken(code.substring(5));
            state++; //2
            XBLToken xblToken = new XBLToken(msToken.access);
            state++; //3
            XSTSToken xstsToken = new XSTSToken(xblToken.token);
            state++; //4
            MCToken mcToken = new MCToken(xstsToken.userHash, xstsToken.token);
            state++; //5
            Profile profile = new Profile(mcToken.token);
            Session session = new Session(profile.name, profile.uuid, mcToken.token, "mojang");
            acc = new MSAccount(session, msToken.refresh, profile.name, session.getProfile().getId().toString());
        } catch (AuthException t) {
            failed = true;
            //Spark.altManager.status = t.getText();
        }
    }

    public boolean stop() {
        try {
            if (srv != null)
                srv.stop(0);
            return true;
        } catch (Throwable t) {
            Spark.logger.info("tried stopping ms auth but failed");
            return false;
        }
    }
}

package me.wallhacks.spark.util.auth;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

public class Request {
    public HttpURLConnection conn;
    public Request(String url) throws MalformedURLException, IOException {
        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
    }

    public void header(String key, String value) {
        conn.setRequestProperty(key, value);
    }

    public void post(String s) throws IOException {
        conn.setRequestMethod("POST");
        byte[] out = s.getBytes(StandardCharsets.UTF_8);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(out);
        }
    }

    public void post(Map<Object, Object> map) throws IOException {
        StringJoiner sj = new StringJoiner("&");
        for (Entry<Object, Object> entry : map.entrySet())
            sj.add(URLEncoder.encode(entry.getKey().toString(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        post(sj.toString());
    }

    public void get() throws ProtocolException {
        conn.setRequestMethod("GET");
    }

    public int response() throws IOException {
        return conn.getResponseCode();
    }

    public String body() throws IOException {
        StringBuilder sb = new StringBuilder();
        try (Reader r = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
            int i;
            while ((i = r.read()) >= 0) {
                sb.append((char)i);
            }
        }
        return sb.toString();
    }

    public String error() throws IOException {
        StringBuilder sb = new StringBuilder();
        try (Reader r = new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8)) {
            int i;
            while ((i = r.read()) >= 0) {
                sb.append((char)i);
            }
        }
        return sb.toString();
    }
}
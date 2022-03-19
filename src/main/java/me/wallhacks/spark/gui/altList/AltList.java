package me.wallhacks.spark.gui.altList;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.AltManager;
import me.wallhacks.spark.util.GuiUtil;
import org.lwjgl.input.Mouse;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.util.auth.account.Account;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;

import java.util.ArrayList;

public class AltList extends GuiPanelBase {

    public ArrayList<AltEntry> list = new ArrayList<>();

    public AltList() {
    }

    public void setList(AltManager altManager) {
        list = new ArrayList<>();
        if (altManager.getAlts().isEmpty()) return;
        for (Account a : altManager.getAlts()) {
            list.add(new AltEntry(a));
        }
    }

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        int spacing = 4;
        int h = spacing;
        AltEntry toRemove = null;
        for (AltEntry item : list) {
            item.setPositionAndSize(posX+spacing,posY+h,width-spacing*2,36);
            item.renderContent(MouseX,MouseY,deltaTime);
            boolean remove = GuiUtil.drawButton("Delete", item.posX + 22,item.posY + 22,item.posX+78,item.posY+35, ClientConfig.getInstance().getMainColor(), MouseX, MouseY, false);
            boolean login = GuiUtil.drawButton("Login", item.posX + 81,item.posY + 22,item.posX+137,item.posY+35, ClientConfig.getInstance().getMainColor(), MouseX, MouseY, item.account.loading);
            if(Mouse.isButtonDown(0)) {
                if(item.isMouseOn) {
                    if(!selected) {
                        if (login) {
                            item.account.login();
                        } if (remove) {
                            toRemove = item;
                        }
                    }
                    selected = true;
                }
            } else
                selected = false;

            h += 36 + 2;
        }
        h+=20;

        height = h;

        if(toRemove != null) {
            Spark.altManager.removeAlt(toRemove.account);
            list.remove(toRemove);
        }

    }
    boolean selected = false;
}

package me.wallhacks.spark.systems.hud.huds;

import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.Notification;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@HudElement.Registration(name = "Notification", posY = 0.5, posX = 1.0, height = 20, width = 0, description = "Shows info about pvp and other ongoing events", drawBackground = false)
public class Notifications extends HudElement {
    public static Notifications INSTANCE;
    public static final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList<>();
    public ModeSetting mode = new ModeSetting("Alignment", this, "Auto", Arrays.asList("Auto","Down", "Up"));
    public DoubleSetting stayTime = new DoubleSetting("StayTime", this, 1.5, 0.5, 20.0);
    public BooleanSetting toggle = new BooleanSetting("Toggle", this, true);
    public BooleanSetting pop = new BooleanSetting("TotemPops", this, false);
    public BooleanSetting death = new BooleanSetting("Deaths", this, false);

    public Notifications() {
        INSTANCE = this;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    public static void addNotification(Notification notification) {
        if (INSTANCE.isEnabled())
            notifications.add(notification);
    }

    @Override
    public int getWidth() {
        return fontManager.getTextWidth("Notification") + 13;
    }

    @Override
    public void draw(float deltaTime) {
        super.draw(deltaTime);
        ScaledResolution sr = new ScaledResolution(MC.mc);
        if (isInHudEditor()) {
            if (getRenderPosX() + getWidth() / 2 > sr.getScaledWidth() / 2) {
                Gui.drawRect(getRenderPosX(), getRenderPosY(), getRenderPosX() + 3, getRenderPosY() + 20, HudSettings.getInstance().getGuiHudMainColor().getRGB());
                fontManager.drawString("Notification", getRenderPosX() + 7, getRenderPosY() + 7, HudSettings.getInstance().getGuiHudSecondColor().getRGB());
            } else {
                Gui.drawRect(getRenderPosX() + fontManager.getTextWidth("Notification") + 10, getRenderPosY(), getRenderPosX() + 13 + fontManager.getTextWidth("Notification"), getRenderPosY() + 20, HudSettings.getInstance().getGuiHudMainColor().getRGB());
                fontManager.drawString("Notification", getRenderPosX() + 5, getRenderPosY() + 7, HudSettings.getInstance().getGuiHudSecondColor().getRGB());
            }
        } else {
            AtomicInteger count = new AtomicInteger();
            notifications.removeIf(notification -> notification.stage == 5);
            notifications.forEach(notification -> {
                if (!notification.didOffset) {
                    notification.didOffset = true;
                    notification.offset = count.get();
                } else {
                    if (notification.offset != count.get()) {
                        if (count.get() > notification.offset) {
                            notification.offset = (int) Math.min(count.get(), notification.offset + notification.animateTimer.getPassedTimeMs());
                        } else {
                            notification.offset = (int) Math.max(count.get(), notification.offset - notification.animateTimer.getPassedTimeMs());
                        }
                    }
                }
                if (notification.timer.getPassedTimeMs() > stayTime.getValue() * 1000 && notification.stage < 3) {
                    notification.stage++;
                    notification.animateX = 0;
                }
                if (notification.stage <= 1) {
                    notification.animateX += notification.animateTimer.getPassedTimeMs() * 1.5;
                    notification.animateX = Math.min((notification.stage == 1 ? 10 : 13) + fontManager.getTextWidth(notification.text), notification.animateX);
                    if (notification.animateX == (notification.stage == 1 ? 10 : 13) + fontManager.getTextWidth(notification.text)) {
                        notification.stage++;
                        notification.animateX = 0;
                    }
                } else if (notification.stage >= 3) {
                    notification.animateX += notification.animateTimer.getPassedTimeMs() * 1.5;
                    notification.animateX = Math.min((notification.stage == 3 ? 10 : 13) + fontManager.getTextWidth(notification.text), notification.animateX);
                    if (notification.animateX == (notification.stage == 3 ? 10 : 13) + fontManager.getTextWidth(notification.text)) {
                        notification.stage++;
                        notification.animateX = 0;
                    }
                    
                }
                notification.animateTimer.reset();
                if (notification.stage != 5) {
                    if (getRenderPosX() + getWidth() / 2 > sr.getScaledWidth() / 2) {
                        int offset = fontManager.getTextWidth(notification.text) - fontManager.getTextWidth("Notification") - 3;
                        Gui.drawRect(getRenderPosX() + 3 - offset + (notification.stage == 0 ? fontManager.getTextWidth(notification.text) + 10 - notification.animateX : notification.stage == 4 ? notification.animateX : 0), getRenderPosY() + notification.offset, getRenderPosX() + 10 + fontManager.getTextWidth(notification.text) - offset, getRenderPosY() + 20 + notification.offset, HudSettings.getInstance().getGuiHudListBackgroundColor().getRGB());
                        fontManager.drawString(notification.text, getRenderPosX() - offset + 7 + (notification.stage == 0 ? fontManager.getTextWidth(notification.text) + 13 - notification.animateX : notification.stage == 4 ? notification.animateX : 0), getRenderPosY() + 7 + notification.offset, HudSettings.getInstance().getGuiHudSecondColor().getRGB());
                        Gui.drawRect(getRenderPosX() - offset + (notification.stage == 0 ? fontManager.getTextWidth(notification.text) + 13 - notification.animateX : notification.stage == 4 ? notification.animateX : 0), getRenderPosY() + notification.offset, getRenderPosX() + (notification.stage == 0 ? fontManager.getTextWidth(notification.text) + 13 : notification.stage == 1 ? +fontManager.getTextWidth(notification.text) + 13 - notification.animateX : notification.stage == 3 ? 3 + notification.animateX : notification.stage == 4 ? fontManager.getTextWidth(notification.text) + 13 : 3) - offset, getRenderPosY() + 20 + notification.offset, HudSettings.getInstance().getGuiHudMainColor().getRGB());
                    } else {
                        Gui.drawRect(getRenderPosX(), getRenderPosY() + notification.offset, getRenderPosX() + (notification.stage == 0 ? notification.animateX : notification.stage == 4 ? 10 + fontManager.getTextWidth(notification.text) - notification.animateX : 10 + fontManager.getTextWidth(notification.text)), getRenderPosY() + 20 + notification.offset, HudSettings.getInstance().getGuiHudListBackgroundColor().getRGB());
                        fontManager.drawString(notification.text, getRenderPosX() + (notification.stage == 0 ? notification.animateX - fontManager.getTextWidth(notification.text) - 13 : notification.stage == 4 ? -notification.animateX : 0) + 5, getRenderPosY() + notification.offset + 7, HudSettings.getInstance().getGuiHudSecondColor().getRGB());
                        Gui.drawRect(getRenderPosX() + (notification.stage == 0 ? 0 : notification.stage == 1 ? notification.animateX : notification.stage == 2 ? 10 + fontManager.getTextWidth(notification.text) : notification.stage == 3 ? 10 + fontManager.getTextWidth(notification.text) - notification.animateX : 0), getRenderPosY() + notification.offset, getRenderPosX() + (notification.stage == 0 ? notification.animateX : notification.stage == 4 ? 13 + fontManager.getTextWidth(notification.text) - notification.animateX : 13 + fontManager.getTextWidth(notification.text)), getRenderPosY() + 20 + notification.offset, HudSettings.getInstance().getGuiHudMainColor().getRGB());
                    }
                }
                boolean up = mode.is("Auto") ? (getRenderPosY() > 0.5) : mode.is("Up");

                count.addAndGet(up ? -24 : 24);

            });

        }
    }
}


package me.wallhacks.spark.systems.hud;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.FontManager;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.util.MC;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class HudElement extends SettingsHolder implements MC {


    private final String name = getMod().name();
    private final String description = getMod().description();
    protected FontManager fontManager;
    protected HudSettings hudSettings;
    boolean drawBackground = getMod().drawBackground();
    int snappedElement = getMod().snappedElement();
    private double percentPosX = getMod().posX();
    private double percentPosY = getMod().posY();
    private int percentPosSnappedX = getMod().snappedXPos();
    private int percentPosSnappedY = getMod().snappedYPos();
    private int width = getMod().width();
    private int height = getMod().height();
    private boolean isEnabled = getMod().enabled();
    private long Time = System.nanoTime();

    public HudElement() {
        super();
        this.hudSettings = HudSettings.getInstance();
        this.fontManager = Spark.fontManager;
    }

    public void resetPos() {
        percentPosX = getMod().posX();
        percentPosY = getMod().posY();

        percentPosSnappedX = getMod().snappedXPos();
        percentPosSnappedY = getMod().snappedYPos();

        setSnappedElement(getMod().snappedElement());
    }

    public int getSnappedElement() {
        return snappedElement;
    }

    public void setSnappedElement(int snappedElement) {
        if(snappedElement >= 0)
        {
            HudElement check = (HudElement) SystemManager.getHudModules().toArray()[snappedElement];
            while(check != null)
            {
                check = check.getSnappedElementObject();
                if(check == this)
                    return;
            }
        }

        this.snappedElement = snappedElement;
    }

    public HudElement getSnappedElementObject() {
        if (getSnappedElement() < 0)
            return null;
        return (HudElement) SystemManager.getHudModules().toArray()[getSnappedElement()];
    }

    public boolean shouldDrawBackground() {
        return drawBackground;
    }

    public void setBackGround(boolean backGround) {
        drawBackground = backGround;
    }

    public HudElement.Registration getMod() {
        return getClass().getAnnotation(HudElement.Registration.class);
    }

    public int getId() {
        int l = 0;
        for (HudElement b : SystemManager.getHudModules()) {
            if (b == this) return l;
            l++;
        }
        return 0;
    }

    public double getPercentPosX() {
        return percentPosX;
    }

    public void setPercentPosX(double percentPosX) {
        this.percentPosX = percentPosX;
    }

    public double getPercentPosY() {
        return percentPosY;
    }

    public void setPercentPosY(double percentPosY) {
        this.percentPosY = percentPosY;

    }

    public int getPercentPosSnappedX() {
        return percentPosSnappedX;
    }

    public void setPercentPosSnappedX(int percentPosSnappedX) {
        this.percentPosSnappedX = percentPosSnappedX;
    }

    public int getPercentPosSnappedY() {
        return percentPosSnappedY;
    }

    public void setPercentPosSnappedY(int percentPosSnappedY) {
        this.percentPosSnappedY = percentPosSnappedY;
    }

    public boolean isIn(HudElement base) {
        return (getRenderPosX() < base.getEndRenderPosX() &&
                getEndRenderPosX() > base.getRenderPosX() &&
                getRenderPosY() < base.getEndRenderPosY() &&
                getEndRenderPosY() > base.getRenderPosY());

    }

    public boolean isIn(HudElement base, double in) {
        return (getRenderPosX() + in < base.getEndRenderPosX() &&
                getEndRenderPosX() > base.getRenderPosX() + in &&
                getRenderPosY() + in < base.getEndRenderPosY() &&
                getEndRenderPosY() > base.getRenderPosY() + in);

    }

    public int getRenderPosX(double pos) {
        return (int) (getRenderPosX() + getWidth() * pos);
    }

    public int getRenderPosY(double pos) {
        return (int) (getRenderPosY() + getHeight() * pos);
    }

    public int getRenderPosX() {
        int x = (int) ((mc.displayWidth / 2 - getWidth()) * percentPosX);

        if (getSnappedElement() >= 0) {
            HudElement e = (HudElement) SystemManager.getHudModules().toArray()[getSnappedElement()];

            //test
            if (e == this) return x;

            x = (int) (e.getRenderPosX(getPercentPosSnappedX()) + width * getPercentPosX());
        }

        return x;
    }

    public void setRenderPosX(int posX) {
        if (getSnappedElement() >= 0) {
            HudElement e = (HudElement) SystemManager.getHudModules().toArray()[getSnappedElement()];
            int x = posX + width * MathHelper.clamp(posX / (mc.displayWidth / 2.0), 0, 1) > e.getCenterRenderPosX() ? 1 : 0;
            setPercentPosSnappedX(x);
            setPercentPosX((posX - e.getRenderPosX(x)) / (double) width);
        } else {
            double w = (mc.displayWidth / 2.0 - getWidth());
            setPercentPosX(1.0 / w * posX);
        }

    }

    public int getRenderPosY() {
        int y = (int) ((mc.displayHeight / 2 - getHeight()) * percentPosY);

        if (mc.currentScreen instanceof GuiChat && percentPosY > 0.5)
            y -= 18;

        if (getSnappedElement() >= 0) {
            HudElement e = (HudElement) SystemManager.getHudModules().toArray()[getSnappedElement()];
            if (e != this)
                y = (int) (e.getRenderPosY(getPercentPosSnappedY()) + height * getPercentPosY());
        }

        return y;
    }

    public void setRenderPosY(int posY) {
        if (getSnappedElement() >= 0) {
            HudElement e = (HudElement) SystemManager.getHudModules().toArray()[getSnappedElement()];
            int y = posY + height * MathHelper.clamp(posY / (mc.displayHeight / 2.0), 0, 1) > e.getCenterRenderPosY() ? 1 : 0;
            setPercentPosSnappedY(y);
            setPercentPosY((posY - e.getRenderPosY(y)) / (double) height);
        } else {
            double w = (mc.displayHeight / 2.0 - getHeight());
            setPercentPosY(1.0 / w * posY);
        }

    }

    public int getEndRenderPosX() {
        return getRenderPosX() + getWidth();
    }

    public int getEndRenderPosY() {
        return getRenderPosY() + getHeight();
    }

    public int getCenterRenderPosX() {
        return getRenderPosX() + getWidth() / 2;
    }

    public int getCenterRenderPosY() {
        return getRenderPosY() + getHeight() / 2;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled) {
        if (enabled)
            enable();
        else
            disable();
    }

    @Override
    public String getName() {
        return name;
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public void enable() {
        if (!this.isEnabled) {
            Spark.eventBus.register(this);

            Spark.logger.info("Enabled " + getName());

            this.isEnabled = true;
            onEnable();
        }
    }

    public void disable() {
        if (this.isEnabled) {
            Spark.eventBus.unregister(this);

            Spark.logger.info("Disabled " + getName());

            this.isEnabled = false;
            onDisable();
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    protected boolean isInHudEditor() {
        //if (mc.currentScreen instanceof ClickGuiMenuBase)
        //    return ((ClickGuiMenuBase) mc.currentScreen).getPanel() instanceof HudEditor;
        return false;
    }

    protected boolean isSelectedInHudEditor() {
        //if (mc.currentScreen instanceof ClickGuiMenuBase)
        //    if (((ClickGuiMenuBase) mc.currentScreen).getPanel() instanceof HudEditor) {
        //        return ((HudEditor) ((ClickGuiMenuBase) mc.currentScreen).getPanel()).getSelected() == this;
        //    }
        return false;
    }

    protected boolean alignLeft() {
        return mc.displayWidth / 4 > getCenterRenderPosX();
    }

    protected boolean alignTop() {
        return mc.displayHeight / 4 > getCenterRenderPosY();
    }

    //this event is good trust me
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Chat event) {
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        float deltaTime = (System.nanoTime() - Time) / 1000000f;
        GlStateManager.color(1f, 1f, 1f, 1f);
        if (isInHudEditor() && isSelectedInHudEditor())
            Gui.drawRect(getRenderPosX(), getRenderPosY(), getEndRenderPosX(), getEndRenderPosY(), hudSettings.getGuiHudListBackgroundColor().getRGB());
        else if (shouldDrawBackground()) {
            Gui.drawRect(getRenderPosX(), getRenderPosY(), getEndRenderPosX(), getEndRenderPosY(), hudSettings.getGuiHudListBackgroundColor().getRGB());
        }
        draw(deltaTime);
        Time = System.nanoTime();

        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.color(1f, 1f, 1f, 1f);

    }

    public void draw(float deltaTime) {

    }

    public boolean mouseOver(int mouseX, int mouseY) {
        return mouseX > getRenderPosX() && mouseX < getEndRenderPosX() && mouseY > getRenderPosY() && mouseY < getEndRenderPosY();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Registration {
        String name();

        String description();


        double posX();

        double posY();

        int width();

        int height();


        int snappedElement() default -1;

        int snappedXPos() default 0;

        int snappedYPos() default 0;

        boolean enabled() default false;

        boolean drawBackground() default true;
    }
}

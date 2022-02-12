package me.wallhacks.spark.systems.hud;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.ClickGuiMenuBase;
import me.wallhacks.spark.gui.clickGui.panels.hudeditor.HudEditor;
import me.wallhacks.spark.manager.FontManager;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.util.MC;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;

import java.awt.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class HudElement extends SettingsHolder implements MC {
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

        boolean enabled() default false;
        boolean drawBackground() default true;
    }

    protected FontManager fontManager;
    protected HudSettings hudSettings;
    public HudElement(){
        super();
        this.hudSettings = HudSettings.getInstance();
        this.fontManager = Spark.fontManager;
    }

    int snappedElement = getMod().snappedElement();
    boolean drawBackground = getMod().drawBackground();

    public int getSnappedElement() {
        return snappedElement;
    }
    public HudElement getSnappedElementObject() {
        if(getSnappedElement() < 0)
            return null;
        return (HudElement) SystemManager.getHudModules().toArray()[getSnappedElement()];
    }

    public boolean shouldDrawBackground() {
        return drawBackground;
    }

    public void setBackGround(boolean backGround) {
        drawBackground = backGround;
    }

    public HudElement.Registration getMod(){
        return getClass().getAnnotation(HudElement.Registration.class);
    }

    private final String name = getMod().name();
    private final String description = getMod().description();

    public int getId() {
        int l = 0;
        for (HudElement b : SystemManager.getHudModules())
        {
            if(b == this) return l;
            l++;
        }
        return 0;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    private double percentPosX = getMod().posX();
    private double percentPosY = getMod().posY();

    private int width = getMod().width();
    private int height = getMod().height();

    public double getPercentPosX() {
        return percentPosX;
    }
    public double getPercentPosY() {
        return percentPosY;
    }

    public void setPercentPosX(double percentPosX) {
        this.percentPosX = percentPosX;
    }
    public void setPercentPosY(double percentPosY) {
        this.percentPosY = percentPosY;

    }

    public boolean isIn(HudElement base){
        return (getRenderPosX() < base.getEndRenderPosX() &&
                getEndRenderPosX() > base.getRenderPosX() &&
                getRenderPosY() < base.getEndRenderPosY() &&
                getEndRenderPosY() > base.getRenderPosY());

    }
    public boolean isIn(HudElement base,double in){
        return (getRenderPosX()+in < base.getEndRenderPosX() &&
                getEndRenderPosX() > base.getRenderPosX()+in &&
                getRenderPosY()+in < base.getEndRenderPosY() &&
                getEndRenderPosY() > base.getRenderPosY()+in);

    }

    public void setSnappedElement(int snappedElement) {
        this.snappedElement = snappedElement;
    }

    public int getRenderPosX(double pos) {
        return (int) (getRenderPosX()+getWidth()*pos);
    }
    public int getRenderPosY(double pos) {
        return (int) (getRenderPosY()+getHeight()*pos);
    }


    public int getRenderPosX() {
        int x = (int)((mc.displayWidth/2 - getWidth())*percentPosX);

        if(getSnappedElement() >= 0)
        {
            HudElement e = (HudElement) SystemManager.getHudModules().toArray()[getSnappedElement()];



            x = (int) (e.getRenderPosX(getPercentPosX()));
        }

        return x;
    }
    public int getRenderPosY() {
        int y = (int)((mc.displayHeight/2 - getHeight())*percentPosY);

        if(mc.currentScreen instanceof GuiChat && percentPosY > 0.5)
            y -= 20;

        if(getSnappedElement() >= 0)
        {
            HudElement e = (HudElement) SystemManager.getHudModules().toArray()[getSnappedElement()];

            y = (int) (e.getRenderPosY(getPercentPosY()));
        }

        return y;
    }
    public int getEndRenderPosX() {
        return getRenderPosX()+getWidth();
    }
    public int getEndRenderPosY() {
        return getRenderPosY()+getHeight();
    }
    public int getCenterRenderPosX() {
        return getRenderPosX()+getWidth()/2;
    }
    public int getCenterRenderPosY() {
        return getRenderPosY()+getHeight()/2;
    }

    public void setRenderPosX(int posX) {
        if(getSnappedElement() >= 0)
        {
            HudElement e = (HudElement) SystemManager.getHudModules().toArray()[getSnappedElement()];
            double x = ((1.0 / e.getWidth()) * (posX-e.getRenderPosX()));
            setPercentPosX(x);

        }
        else
        {
            double w = (mc.displayWidth/2.0 - getWidth());
            setPercentPosX(1.0/w*posX);
        }

    }
    public void setRenderPosY(int posY) {
        if(getSnappedElement() >= 0)
        {
            HudElement e = (HudElement) SystemManager.getHudModules().toArray()[getSnappedElement()];
            double y = ((1.0 / e.getHeight()) * (posY-e.getRenderPosY()));
            setPercentPosY(y);

        }
        else
        {
            double w = (mc.displayHeight/2.0 - getHeight());
            setPercentPosY(1.0/w*posY);
        }

    }



    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }

    private boolean isEnabled = getMod().enabled();




    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public String getName() {
        return name;
    }



    public void toggle(){
        setEnabled(!isEnabled());
    }

    public void setEnabled(boolean enabled) {
        if(enabled)
            enable();
        else
            disable();
    }


    public void enable() {
        if(!this.isEnabled) {
            Spark.eventBus.register(this);

            Spark.logger.info("Enabled " + getName());

            this.isEnabled = true;
            onEnable();
        }
    }

    public void disable() {
        if(this.isEnabled) {
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

    private long Time = System.nanoTime();

    protected boolean isInHudEditor() {
        if(mc.currentScreen instanceof ClickGuiMenuBase)
            return ((ClickGuiMenuBase)mc.currentScreen).getPanel() instanceof HudEditor;
        return false;
    }
    protected boolean isSelectedInHudEditor() {
        if(mc.currentScreen instanceof ClickGuiMenuBase)
        if(((ClickGuiMenuBase)mc.currentScreen).getPanel() instanceof HudEditor)
        {
            return ((HudEditor)((ClickGuiMenuBase)mc.currentScreen).getPanel()).getSelected() == this;
        }
        return false;
    }

    protected boolean alignLeft() {
        return mc.displayWidth/4 > getCenterRenderPosX();
    }

    protected boolean alignTop() {
        return mc.displayHeight/4 > getCenterRenderPosY();
    }


    //this event is good trust me
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Chat event) {
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        float deltaTime = (System.nanoTime()-Time)/1000000f;
        if(isInHudEditor())
            Gui.drawRect(getRenderPosX(), getRenderPosY(), getEndRenderPosX(), getEndRenderPosY(), !isSelectedInHudEditor() ? hudSettings.getGuiHudListBackgroundColor().getRGB() : new Color(47, 47, 47, 160).getRGB());
        else if (shouldDrawBackground()) {
            Gui.drawRect(getRenderPosX(), getRenderPosY(), getEndRenderPosX(), getEndRenderPosY(), hudSettings.getGuiHudListBackgroundColor().getRGB());
        }
        draw(deltaTime);
        Time = System.nanoTime();

        GlStateManager.color(1f,1f,1f,1f);

    }

    public void draw(float deltaTime) {

    }

    public boolean mouseOver(int mouseX, int mouseY) {
        return mouseX > getRenderPosX() && mouseX < getEndRenderPosX() && mouseY > getRenderPosY() && mouseY < getEndRenderPosY();
    }
}

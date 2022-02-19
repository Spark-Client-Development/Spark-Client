package me.wallhacks.spark.gui.clickGui;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.panels.configs.Configs;
import me.wallhacks.spark.gui.clickGui.panels.navigation.NavigationGui;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.gui.panels.GuiPanelScreen;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.systems.module.modules.player.Freecam;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import me.wallhacks.spark.gui.clickGui.panels.hudeditor.HudEditor;
import me.wallhacks.spark.gui.clickGui.panels.mainScreen.SystemsScreen;
import me.wallhacks.spark.gui.clickGui.panels.socials.Socials;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.render.ColorUtil;

public class ClickGuiMenuBase extends GuiPanelScreen {

    public final ClickGuiPanel[] panels = new ClickGuiPanel[]{new SystemsScreen(this),new HudEditor(this),new Socials(this),new Configs(this),new NavigationGui(this)};


    final GuiPanelButton[] menus;


    int selected = 0;
    int switchToSelected = 0;

    double Progress = 0;

    final ClientConfig guiSettings;
    public ClickGuiMenuBase() {
        guiSettings = ClientConfig.getInstance();
        menus = new GuiPanelButton[panels.length];

        for (int i = 0; i < panels.length; i++)
        {
            int finalI = i;
            menus[i] = new GuiPanelButton(() -> {
                    switchToSelected = finalI;
                    panels[switchToSelected].init();
            }
            ,panels[i].getName());
        }
    }




    @Override
    public void initGui() {
        super.initGui();
        menuYPos = hidden;

        mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));

        getPanel().init();

    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        try {
            mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        } catch (NullPointerException e) {
            //ez
        }
    }
    @Override
    public void updateScreen() {
        getPanel().tick();
    }




    double menuYPos = 0;
    int hidden = -40;

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);


        ClickGuiPanel clickGuiPanel = getPanel();
        ClickGuiPanel clickGuiPanelSwitchTo = getPanel(switchToSelected);


        drawRect(0,0,this.width,this.height, ColorUtil.mutiplyAlpha(guiSettings.getGuiScreenBackgroundColor(), (float) MathUtil.lerp(clickGuiPanel.renderBackground() ? 1 : 0,clickGuiPanelSwitchTo.renderBackground() ? 1 : 0,Progress)).getRGB());



        GL11.glPushMatrix();


        if(switchToSelected != selected)
        {
            boolean inVert = switchToSelected > selected;

            GL11.glPushMatrix();
            GL11.glTranslated((inVert ? -1 : 1)*-width+width*Progress*(inVert ? -1 : 1),0,0);

            GuiUtil.addGlScissorOffset(new Vec3d((inVert ? -1 : 1)*-width+width*Progress*(inVert ? -1 : 1),0,0));

            clickGuiPanelSwitchTo.renderContent(MouseX,MouseY,deltaTime);

            GL11.glPopMatrix();

            GuiUtil.addGlScissorOffset(new Vec3d(width*Progress*(inVert ? -1 : 1),0,0));



            GL11.glTranslated(width*Progress*(inVert ? -1 : 1),0,0);


            Progress+=deltaTime*0.005;
            if(Progress >= 1)
            {
                selected = switchToSelected;
                getPanel().init();
            }


        }
        else
            Progress = 0;


        clickGuiPanel.renderContent(MouseX,MouseY,deltaTime);

        GL11.glPopMatrix();
        GuiUtil.resetGlScissorOffset();

        int sizeX = 60;
        int sizeY = 18;
        int spacing = ClientConfig.getInstance().spacing;


        int fullWidth = (sizeX*menus.length+menus.length*spacing-spacing);

        int x = width / 2 - fullWidth / 2;
        int y = spacing + 4;



        menuYPos = MathUtil.lerp(menuYPos,clickGuiPanel.showMenuBar ? 0 : hidden,deltaTime*0.03);
        if(menuYPos-0.4 > hidden)
        {
            GL11.glPushMatrix();
            GlStateManager.translate(0,menuYPos,0);

            drawRect(x-spacing,y-spacing,x+fullWidth+spacing,y+sizeY+spacing, ColorUtil.mutiplyAlpha(guiSettings.getGuiScreenBackgroundColor(), (float) MathUtil.lerp(clickGuiPanel.renderBackground() ? 0 : 1,clickGuiPanelSwitchTo.renderBackground() ? 0 : 1,Progress)).getRGB());

            drawRect(x-spacing,y-spacing,x+fullWidth+spacing,y+sizeY+spacing, ClientConfig.getInstance().getGuiMainPanelBackgroundColor().getRGB());
            int i = 0;
            for (GuiPanelButton button : menus) {

                button.drawLine = (i == switchToSelected);

                button.setPositionAndSize(x,y,sizeX,sizeY);

                button.renderContent(MouseX,MouseY,deltaTime);



                x+=spacing+sizeX;

                i++;
            }
            GL11.glPopMatrix();
        }


    }




    public ClickGuiPanel getPanel(int i) {
        return panels[i];
    }
    public ClickGuiPanel getPanel() {
        return panels[selected];
    }
    public <T extends ClickGuiPanel> T getPanelOfType(Class<T> clazz) {
        for (ClickGuiPanel p : panels) {
            if(p.getClass() == clazz)
                return (T)p;
        }
        return null;
    }




}

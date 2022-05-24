package me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting;

import me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting.guiKitEditor.GuiKitEditor;
import me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting.kitList.GuiKitList;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelButton;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelScreen;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelScroll;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.systems.module.modules.player.InventoryManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class KitSettingGui extends GuiPanelScreen {
    public KitSettingGui(GuiScreen screen) {
        super(screen);

    }

    @Override
    public void initGui() {
        super.initGui();
        mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));


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



    GuiPanelButton backButton = new GuiPanelButton(() -> {close();},lastScreen == null ? "close" : "Back");
    GuiPanelButton addKitButton = new GuiPanelButton(() -> {InventoryManager.instance.createEmptyKit("newKit");},"NewKit");

    GuiKitList kitList = new GuiKitList(this);
    GuiPanelScroll scroll = new GuiPanelScroll(kitList);

    GuiKitEditor kitEditor = new GuiKitEditor();

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);


        ClientConfig guiSettings =  ClientConfig.getInstance();


        drawRect(0,0,this.width,this.height, guiSettings.getGuiScreenBackgroundColor().getRGB());

        int settingHeight = 230;


        int kitEditorWidth = 18*9;

        int settingWidth = kitEditorWidth*2+guiSettings.spacing;

        int nameBarHeight = 18;

        int x = getCenterX()-settingWidth/2;
        int y = getCenterY()-settingHeight/2;

        drawRect(x-4,y-4,x+settingWidth+4,y+settingHeight+4,guiSettings.getGuiMainPanelBackgroundColor().getRGB());


        Gui.drawRect(x,y,x+kitEditorWidth,y+nameBarHeight,guiSettings.getGuiSubPanelBackgroundColor().getRGB());

        fontManager.drawString("Kit List",x+4,y+nameBarHeight/2-fontManager.getTextHeight()/2, guiSettings.getContrastColor().getRGB());


        scroll.drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        scroll.setPositionAndSize(x,y+guiSettings.spacing+nameBarHeight,settingWidth-kitEditorWidth-guiSettings.spacing,settingHeight-guiSettings.spacing-nameBarHeight);
        scroll.renderContent(MouseX, MouseY, deltaTime);


        kitEditor.setPositionAndSize(x+settingWidth-kitEditorWidth,y,kitEditorWidth,settingHeight);

        kitEditor.renderContent(MouseX, MouseY, deltaTime);



        y += settingHeight + 20;

        Gui.drawRect(x-4,y-4,x+settingWidth+4,y+nameBarHeight+4, guiSettings.getGuiMainPanelBackgroundColor().getRGB());

        addKitButton.setPositionAndSize(x,y,kitEditorWidth,nameBarHeight);
        addKitButton.renderContent(MouseX, MouseY, deltaTime);

        backButton.setPositionAndSize(x+kitEditorWidth+guiSettings.spacing,y,kitEditorWidth,nameBarHeight);
        backButton.renderContent(MouseX, MouseY, deltaTime);
    }

    public void setEditKit(String k) {
        kitEditor.setKit(k);
    }


}

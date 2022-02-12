package me.wallhacks.spark.gui.clickGui.panels.configs.configList;

import com.mojang.authlib.GameProfile;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.panels.configs.Configs;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.manager.ConfigManager;
import me.wallhacks.spark.manager.SocialManager;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.SessionUtils;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.awt.*;
import java.util.UUID;

public class ConfigListItem extends GuiPanelBase {


    Configs configs;
    ConfigManager.Config config;
    public ConfigListItem(ConfigManager.Config config,Configs configs) {
        this.config = config;
        this.configs = configs;
    }

    GuiPanelButton EditButton = new GuiPanelButton(() -> {configs.EditConfig(config);},"Edit");
    GuiPanelButton DeleteButton = new GuiPanelButton(() -> {configs.DeleteConfig(config);},"Delete");
    GuiPanelButton LoadButton = new GuiPanelButton(() -> {configs.LoadConfig(config);},"Load");

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);



        super.renderContent(MouseX, MouseY, deltaTime);








        int FieldSizeY = 14;

        int xp = fontManager.drawString(config.getConfigName(),posX,posY+4, guiSettings.getContrastColor().getRGB());

        if(Spark.configManager.getCurrentConfig() == config)
            fontManager.drawString(ChatFormatting.ITALIC +"[Loaded]",xp+5,posY+4, guiSettings.getContrastColor().getRGB());


        int y = posY+4+ fontManager.getTextHeight()/2-FieldSizeY/2;

        int x = posX + width;

        int FieldSizeX = fontManager.getTextWidth(DeleteButton.getText())+6;
        x-=FieldSizeX+4;

        DeleteButton.setPositionAndSize(x,y,FieldSizeX,FieldSizeY);
        DeleteButton.setOverrideColor(guiSettings.getGuiSettingFieldColor());
        DeleteButton.renderContent(MouseX,MouseY,deltaTime);

        FieldSizeX = fontManager.getTextWidth(LoadButton.getText())+6;
        x-=FieldSizeX+4;

        LoadButton.setPositionAndSize(x,y,FieldSizeX,FieldSizeY);
        LoadButton.setOverrideColor(guiSettings.getMainColor());
        LoadButton.renderContent(MouseX,MouseY,deltaTime);

        FieldSizeX = fontManager.getTextWidth(EditButton.getText())+6;
        x-=FieldSizeX+4;

        EditButton.setPositionAndSize(x,y,FieldSizeX,FieldSizeY);
        EditButton.setOverrideColor(guiSettings.getGuiSettingFieldColor());
        EditButton.renderContent(MouseX,MouseY,deltaTime);



        height = 8 + fontManager.getTextHeight();



    }
}
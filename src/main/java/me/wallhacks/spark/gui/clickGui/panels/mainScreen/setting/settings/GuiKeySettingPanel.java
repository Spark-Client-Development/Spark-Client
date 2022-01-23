package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings;

import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiSettingPanel;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import me.wallhacks.spark.systems.setting.settings.KeySetting;

public class GuiKeySettingPanel extends GuiSettingPanel<KeySetting> {

    public GuiKeySettingPanel(KeySetting setting) {
        super(setting);


    }



    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        String s = "Bind: "+(waitingForKey ? "..." : getSetting().getKeyName());

        int FieldSizeX = fontManager.getTextWidth(s)+6;




        int FieldSizeY = 14;

        fontManager.drawString(getSetting().getName(),posX,posY+4, guiSettings.getContrastColor().getRGB());

        int y = posY+4+ fontManager.getTextHeight()/2-FieldSizeY/2;

        int x = posX + width - FieldSizeX;


        Gui.drawRect(x,y,x+FieldSizeX,y+FieldSizeY, guiSettings.getGuiSettingFieldColor().getRGB());




        fontManager.drawString(s,x+FieldSizeX/2-fontManager.getTextWidth(s)/2,y+FieldSizeY/2-fontManager.getTextHeight()/2, guiSettings.getContrastColor().getRGB());



        height = 6 +  fontManager.getTextHeight();



        if(!isFocused())
            waitingForKey = false;
        else if(!waitingForKey)
            setFocused(false);

    }

    boolean waitingForKey = false;

    @Override
    public void onClickDown(int MouseButton, int MouseX, int MouseY) {
        super.onClickDown(MouseButton, MouseX, MouseY);
        if(waitingForKey)
            key(-2-MouseButton);
        else
            waitingForKey = true;
    }

    @Override
    public void onKey(int KeyCode, char TypedChar) {
        super.onKey(KeyCode, TypedChar);

        if(waitingForKey)
            key(KeyCode);
    }

    void key(int k){
        setFocused(false);
        waitingForKey = false;
        if(k == Keyboard.KEY_DELETE)
            getSetting().setKey(Keyboard.KEY_NONE);
        else if(k != Keyboard.KEY_ESCAPE)
            getSetting().setKey(k);

    }
}

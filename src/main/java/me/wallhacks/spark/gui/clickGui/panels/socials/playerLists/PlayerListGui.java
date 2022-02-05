package me.wallhacks.spark.gui.clickGui.panels.socials.playerLists;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.panels.socials.Socials;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;

public class PlayerListGui extends GuiPanelBase {

    public final GuiPanelInputField moduleSearchField = new GuiPanelInputField(0,0,0,0,0);

    public final Socials socials;



    public PlayerListGui(Socials socials) {
        this.socials = socials;

    }





    public void renderContent(int MouseX, int MouseY, float deltaTime) {


        super.renderContent(MouseX,MouseY,deltaTime);



        boolean isFriend = (socials.playerFriendListGui == this);

        int spacing = ClientConfig.spacing;

        int h = spacing;
        for (PlayerListItem item : socials.players) {

            if(!isFriend){
                if(!item.getName().toLowerCase().contains(socials.moduleSearchField.getText().toLowerCase()) || Spark.socialManager.isFriend(item.playerId))
                    continue;
            }
            else if (!Spark.socialManager.isFriend(item.playerId))
                continue;

            item.setPositionAndSize(posX+spacing,posY+h,width-spacing*2,18);
            item.renderContent(MouseX,MouseY,deltaTime);

            String buttonText = isFriend ? "Remove" : "Add";
            int tw = fontManager.getTextWidth(buttonText);

            drawRect(item.posX+item.width-tw-6,item.posY+2,item.posX+item.width-2,item.posY+item.height-2,guiSettings.getGuiSettingFieldColor().getRGB());

            fontManager.drawString(buttonText,item.posX+item.width-tw-4,item.posY+item.height/2-fontManager.getTextHeight()/2,isFriend ? guiSettings.getMainColor().getRGB() : guiSettings.getContrastColor().getRGB());

            h += 18 + 2;
        }
        h+=20;

        height = h;





    }









}

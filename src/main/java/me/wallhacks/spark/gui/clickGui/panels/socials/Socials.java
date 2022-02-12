package me.wallhacks.spark.gui.clickGui.panels.socials;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.ClickGuiMenuBase;
import me.wallhacks.spark.gui.clickGui.ClickGuiPanel;
import me.wallhacks.spark.gui.clickGui.panels.socials.playerLists.PlayerListItem;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.gui.panels.GuiPanelScroll;
import me.wallhacks.spark.manager.SocialManager;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import me.wallhacks.spark.gui.clickGui.panels.socials.playerLists.PlayerListGui;
import me.wallhacks.spark.util.SessionUtils;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class Socials extends ClickGuiPanel {


    public Socials(ClickGuiMenuBase clickGuiMenuBase) {
        super(clickGuiMenuBase);
    }

    @Override
    public String getName() {
        return "Socials";
    }


    @Override
    public void init() {
        super.init();


    }


    @Override
    public void tick() {
        updateList();

    }

    public final GuiPanelInputField moduleSearchField = new GuiPanelInputField(0,0,0,0,0);


    final ResourceLocation searchIcon = new ResourceLocation("textures/icons/searchicon.png");

    public final PlayerListGui playerListGui = new PlayerListGui(this);
    public final PlayerListGui playerFriendListGui = new PlayerListGui(this);
    public final GuiPanelScroll guiPanelScroll = new GuiPanelScroll(0, 0, 0, 0,playerListGui);
    public final GuiPanelScroll guiPanelScrollFrieds = new GuiPanelScroll(0, 0, 0, 0,playerFriendListGui);

    public final GuiPanelButton addButton = new GuiPanelButton(() -> {
        if(moduleSearchField.getText().length() > 0)
        {
            Spark.socialManager.addFriend(moduleSearchField.getText());

        }

    }, "Add Offline");

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {

        super.renderContent(MouseX,MouseY,deltaTime);

        int ListWidth = 190;
        int height = 238;

        int width = ListWidth + ListWidth + guiSettings.spacing;

        int x = getCenterX()-width/2;
        int y = getCenterY()-height/2;

        //gui background
        Gui.drawRect(x-4,y-4,x+width+4,y+height+4, guiSettings.getGuiMainPanelBackgroundColor().getRGB());





        int searchFieldHeight = 18;



        Gui.drawRect(x,y,x+ListWidth,y+searchFieldHeight,guiSettings.getGuiSubPanelBackgroundColor().getRGB());

        fontManager.drawString("Add Players",x+4,y+searchFieldHeight/2-fontManager.getTextHeight()/2, guiSettings.getContrastColor().getRGB());



        Gui.drawRect(x+ListWidth+guiSettings.spacing,y,x+ListWidth*2+guiSettings.spacing,y+searchFieldHeight,guiSettings.getGuiSubPanelBackgroundColor().getRGB());

        fontManager.drawString("Your Friends",x+ListWidth+guiSettings.spacing+4,y+searchFieldHeight/2-fontManager.getTextHeight()/2, guiSettings.getContrastColor().getRGB());


        y += searchFieldHeight + guiSettings.spacing;

        guiPanelScroll.setPositionAndSize(x,y,ListWidth,height-searchFieldHeight-guiSettings.spacing);
        guiPanelScroll.drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        guiPanelScroll.renderContent(MouseX,MouseY,deltaTime);


        x += ListWidth + guiSettings.spacing;

        guiPanelScrollFrieds.setPositionAndSize(x,y,ListWidth,height-searchFieldHeight-guiSettings.spacing);
        guiPanelScrollFrieds.drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        guiPanelScrollFrieds.renderContent(MouseX,MouseY,deltaTime);







        x = getCenterX()-width/2;
        y += height+guiSettings.spacing;


        Gui.drawRect(x-4,y-4,x+width+4,y+18+4, guiSettings.getGuiMainPanelBackgroundColor().getRGB());


        moduleSearchField.setBackGroundColor(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        moduleSearchField.setTextOffsetX(searchFieldHeight);
        moduleSearchField.setPositionAndSize(x,y,ListWidth,searchFieldHeight);
        moduleSearchField.renderContent(MouseX,MouseY,deltaTime);

        GuiUtil.drawCompleteImage(x+3,y+3, searchFieldHeight-6, searchFieldHeight-6,searchIcon, guiSettings.getContrastColor());


        x += ListWidth + guiSettings.spacing;

        addButton.setOverrideColor(guiSettings.getGuiSubPanelBackgroundColor());
        addButton.setPositionAndSize(x,y,ListWidth,searchFieldHeight);
        addButton.renderContent(MouseX,MouseY,deltaTime);




        for (PlayerListItem item : players)
            if(item.isSelected())
            {
                if(Spark.socialManager.isFriend(item.player))
                    Spark.socialManager.removeFriend(item.player);
                else
                    Spark.socialManager.addFriend(item.player);
                GuiPanelBase.SelectedMouse = null;
            }

    }



    public CopyOnWriteArrayList<PlayerListItem> players = new CopyOnWriteArrayList<>();


    String lastSearchText = "";
    int ticksSinceTextNotChange = 0;
    public void updateList() {


        String searchText = moduleSearchField.getText();


        boolean isFound = false;


        friendCheck:
        for (SocialManager.SocialEntry item : Spark.socialManager.getFriends())
        {
            for (PlayerListItem t : players)
            {
                if(t.getName().equalsIgnoreCase(item.getName()))
                    continue friendCheck;
            }

            players.add(new PlayerListItem(item));

        }

        playerMaploop:
        for (NetworkPlayerInfo s : Minecraft.getMinecraft().player.connection.getPlayerInfoMap())
        {
            if(searchText.equalsIgnoreCase(s.getGameProfile().getName()))
                isFound = true;

            for (PlayerListItem t : players)
            {
                if(t.getName().equalsIgnoreCase(s.gameProfile.getName()))
                    continue playerMaploop;
            }

            players.add(new PlayerListItem(s));

        }

        for (PlayerListItem item : players)
        {
            if(searchText.equalsIgnoreCase(item.getName()))
            {
                isFound = true;
            }
        }

        if(lastSearchText.equalsIgnoreCase(searchText))
            ticksSinceTextNotChange++;
        else
            ticksSinceTextNotChange = 0;

        if(searchText.length() > 0)
        {

            if(ticksSinceTextNotChange == 10 && !isFound)
            {

                Spark.threadManager.execute(() -> {
                    Spark.logger.info(searchText);
                    UUID uuid = SessionUtils.getid(searchText);
                    Spark.logger.info(uuid);
                    if(uuid != null) {
                        players.add(new PlayerListItem(new SocialManager.UUIDSocial(uuid)));
                    }
                });
            }
        }


        lastSearchText = moduleSearchField.getText();
    }
}

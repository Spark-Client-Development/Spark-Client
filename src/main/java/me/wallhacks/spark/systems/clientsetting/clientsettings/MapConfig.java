package me.wallhacks.spark.systems.clientsetting.clientsettings;

import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.ClickGuiTheme;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.guithemes.Classic;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.guithemes.Light;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.guithemes.Opaque;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.objects.MCStructures;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Arrays;

@ClientSetting.Registration(name = "ClientConfig", description = "Config for map settings")
public class MapConfig extends ClientSetting {


    public BooleanSetting SaveMap = new BooleanSetting("UseFileSystem", this, false, "MapManager");

    public BooleanSetting Structures = new BooleanSetting("ShowStructures", this, true, "MapManager");



    public StructureListSelectSetting StructureList = new StructureListSelectSetting("ShowList", this, new MCStructures[] {
        MCStructures.Stronghold,MCStructures.Igloo,MCStructures.EndCity,MCStructures.WitchHut,MCStructures.DesertTemple,MCStructures.JungleTemple,MCStructures.Mansion,MCStructures.NetherFortress,MCStructures.OceanMonument,MCStructures.Village
    }, v -> Structures.isOn(),"MapManager");


    public static MapConfig INSTANCE;


    public MapConfig() {
        super();
        INSTANCE = this;
    }

    public static MapConfig getInstance(){
        return INSTANCE;
    }
}

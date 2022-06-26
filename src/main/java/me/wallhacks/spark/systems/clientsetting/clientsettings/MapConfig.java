package me.wallhacks.spark.systems.clientsetting.clientsettings;

import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.objects.MCStructures;

@ClientSetting.Registration(name = "ClientConfig", description = "Config for map settings")
public class MapConfig extends ClientSetting {


    public BooleanSetting SaveMap = new BooleanSetting("UseFileSystem", this, false);

    public BooleanSetting Structures = new BooleanSetting("ShowStructures", this, true);



    public StructureListSelectSetting StructureList = new StructureListSelectSetting("ShowList", this, new MCStructures[] {
        MCStructures.Stronghold,MCStructures.Igloo,MCStructures.EndCity,MCStructures.WitchHut,MCStructures.DesertTemple,MCStructures.JungleTemple,MCStructures.Mansion,MCStructures.NetherFortress,MCStructures.OceanMonument,MCStructures.Village
    }, v -> Structures.isOn());


    public static MapConfig INSTANCE;


    public MapConfig() {
        super();
        INSTANCE = this;
    }

    public static MapConfig getInstance(){
        return INSTANCE;
    }
}

package me.wallhacks.spark.systems.clientsetting.clientsettings;

import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

import java.util.Arrays;

@ClientSetting.Registration(name = "AntiCheatConfig", description = "Anti cheat config")
public class AntiCheatConfig extends ClientSetting {

    DoubleSetting BlockPlaceRange = new DoubleSetting("BlockRange",this,4,0,5,0.25,"Blocks");
    DoubleSetting BlockPlaceWallRange = new DoubleSetting("BlockWallRange",this,0,0,5,0.25,"Blocks");
    BooleanSetting BlockStrictRayTrace = new BooleanSetting("NeedsToSeeFace",this,false,"Blocks");

    BooleanSetting BlockRotate = new BooleanSetting("BlockRotate",this,true,"Blocks");
    IntSetting BlockRotStep = new IntSetting("BlockRotStep",this,180,45,180,"Blocks");
    public ModeSetting PlaceSwing = new ModeSetting("PlaceSwing",this,"Normal", Arrays.asList("Off","Normal","Packet"), "Blocks");
    public double getBlockPlaceRange() {
        return BlockPlaceRange.getValue();
    }
    public double getBlockPlaceWallRange() {
        return BlockPlaceWallRange.getValue();
    }
    public boolean getBlockStrictRayTrace() {
        return BlockStrictRayTrace.getValue();
    }
    public boolean getBlockRotate() {
        return BlockRotate.getValue();
    }
    public int getBlockRotStep() {
        return BlockRotStep.getValue();
    }


    public ModeSetting attackSwing = new ModeSetting("AttackSwing",this,"Normal" ,  Arrays.asList("Off","Normal","Packet"), "Attack");

    DoubleSetting crystalPlaceRange = new DoubleSetting("CrystalPlaceRange",this,4,1,6,0.25, "Crystals");
    DoubleSetting crystalBreakRange = new DoubleSetting("CrystalBreakRange",this,4,1,6,0.25, "Crystals");
    DoubleSetting crystalWallRange = new DoubleSetting("CrystalWallRange",this,0,0,6,0.25, "Crystals");
    public ModeSetting crystalBreakHand = new ModeSetting("BreakHand",this,"Both", Arrays.asList("Both","MainHand","OffHand"), "Crystals");

    public ModeSetting crystalBreakSwing = new ModeSetting("BreakSwing",this,"Normal", Arrays.asList("Off","Normal","Packet"), "Crystals");
    public ModeSetting crystalPlaceSwing = new ModeSetting("CrystalPlaceSwing",this,"Off", Arrays.asList("Off","Normal","Packet"), "Crystals");
    BooleanSetting crystalRotate = new BooleanSetting("CrystalRotate",this,true,"Crystals");
    IntSetting crystalRotStep = new IntSetting("CrystalRotStep",this,180,45,180,"Crystals");


    public double getCrystalPlaceRange() {
        return crystalPlaceRange.getValue();
    }
    public double getCrystalBreakRange() {
        return crystalBreakRange.getValue();
    }
    public double getCrystalWallRange() {
        return crystalWallRange.getValue();
    }
    public boolean CrystalRotate() {
        return crystalRotate.getValue();
    }
    public int getCrystalRotStep() {
        return crystalRotStep.getValue();
    }
    public static AntiCheatConfig INSTANCE;
    public AntiCheatConfig() {
        INSTANCE = this;
    }

    public static AntiCheatConfig getInstance(){
        return INSTANCE;
    }
}

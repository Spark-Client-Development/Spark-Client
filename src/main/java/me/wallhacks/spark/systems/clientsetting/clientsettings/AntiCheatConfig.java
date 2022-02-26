package me.wallhacks.spark.systems.clientsetting.clientsettings;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Arrays;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

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

    ModeSetting switchingMode = new ModeSetting("PlaceSwitch", this, "Normal",  Arrays.asList("Normal","Silent","Const"),"Blocks");


    public ModeSetting attackSwing = new ModeSetting("AttackSwing",this,"Normal" ,  Arrays.asList("Off","Normal","Packet"), "Attack");

    DoubleSetting crystalPlaceRange = new DoubleSetting("CrystalPlaceRange",this,4,1,6,0.25, "Crystals");
    DoubleSetting crystalBreakRange = new DoubleSetting("CrystalBreakRange",this,4,1,6,0.25, "Crystals");
    DoubleSetting crystalWallRange = new DoubleSetting("CrystalWallRange",this,0,0,6,0.25, "Crystals");
    public ModeSetting crystalBreakHand = new ModeSetting("BreakHand",this,"Both", Arrays.asList("Both","MainHand","OffHand"), "Crystals");
    public ModeSetting antiWeakness = new ModeSetting("AntiWeakness",this,"Normal", Arrays.asList("Silent","Normal","Off"), "Crystals");

    public ModeSetting crystalBreakSwing = new ModeSetting("BreakSwing",this,"Normal", Arrays.asList("Off","Normal","Packet"), "Crystals");
    public ModeSetting crystalPlaceSwing = new ModeSetting("CrystalPlaceSwing",this,"Off", Arrays.asList("Off","Normal","Packet"), "Crystals");
    BooleanSetting crystalRotate = new BooleanSetting("CrystalRotate",this,true,"Crystals");
    IntSetting crystalRotStep = new IntSetting("CrystalRotStep",this,180,45,180,"Crystals");


    public DoubleSetting tickAdjustment = new DoubleSetting("TickAdjustment", this, 0.95D, 0.1D, 2.0D, "AEF");
    public IntSetting maxPacketFlyLevels = new IntSetting("MaxPacketFlyLevels", this, 25, 1, 200, "AEF");
    public IntSetting packetFlyTicks = new IntSetting("PacketFlyTicks", this, 25, 1, 1000, "AEF");

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
        Spark.eventBus.register(this);
        INSTANCE = this;
    }

    public static AntiCheatConfig getInstance(){
        return INSTANCE;
    }

    public ItemSwitcher.switchType getBlockPlaceSwitchType() {
        return Spark.switchManager.getModeFromString(switchingMode.getValue());
    }


    private final List<SocketAddress> knownAEFServers = new ArrayList<>();
    private final List<Float> packetFlyLevels = new ArrayList<>();

    private boolean expectAEF;

    private boolean isPacketFlyLimited;
    private boolean wasPacketFlyLimited;

    @SubscribeEvent
    public synchronized void onUpdate(PlayerUpdateEvent event) {
        if (mc.currentServerData != null && mc.player != null && mc.world != null) {
            expectAEF = !mc.player.isElytraFlying() && !mc.player.isRiding() && mc.world.getBlockState(new BlockPos(mc.player.getPosition()).down()).getBlock().equals(Blocks.AIR);

            wasPacketFlyLimited = isPacketFlyLimited;
            isPacketFlyLimited = packetFlyLevels.size() >= maxPacketFlyLevels.getValue();

            List<Float> toRemove = new ArrayList<>();

            for (int index = 0; index < packetFlyLevels.size(); index++) {
                float level = packetFlyLevels.get(index) + tickAdjust(tickAdjustment.getFloatValue());
                packetFlyLevels.set(index, level);
                if (level >= packetFlyTicks.getValue()) toRemove.add(level);
            }

            toRemove.forEach(packetFlyLevels::remove);
        }
    }

    @SubscribeEvent
    public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        packetFlyLevels.clear();
    }

    @SubscribeEvent
    public void onLeaveServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        packetFlyLevels.clear();
    }

    @SubscribeEvent
    public synchronized boolean onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketConfirmTeleport && !isPacketFlyLimited() && expectAEF) packetFlyLevels.add(0.0f);
        return false;
    }

    private float tickAdjust(float ticks) {
        return ticks * Spark.tickManager.getTickRate()/20;
    }

    /**
     * @return The current packetfly levels.
     */
    public int getPacketFlyLevels() {
        return packetFlyLevels.size();
    }

    /**
     * @return Whether the AEF packetfly limit has taken effect.
     */
    public boolean isPacketFlyLimited() {
        return isPacketFlyLimited;
    }

    /**
     * @return Whether we were AEF packetfly limited last tick.
     */
    public boolean wasPacketFlyLimited() {
        return wasPacketFlyLimited;
    }
}

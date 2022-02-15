package me.wallhacks.spark.systems.module.modules.world;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.render.NameTags;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.util.render.EspUtil;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Module.Registration(name = "LogoutSpots", description = "Renders logout spots for enemy", alwaysListening = true)
public class LogoutSpots extends Module {
    ColorSetting color = new ColorSetting("Color", this, new Color(0x7E2854B3, true));

    static CopyOnWriteArrayList<LogoutSpot> spots = new CopyOnWriteArrayList<>();

    public static ArrayList<LogoutSpot> getLogoutSpots() {
        return new ArrayList<>(spots);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            SPacketPlayerListItem packet = event.getPacket();
            if (packet.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                for (SPacketPlayerListItem.AddPlayerData d : packet.getEntries()) {
                    EntityPlayer entityPlayer = mc.world.getPlayerEntityByUUID(d.getProfile().getId());
                    if (entityPlayer != null) {
                        spots.add(new LogoutSpot(entityPlayer.getEntityBoundingBox(), entityPlayer.getUniqueID(), entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount(), entityPlayer.getName()));
                    }
                }
            } else if (packet.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
                for (SPacketPlayerListItem.AddPlayerData d : packet.getEntries()) {
                    for (LogoutSpot spot : spots) {
                        if (spot.uuid.toString().equals(d.getProfile().getId().toString())) {
                            spots.remove(spot);
                            break;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if (this.isEnabled())
            for (LogoutSpot spot : spots) {
                Vec3d center = spot.box.getCenter();
                EspUtil.boundingESPBox(spot.box, color.getColor(), 3);
                GL11.glPushMatrix();
                RenderUtil.glBillboardDistanceScaled((float) center.x, (float) (center.y + 1.2), (float) center.z, mc.player, 1);
                String s = spot.name + " " + NameTags.getHealthText((float) spot.hp) + "HP";
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                Spark.fontManager.drawString(s, -Spark.fontManager.getTextWidth(s)/2, 0, 0x585858);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glPopMatrix();
            }
    }

    private class LogoutSpot {
        double hp;
        UUID uuid;
        AxisAlignedBB box;
        String name;

        LogoutSpot(AxisAlignedBB box, UUID uuid, double hp, String name) {
            this.box = box;
            this.uuid = uuid;
            this.hp = hp;
            this.name = name;
        }

        public AxisAlignedBB getBox() {
            return box;
        }

        public double getHp() {
            return hp;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }
    }
}

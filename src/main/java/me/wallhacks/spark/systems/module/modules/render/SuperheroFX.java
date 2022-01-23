package me.wallhacks.spark.systems.module.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

import java.awt.*;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@Module.Registration(name = "SuperheroFX", description = "Draws sexy hiteffects")
public class SuperheroFX extends Module {
    DoubleSetting delay = new DoubleSetting("Delay", this, 1.0, 0.0, 10.0);
    DoubleSetting scale = new DoubleSetting("Scale", this, 1.5, 0.0, 5.0);
    IntSetting extra = new IntSetting("Extra", this, 1, 0, 5);
    BooleanSetting randomColor = new BooleanSetting("RandomColor", this, true);
    ColorSetting colourSetting = new ColorSetting("Color", this, new Color(50, 120, 230, 255),v -> !randomColor.isOn());
    private List<PopupText> popTexts = new CopyOnWriteArrayList<>();
    private final Random rand = new Random();
    private final Timer timer = new Timer();
    private static final String[] superHeroTextsBlowup = new String[]{"KABOOM", "BOOM", "POW", "KAPOW", "KABLEM"};
    private static final String[] superHeroTextsDamageTaken = new String[]{"OUCH", "ZAP", "BAM", "WOW", "POW", "SLAP"};



    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        this.popTexts.removeIf(PopupText::isMarked);
        this.popTexts.forEach(PopupText::Update);
    }


    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        MC.mc.getRenderManager();
        if (MC.mc.getRenderManager().options != null) {

            this.popTexts.forEach(pop -> {
                GlStateManager.pushMatrix();
                RenderUtil.glBillboardDistanceScaled((float) pop.pos.x, (float) pop.pos.y, (float) pop.pos.z, MC.mc.player, scale.getFloatValue());
                GlStateManager.disableDepth();
                GlStateManager.translate(-((double) Spark.fontManager.getBadaboom().getStringWidth(pop.getDisplayName()) / 2.0), 0.0, 0.0);
                Spark.fontManager.getBadaboom().drawText(pop.getDisplayName(), 0, 0, pop.color);

                //added this line to not fuck up item rendering
                GlStateManager.enableDepth();

                GlStateManager.popMatrix();
            });
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (MC.mc.player == null || MC.mc.world == null) return;
        try {
            if (event.getPacket() instanceof SPacketExplosion) {
                SPacketExplosion packet = (SPacketExplosion) event.getPacket();
                if (MC.mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) < 20.0 && this.timer.passedMs((long) (this.delay.getValue() * 1000.0f))) {
                    this.timer.reset();
                    int len = rand.nextInt(extra.getValue());
                    for (int i = 0; i <= len; i++) {
                        Vec3d pos = new Vec3d(packet.getX() + rand.nextInt(4) - 2, packet.getY() + rand.nextInt(2), packet.getZ() + rand.nextInt(4) - 2);
                        PopupText popupText = new PopupText(ChatFormatting.ITALIC + SuperheroFX.superHeroTextsBlowup[this.rand.nextInt(SuperheroFX.superHeroTextsBlowup.length)], pos);
                        popTexts.add(popupText);
                    }
                }
            } else if (event.getPacket() instanceof SPacketEntityStatus) {
                SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
                if (MC.mc.world != null) {
                    Entity e = packet.getEntity((World) MC.mc.world);
                    if (packet.getOpCode() == 35) {
                        if (MC.mc.player.getDistance(e) < 20.0f) {
                            PopupText popupText = new PopupText(ChatFormatting.ITALIC + "POP", e.getPositionVector().add((double) (this.rand.nextInt(2) / 2), 1.0, (double) (this.rand.nextInt(2) / 2)));
                            popTexts.add(popupText);
                        }
                    } else if (packet.getOpCode() == 2) {
                        if (MC.mc.player.getDistance(e) < 20.0f & e != MC.mc.player) {
                            if (this.timer.passedMs((long) (this.delay.getValue() * 1000.0f))) {
                                this.timer.reset();
                                int len = rand.nextInt((int)Math.ceil(extra.getValue()/2.0));
                                for (int i = 0; i <= len; i++) {
                                    Vec3d pos = new Vec3d(e.posX + rand.nextInt(2) - 1, e.posY + rand.nextInt(2) - 1, e.posZ + rand.nextInt(2) - 1);
                                    PopupText popupText = new PopupText(ChatFormatting.ITALIC + SuperheroFX.superHeroTextsDamageTaken[this.rand.nextInt(SuperheroFX.superHeroTextsBlowup.length)], pos);
                                    popTexts.add(popupText);
                                }
                            }
                        }
                    }
                }
            } else if (event.getPacket() instanceof SPacketDestroyEntities) {
                SPacketDestroyEntities packet = (SPacketDestroyEntities) event.getPacket();
                final int[] array = packet.getEntityIDs();
                for (int i = 0; i < array.length - 1; i++) {
                    int id = array[i];
                    try {
                    	//wtf is this?
                        if (MC.mc.world.getEntityByID(id) == null) continue;
                    } catch (ConcurrentModificationException exception) {
                        return;
                    }
                    Entity e = MC.mc.world.getEntityByID(id);
                    if (e != null && e.isDead) {
                        if ((MC.mc.player.getDistance(e) < 20.0f & e != MC.mc.player) && e instanceof EntityPlayer) {
                            for (int t = 0; t <= rand.nextInt(extra.getValue()); t++) {
                                Vec3d pos = new Vec3d(e.posX + rand.nextInt(2) - 1, e.posY + rand.nextInt(2) - 1, e.posZ + rand.nextInt(2) - 1);
                                PopupText popupText = new PopupText(ChatFormatting.ITALIC + "" + ChatFormatting.BOLD + "EZ", pos);
                                popTexts.add(popupText);
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException ignoredlel) {
            //rreee empty catch block
        }
    }


    class PopupText {
        private String displayName;
        private Vec3d pos;
        private boolean markedToRemove;
        private int color;
        private Timer timer;
        private double yIncrease;

        public PopupText(final String displayName, final Vec3d pos) {
            this.timer = new Timer();
            this.yIncrease = Math.random();
            while (this.yIncrease > 0.025 || this.yIncrease < 0.011) {
                this.yIncrease = Math.random();
            }
            this.timer.reset();
            this.setDisplayName(displayName);
            this.pos = pos;
            this.markedToRemove = false;
            if (!randomColor.getValue()) {
                this.color = colourSetting.getColor().getRGB();
            } else {
                this.color = Color.getHSBColor(rand.nextFloat(), 1.0F, 0.9F).getRGB();
            }
        }

        public void Update() {
            this.pos = this.pos.add(0.0, this.yIncrease, 0.0);
            if (this.timer.passedMs(1000)) {
                this.markedToRemove = true;
            }
        }

        public boolean isMarked() {
            return this.markedToRemove;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public void setDisplayName(final String displayName) {
            this.displayName = displayName;
        }

        public int getColor() {
            return this.color;
        }
    }
}
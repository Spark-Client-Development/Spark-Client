package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PlayerMoveEvent;
import me.wallhacks.spark.event.player.PlayerPreUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraft.entity.MoverType;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

import java.util.Arrays;

@Module.Registration(name = "Speed", description = "Fast module go brrr")
public class Speed extends Module {
    ModeSetting mode = new ModeSetting("Mode", this, "Vanilla", Arrays.asList("Vanilla", "Strafe", "StrictNCP"));
    BooleanSetting useSpeed = new BooleanSetting("UseSpeed", this, false);
    BooleanSetting useJumpBoost = new BooleanSetting("UseJumpBoost", this, false);
    BooleanSetting liquids = new BooleanSetting("Liquids", this, false);
    BooleanSetting boost = new BooleanSetting("Boost", this, false, "Boost");
    IntSetting boostTicks = new IntSetting("BoostTicks", this, 5, 1, 40, "Boost");
    DoubleSetting boostSpeed = new DoubleSetting("BoostSpeed", this, 0.1, 0.0, 2.0, "Boost");
    BooleanSetting boostStop = new BooleanSetting("BoostStop", this, false, "Boost");
    public static Speed INSTANCE;

    public Speed() {
        INSTANCE = this;
    }

    private boolean prevOnGround;
    private int jumps;
    private int offGroundTicks;
    private boolean boostable;
    private int boostTick;

    @SubscribeEvent
    public void onUpdate(PlayerPreUpdateEvent event) {
        if (fullCheck()) {
            Vec3d velocity = getVelocity();

            if (this.mc.player.onGround && !this.prevOnGround && (this.mc.player.moveForward != 0.0 || this.mc.player.moveStrafing != 0.0)) {
                MC.mc.player.setSprinting(true);
                ++this.jumps;
                if (boostStop.getValue()) boostable = false;
                velocity = new Vec3d(velocity.x, 0.405f, velocity.z);
                PotionEffect jumpBoostEffect = this.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST);
                if (useJumpBoost.getValue() && this.mc.player.isPotionActive(MobEffects.JUMP_BOOST) && jumpBoostEffect != null) {
                    velocity = velocity.add(new Vec3d(0.0, ((jumpBoostEffect.getAmplifier() + 1) * 0.1f), 0.0));
                }
            }
            if (!this.mc.player.onGround) {
                ++this.offGroundTicks;
            } else if (!this.prevOnGround) {
                this.offGroundTicks = 0;
            }
            if (velocity.x * velocity.x + velocity.z * velocity.z > 1.0E-4 && (this.mc.player.moveForward != 0.0 || this.mc.player.moveStrafing != 0.0)) {
                this.prevOnGround = this.mc.player.onGround;
            } else {
                this.prevOnGround = false;
                this.jumps = 0;
                boostable = false;
            }
            MC.mc.player.setVelocity(velocity.x, velocity.y, velocity.z);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            jumps = 0;
            boostable = false;
            this.prevOnGround = false;
        }
        if (event.getPacket() instanceof SPacketExplosion && boost.getValue()) {
            SPacketExplosion packet = (SPacketExplosion) event.getPacket();
            if (Math.abs(packet.motionX) > 0.1 || Math.abs(packet.motionZ) > 0.1) {
                boostTick = 0;
                boostable = true;
            }
            packet.motionY = 0;
        }
    }

    @SubscribeEvent
    public void onMove(PlayerMoveEvent event) {
        if (fullCheck() && event.getType() == MoverType.SELF) {
            double forwardSpeed = this.mc.player.moveForward;
            double strafeSpeed = this.mc.player.moveStrafing;
            double rotationYaw = this.mc.player.rotationYaw;
            Vec3d velocity = getVelocity();
            float playerSpeed = 0.2873f;
            if (!mode.is("Strict")) {
                if (this.mc.player.onGround) {
                    if (this.jumps > 1) {
                        playerSpeed *= 1.9318f;
                    }
                    if (this.jumps > 2) {
                        playerSpeed *= 1.0541f;
                    }
                    if (this.jumps > 3) {
                        playerSpeed *= 1.0256f;
                    }
                    if (this.jumps > 4) {
                        playerSpeed *= 1.0167f;
                    }
                } else {
                    if (this.jumps > 1) {
                        playerSpeed *= 1.2008f - 0.0139f * this.offGroundTicks;
                    }
                    if (this.jumps > 2) {
                        playerSpeed *= 1.029f;
                    }
                    if (this.jumps > 3) {
                        playerSpeed *= 1.0141f;
                    }
                    if (mode.is("Fast")) {
                        if (this.jumps > 2) {
                            playerSpeed *= 1.0139f;
                        }
                        if (this.jumps > 4) {
                            playerSpeed *= 1.0274f;
                        }
                        if (this.jumps > 6) {
                            playerSpeed *= 1.0267f;
                        }
                        if (this.jumps > 8) {
                            playerSpeed *= 1.026f;
                            this.jumps = 3;
                        }
                    }
                }
                if (playerSpeed < 0.2873f) {
                    playerSpeed = 0.2873f;
                }
            }
            if (boostable) {
                if (boostTick < boostTicks.getValue()) {
                    float boost;
                    int t = (boostTicks.getValue() / 3);
                    if (boostTick > t * 2) {
                        int d = t - (boostTick - t * 2);
                        if (d <= 0) {
                            boost = 0;
                        } else {
                            boost = boostSpeed.getFloatValue() * ((float) d / (float) t);
                        }
                    } else boost = boostSpeed.getFloatValue();
                    playerSpeed *= 1 + boost;
                    boostTick++;
                } else boostable = false;
            }
            PotionEffect speedEffect = this.mc.player.getActivePotionEffect(MobEffects.SPEED);
            if (useSpeed.getValue() && this.mc.player.isPotionActive(MobEffects.SPEED) && speedEffect != null) {
                int amplifier = speedEffect.getAmplifier();
                playerSpeed *= 1.0f + 0.2f * (amplifier + 1);
            }
            if (forwardSpeed == 0.0f && strafeSpeed == 0.0f) {
                velocity = new Vec3d(0.0, velocity.y, 0.0);
                jumps = 0;
                boostable = false;
            } else {
                if (forwardSpeed != 0.0f) {
                    if (strafeSpeed > 0.0f) {
                        rotationYaw += ((forwardSpeed > 0.0f) ? -45 : 45);
                    } else if (strafeSpeed < 0.0f) {
                        rotationYaw += ((forwardSpeed > 0.0f) ? 45 : -45);
                    }
                    strafeSpeed = 0.0f;
                    if (forwardSpeed > 0.0f) {
                        forwardSpeed = 1.0f;
                    } else if (forwardSpeed < 0.0f) {
                        forwardSpeed = -1.0f;
                    }
                }
                double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
                double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));
                velocity = new Vec3d(forwardSpeed * playerSpeed * cos + strafeSpeed * playerSpeed * sin, velocity.y, forwardSpeed * playerSpeed * sin - strafeSpeed * playerSpeed * cos);
            }
            event.setX(velocity.x);
            event.setY(velocity.y);
            event.setZ(velocity.z);
            MC.mc.player.setVelocity(velocity.x, velocity.y, velocity.z);
        }
    }

    @Override
    public void onEnable() {
        this.prevOnGround = false;
        this.jumps = 0;
        this.offGroundTicks = 0;
    }

    private boolean fullCheck() {
        try {
            if (this.mc.player == null || !this.isEnabled() || MC.mc.world == null) {
                return false;
            } else if ((this.mc.player.isInLava() || this.mc.player.isInLava()) && !liquids.getValue()) {
                return false;
            } else if (this.mc.player.isElytraFlying()) {
                return false;
            } else {
                return !this.mc.player.isOnLadder() && this.mc.player.getRidingEntity() == null && MC.mc.getRenderViewEntity() == MC.mc.player;
            }
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    public Vec3d getVelocity() {
        return new Vec3d(this.mc.player.motionX, this.mc.player.motionY, this.mc.player.motionZ);
    }
}

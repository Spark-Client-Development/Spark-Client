package me.wallhacks.spark.systems.module.modules.movement;

import baritone.Baritone;
import baritone.api.pathing.movement.IMovement;
import baritone.api.pathing.path.IPathExecutor;
import baritone.api.BaritoneAPI;
import baritone.pathing.movement.Movement;
import baritone.pathing.movement.movements.MovementAscend;
import baritone.pathing.movement.movements.MovementDiagonal;
import baritone.pathing.movement.movements.MovementTraverse;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.event.player.PlayerMoveEvent;
import me.wallhacks.spark.event.player.PlayerPreUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.BaritoneConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraft.entity.MoverType;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
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
    ModeSetting mode = new ModeSetting("Mode", this, "Vanilla", Arrays.asList("Vanilla", "Strafe", "StrictNCP", "OnGround"));
    BooleanSetting liquids = new BooleanSetting("Liquids", this, false);
    BooleanSetting useSpeed = new BooleanSetting("UseSpeed", this, false, "Effects");
    BooleanSetting useJumpBoost = new BooleanSetting("UseJumpBoost", this, false, "Effects");
    BooleanSetting boost = new BooleanSetting("Boost", this, false, "Boost");
    IntSetting boostTicks = new IntSetting("BoostTicks", this, 5, 1, 40, "Boost");
    DoubleSetting boostSpeed = new DoubleSetting("BoostSpeed", this, 0.1, 0.0, 2.0, "Boost");
    BooleanSetting boostStop = new BooleanSetting("BoostStop", this, false, "Boost");
    public static Speed INSTANCE;
    private double prevMotion = 0.0D;
    private int gState;
    private double speed;
    private boolean prevOnGround;
    private int jumps;
    private int offGroundTicks;
    private boolean boostable;
    private int boostTick;

    public Speed() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onUpdate(PlayerPreUpdateEvent event) {
        if (fullCheck() && !mode.is("OnGround")) {
            Vec3d velocity = getVelocity();
            if (mc.player.onGround && !prevOnGround && (mc.player.moveForward != 0.0 || mc.player.moveStrafing != 0.0)) {
                MC.mc.player.setSprinting(true);
                ++jumps;
                if (boostStop.getValue()) boostable = false;
                velocity = new Vec3d(velocity.x, 0.405f, velocity.z);
                PotionEffect jumpBoostEffect = mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST);
                if (useJumpBoost.getValue() && mc.player.isPotionActive(MobEffects.JUMP_BOOST) && jumpBoostEffect != null) {
                    velocity = velocity.add(new Vec3d(0.0, ((jumpBoostEffect.getAmplifier() + 1) * 0.1f), 0.0));
                }
            }
            if (!mc.player.onGround) {
                ++offGroundTicks;
            } else if (!prevOnGround) {
                offGroundTicks = 0;
            }
            if (velocity.x * velocity.x + velocity.z * velocity.z > 1.0E-4 && (mc.player.moveForward != 0.0 || mc.player.moveStrafing != 0.0)) {
                prevOnGround = mc.player.onGround;
            } else {
                prevOnGround = false;
                jumps = 0;
                boostable = false;
            }
            MC.mc.player.setVelocity(velocity.x, velocity.y, velocity.z);
        } else {
            double dX = mc.player.posX - mc.player.prevPosX;
            double dZ = mc.player.posZ - mc.player.prevPosZ;
            prevMotion = Math.sqrt(dX * dX + dZ * dZ);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            jumps = 0;
            boostable = false;
            prevOnGround = false;
            gState = 2;
            speed = 0;
        }
        if (event.getPacket() instanceof SPacketExplosion && boost.getValue()) {
            SPacketExplosion packet = event.getPacket();
            if (Math.abs(packet.motionX) > 0.1 || Math.abs(packet.motionZ) > 0.1) {
                boostTick = 0;
                boostable = true;
            }
            packet.motionY = 0;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if (fullCheck()) {
            if (event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
                if (gState == 3 && !((mc.player.collidedHorizontally || mc.player.moveForward == 0) && mc.player.moveStrafing == 0) && mc.player.onGround) {
                    ((CPacketPlayer) event.getPacket()).y += checkHeadspace() ? 0.2 : 0.4;
                }
            }
        }
    }

    @SubscribeEvent
    public void onMove(PlayerMoveEvent event) {
        if (fullCheck() && event.getType() == MoverType.SELF) {
            float forward = mc.player.movementInput.moveForward;
            float strafe = mc.player.movementInput.moveStrafe;
            if (mode.is("OnGround")) {
                if (!mc.player.onGround) {
                    if (gState != 3) return;
                }
                if (!((mc.player.collidedHorizontally || mc.player.moveForward == 0) && mc.player.moveStrafing == 0)) {
                    if (gState == 2) {
                        speed *= 2.149;
                        gState = 3;
                    } else if (gState == 3) {
                        double adjustedSpeed = 0.66 * (prevMotion - getBaseMotionSpeed());
                        speed = prevMotion - adjustedSpeed;
                        gState = 2;
                    } else {
                        if (checkHeadspace() || mc.player.collidedVertically) {
                            gState = 1;
                        }
                    }
                }
                speed = Math.min(Math.max(speed, getBaseMotionSpeed()), getBaseMotionSpeed() * mSpeed.getValue());
            } else {
                if (!mode.is("Strict")) {
                    speed = getBaseMotionSpeed();
                    if (mc.player.onGround) {
                        if (jumps > 1) {
                            speed *= 1.9318f;
                        }
                        if (jumps > 2) {
                            speed *= 1.0541f;
                        }
                        if (jumps > 3) {
                            speed *= 1.0256f;
                        }
                        if (jumps > 4) {
                            speed *= 1.0167f;
                        }
                    } else {
                        if (jumps > 1) {
                            speed *= 1.2008f - 0.0139f * offGroundTicks;
                        }
                        if (jumps > 2) {
                            speed *= 1.029f;
                        }
                        if (jumps > 3) {
                            speed *= 1.0141f;
                        }
                        if (mode.is("Fast")) {
                            if (jumps > 2) {
                                speed *= 1.0139f;
                            }
                            if (jumps > 4) {
                                speed *= 1.0274f;
                            }
                            if (jumps > 6) {
                                speed *= 1.0267f;
                            }
                            if (jumps > 8) {
                                speed *= 1.026f;
                                jumps = 3;
                            }
                        }
                    }
                    if (speed < 0.2873f) {
                        speed = 0.2873f;
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
                        speed *= 1 + boost;
                        boostTick++;
                    } else boostable = false;
                }
            }
            if (forward == 0 && strafe == 0) {
                event.setX(0D);
                event.setZ(0D);
            } else if (forward != 0.0D && strafe != 0.0D) {
                forward *= Math.sin(0.7853981633974483D);
                strafe *= Math.cos(0.7853981633974483D);
            }
            float yaw = BaritoneAPI.getProvider().getPrimaryBaritone().getLookBehavior().getYaw();
            event.setX(forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw)));
            event.setZ(forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw)));
        }
    }

    @Override
    public void onEnable() {
        prevOnGround = false;
        jumps = 0;
        offGroundTicks = 0;
        gState = 2;
    }

    private double getBaseMotionSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.player.isPotionActive(MobEffects.SPEED) && useSpeed.getValue()) {
            int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
            baseSpeed *= 1.0D + 0.2D * ((double) amplifier + 1);
        }
        return baseSpeed;
    }

    private boolean fullCheck() {
        try {
            if (mc.player == null || !isEnabled() || MC.mc.world == null) {
                return false;
            } else if ((mc.player.isInLava() || mc.player.isInLava()) && !liquids.getValue()) {
                return false;
            } else if (mc.player.isElytraFlying()) {
                return false;
            } else if (!isSafeToSpeed()) {
                return false;
            } else {
                return !mc.player.isOnLadder() && mc.player.getRidingEntity() == null && MC.mc.getRenderViewEntity() == MC.mc.player;
            }
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    private boolean checkHeadspace() {
        return mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0D, 0.21D, 0D)).size() > 0;
    }

    boolean isSafeToSpeed() {
        if (!BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) return true;
        IPathExecutor executor = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getCurrent();
        int currentPostion = executor.getPosition();
        Class f = null;
        if (executor.getPath().movements().isEmpty()) return true;
        for (int i = 0; i < Math.min(2, executor.getPath().movements().size() - currentPostion - 1); i++) {
            IMovement movement = executor.getPath().movements().get(currentPostion + i);
            if (movement instanceof MovementTraverse || movement instanceof MovementDiagonal) {
                if (f == null) {
                    f = movement.getClass();
                    continue;
                } else if (movement.getClass().equals(f)) {
                    continue;
                }
            }
            return false;
        }
        return true;
    }

    public Vec3d getVelocity() {
        return new Vec3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
    }
}

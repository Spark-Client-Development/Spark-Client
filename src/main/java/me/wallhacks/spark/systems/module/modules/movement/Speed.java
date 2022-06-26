package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.event.player.PlayerMoveEvent;
import me.wallhacks.spark.event.player.PlayerPreUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import net.minecraft.entity.MoverType;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

@Module.Registration(name = "Speed", description = "Fast module go brrr")
public class Speed extends Module {
    ModeSetting mode = new ModeSetting("Mode", this, "Strafe", Arrays.asList("Strafe", "StrictStrafe", "OnGround", "BoostStrafe", "LowHop"));
    DoubleSetting boostF = new DoubleSetting("Boost", this, 1.5D, 1D, 3D, v -> mode.is("BoostStrafe"));
    DoubleSetting boosty = new DoubleSetting("YFactor", this, 0.8D, 0.2D, 1D, v -> mode.is("BoostStrafe"));
    BooleanSetting liquids = new BooleanSetting("Liquids", this, false);
    BooleanSetting useSpeed = new BooleanSetting("UseSpeed", this, false);
    BooleanSetting useJumpBoost = new BooleanSetting("UseJumpBoost", this, false);
    private double prevMotion = 0.0D;
    private int state;
    private double speed;
    private boolean prevOnGround;
    private int jumps;
    private int offGroundTicks;
    private double boost = 0;
    private int boostTick;
    private boolean flag;
    
    @SubscribeEvent
    public void onUpdate(PlayerPreUpdateEvent event) {
        boostTick++;
        if (fullCheck() && !mode.is("OnGround") && !mode.is("LowHop")) {
            Vec3d velocity = getVelocity();
            if (mc.player.onGround && !prevOnGround && (mc.player.moveForward != 0.0 || mc.player.moveStrafing != 0.0)) {
                mc.player.setSprinting(true);
                ++jumps;
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
            }
            mc.player.setVelocity(velocity.x, velocity.y, velocity.z);
        } else boost = 0;
        double dX = mc.player.posX - mc.player.prevPosX;
        double dZ = mc.player.posZ - mc.player.prevPosZ;
        prevMotion = Math.sqrt(dX * dX + dZ * dZ);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketReceiveEvent event) {
        if (mc.player != null)
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            jumps = 0;
            prevOnGround = false;
            state = mode.is("OnGround") ? 2 : 4;
            speed = 0;
            boost = 0;
        } else if (event.getPacket() instanceof SPacketExplosion || (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) event.getPacket()).entityID == mc.player.entityId)) {
            double motionX;
            double motionZ;
            if (event.getPacket() instanceof SPacketExplosion) {
                SPacketExplosion p = event.getPacket();
                motionX = p.motionX;
                motionZ = p.motionZ;
            } else {
                SPacketEntityVelocity p = event.getPacket();
                motionX = p.motionX / 8000f;
                motionZ = p.motionZ / 8000f;
            }
            boostTick = 0;
            boost = MathHelper.clamp(Math.sqrt(MathUtil.square(motionX) + MathUtil.square(motionZ)) * boostF.getValue(), boost, 1);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if (fullCheck() && mode.is("OnGround")) {
            if (event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
                if (state == 3 && !((mc.player.collidedHorizontally || mc.player.moveForward == 0) && mc.player.moveStrafing == 0) && mc.player.onGround) {
                    ((CPacketPlayer) event.getPacket()).y += checkHeadspace() ? 0.2 : 0.4;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onMove(PlayerMoveEvent event) {
        if (event.isCanceled()) return;
        if (fullCheck() && event.getType() == MoverType.SELF) {
            float forward = mc.player.movementInput.moveForward;
            float strafe = mc.player.movementInput.moveStrafe;
            if (mode.is("OnGround")) {
                if (!mc.player.onGround) {
                    if (state != 3) return;
                }
                if (!((mc.player.collidedHorizontally || mc.player.moveForward == 0) && mc.player.moveStrafing == 0)) {
                    if (state == 2) {
                        speed *= 2.149;
                        state = 3;
                    } else if (state == 3) {
                        double adjustedSpeed = 0.66 * (prevMotion - getBaseMotionSpeed());
                        speed = prevMotion - adjustedSpeed;
                        state = 2;
                    } else {
                        if (checkHeadspace() || mc.player.collidedVertically) {
                            state = 1;
                        }
                    }
                }
                speed = Math.max(speed, getBaseMotionSpeed());
            } else if (mode.is("Strafe") || mode.is("BoostStrafe")) {
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
                        speed *= 1.0433031f;
                    }
                    if (jumps > 3) {
                        speed *= 1.0141f;
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
                if (mode.is("BoostStrafe")) {
                    if (prevMotion < speed) boost = 0;
                    if (boost > speed && boostTick < 50) {
                        speed = MathHelper.clamp(speed * boostF.getValue(), speed, boost);
                        if (event.getY() < 0) event.setY(event.getY()*boosty.getFloatValue());
                    }
                }
            } else if (mode.is("StrictStrafe")) {
                mc.player.setSprinting(true);
                return;
            } else if (mode.is("LowHop")) {
                double jumpSpeed = 0.0D;

                if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    jumpSpeed += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F;
                }

                if (MathUtil.roundAvoid(mc.player.posY - (double) (int) mc.player.posY, 3) == MathUtil.roundAvoid(0.4, 3)) {
                    mc.player.motionY = 0.31 + jumpSpeed;
                    event.setY(mc.player.motionY);
                } else if (MathUtil.roundAvoid(mc.player.posY - (double) (int) mc.player.posY, 3) == MathUtil.roundAvoid(0.71, 3)) {
                    mc.player.motionY = 0.04 + jumpSpeed;
                    event.setY(mc.player.motionY);
                } else if (MathUtil.roundAvoid(mc.player.posY - (double) (int) mc.player.posY, 3) == MathUtil.roundAvoid(0.75, 3)) {
                    mc.player.motionY = -0.2 - jumpSpeed;
                    event.setY(mc.player.motionY);
                } else if (MathUtil.roundAvoid(mc.player.posY - (double) (int) mc.player.posY, 3) == MathUtil.roundAvoid(0.55, 3)) {
                    mc.player.motionY = -0.14 + jumpSpeed;
                    event.setY(mc.player.motionY);
                } else {
                    if (MathUtil.roundAvoid(mc.player.posY - (double) (int) mc.player.posY, 3) == MathUtil.roundAvoid(0.41, 3)) {
                        mc.player.motionY = -0.2 + jumpSpeed;
                        event.setY(mc.player.motionY);
                    }
                }

                if (state == 1 && (mc.player.moveForward != 0F || mc.player.moveStrafing != 0F)) {
                    speed = 1.35 * getBaseMotionSpeed() - 0.01;
                } else if (state == 2 && (mc.player.moveForward != 0F || mc.player.moveStrafing != 0F)) {
                    mc.player.motionY = (checkHeadspace() ? 0.2 : 0.3999) + jumpSpeed;
                    event.setY(mc.player.motionY);
                    speed = speed * (flag ? 1.5685 : 1.3445);
                } else if (state == 3) {
                    double dV = 0.66 * (prevMotion - getBaseMotionSpeed());
                    speed = prevMotion - dV;
                    flag = !flag;
                } else {
                    if (mc.player.onGround && state > 0) {
                        state = mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f ? 1 : 0;
                    }
                    speed = prevMotion - prevMotion / 159.0D;
                }

                speed = Math.max(speed, getBaseMotionSpeed());

                if (mc.player.moveForward != 0F || mc.player.moveStrafing != 0F) state++;
            }
            if (forward == 0 && strafe == 0) {
                event.setX(0D);
                event.setZ(0D);
                boost = 0;
            } else if (forward != 0.0D && strafe != 0.0D) {
                forward *= Math.sin(0.7853981633974483D);
                strafe *= Math.cos(0.7853981633974483D);
            }
            event.setCanceled(true);
            float yaw = mc.player.rotationYaw; //BaritoneAPI.getProvider().getPrimaryBaritone().getLookBehavior().getYaw();
            event.setX(forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw)));
            event.setZ(forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw)));
        } else {
            boost = 0;
        }
    }

    @Override
    public void onEnable() {
        prevOnGround = false;
        jumps = 0;
        prevMotion = 0;
        offGroundTicks = 0;
        state = mode.is("OnGround") ? 2 : 4;
        speed = 0;
        boost = 0;
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
            if (mc.player == null || !isEnabled() || mc.world == null) {
                return false;
            } else if (((mc.player.isInLava() || mc.player.isInLava()) && !liquids.getValue()) || (mode.is("OnGround") && PlayerUtil.isOnLiquid())) {
                return false;
            } else if (mc.player.isElytraFlying()) {
                return false;
            } else if (!isSafeToSpeed()) {
                return false;
            } else if (mc.player.isSneaking()) {
                return false;
            } else {
                return !mc.player.isOnLadder() && mc.player.getRidingEntity() == null && mc.getRenderViewEntity() == mc.player;
            }
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    private boolean checkHeadspace() {
        return mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0D, 0.21D, 0D)).size() > 0;
    }

    boolean isSafeToSpeed() {
        return true;
        /*
        if (!BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) return true;
        IPathExecutor executor = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getCurrent();
        int currentPostion = executor.getPosition();
        Class<? extends IMovement> f = null;
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
        return true;*/
    }

    public Vec3d getVelocity() {
        return new Vec3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
    }
}

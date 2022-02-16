package me.wallhacks.spark.util.objects;

import me.wallhacks.spark.util.render.CameraUtil;
import net.minecraft.client.entity.EntityPlayerSP;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collection;

public class FreecamEntity extends EntityPlayerSP {
    public FreecamEntity(Minecraft mc) {
        super(mc, mc.world, mc.player.connection, mc.player.getStatFileWriter(), mc.player.getRecipeBook());

        this.noClip = true;
        Entity player = mc.player;

        if (player != null) {
            this.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);

            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
            this.setRotationYawHead(this.rotationYaw);
            this.setRenderYawOffset(this.rotationYaw);

            this.inventory = mc.player.inventory;
        }
    }

    private static MovementInput dummyInput = new MovementInput();
    private Entity originalRenderViewEntity;

    private float movementSpeed = 0.07f;
    private float movementScaleY = 0.75f;
    private boolean cullChunksOriginal;
    private float forwardRamped;
    private float strafeRamped;
    private float verticalRamped;

    @Override
    public boolean isSpectator() {
        return true;
    }

    @Override
    public boolean isPotionActive(Potion potion) {
        return potion != MobEffects.BLINDNESS && mc.player.isPotionActive(potion);
    }

    public MovementInput getDummyInput() {
        return dummyInput;
    }

    @Nullable
    public PotionEffect getActivePotionEffect(Potion potionIn) {
        return mc.player.getActivePotionEffect(potionIn);
    }

    @Nonnull
    public Collection<PotionEffect> getActivePotionEffects() {
        return mc.player.getActivePotionEffects(); //Might be bugged
    }

    public float getAbsorptionAmount() {
        return mc.player.getAbsorptionAmount();
    }

    @Nonnull
    public FoodStats getFoodStats() {
        return mc.player.getFoodStats();
    }

    public void movementTick(TickEvent.Phase phase) {
        if (phase == TickEvent.Phase.END) {
            if (mc.getRenderViewEntity() != this) {
                mc.setRenderViewEntity(this);
            }
            mc.gameSettings.thirdPersonView = 0;
            return;
        }

        if (mc.player.movementInput.getClass() == MovementInputFromOptions.class) {
            mc.player.movementInput = dummyInput;
        }

        this.updateCamera();
        this.updateLastTickPosition();
        this.movementInput.updatePlayerMoveState();

        float forward = this.movementInput.moveForward;
        float vertical = (float) Boolean.compare(this.movementInput.jump, this.movementInput.sneak);
        float strafe = this.movementInput.moveStrafe;

        float rampAmount = 0.15f;
        float speed = strafe * strafe + forward * forward;

        if (forward != 0 && strafe != 0) {
            speed = (float) Math.sqrt(speed * 0.6);
        } else {
            speed = 1;
        }

        forwardRamped = getRampedMotion(forwardRamped, forward, rampAmount) / speed * (this.isSprinting() ? 3 : 1);
        verticalRamped = getRampedMotion(verticalRamped, vertical, rampAmount);
        strafeRamped = getRampedMotion(strafeRamped, strafe, rampAmount) / speed;

        this.handleMotion(forwardRamped, verticalRamped, strafeRamped);
    }

    private static float getRampedMotion(float current, float input, float rampAmount) {
        if (input != 0) {
            if (input < 0) {
                rampAmount *= -1f;
            }

            // Immediately kill the motion when changing direction to the opposite
            if ((input < 0) != (current < 0)) {
                current = 0;
            }

            current = MathHelper.clamp(current + rampAmount, -1f, 1f);
        } else {
            current *= 0.5f;
        }

        return current;
    }

    public void setMoveSpeed(float speed, float scaleY) {
        this.movementSpeed = speed;
        this.movementScaleY = scaleY;
    }


    private void handleMotion(float forward, float up, float strafe) {
        double scale = this.movementSpeed;
        double scaleY = this.movementScaleY;

        this.moveRelative(strafe, up, forward, 1.0f);
        this.move(MoverType.SELF, this.motionX * scale, this.motionY * scale * scaleY, this.motionZ * scale);

        this.motionX = 0; //(double) (strafe * zFactor - forward * xFactor) * scale;
        this.motionY = 0; //(double) up * scale;
        this.motionZ = 0; //(double) (forward * zFactor + strafe * xFactor) * scale;

        this.chunkCoordX = (int) Math.floor(this.posX) >> 4;
        this.chunkCoordY = (int) Math.floor(this.posY) >> 4;
        this.chunkCoordZ = (int) Math.floor(this.posZ) >> 4;
    }

    private void updateLastTickPosition() {
        this.prevPosX = this.lastTickPosX = this.posX;
        this.prevPosY = this.lastTickPosY = this.posY;
        this.prevPosZ = this.lastTickPosZ = this.posZ;
    }

    private void updateCamera() {
        this.setHealth(mc.player.getHealth());
        this.hurtTime = mc.player.hurtTime;
        this.maxHurtTime = mc.player.maxHurtTime;
        this.attackedAtYaw = mc.player.attackedAtYaw;
    }

    public void enableCamera() {
        originalRenderViewEntity = mc.getRenderViewEntity();
        cullChunksOriginal = mc.renderChunksMany;

        mc.player.movementInput = dummyInput;
        this.movementInput = (MovementInput) new MovementInputFromOptions(mc.gameSettings);

        mc.setRenderViewEntity(this);
        mc.renderChunksMany = false; // Disable chunk culling
    }

    public void disableCamera() {
        mc.setRenderViewEntity(originalRenderViewEntity);
        mc.renderChunksMany = cullChunksOriginal;

        if (mc.world != null) {
            CameraUtil.markChunksForRebuildOnDeactivation(this.chunkCoordX, this.chunkCoordZ);
        }

        mc.player.movementInput = (MovementInput) new MovementInputFromOptions(mc.gameSettings);
        originalRenderViewEntity = null;
    }
}

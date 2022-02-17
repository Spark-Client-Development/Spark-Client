package me.wallhacks.spark.mixin.mixins.spark;

import com.mojang.authlib.GameProfile;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.*;
import me.wallhacks.spark.systems.module.modules.player.PortalChat;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.FreecamEntity;
import me.wallhacks.spark.util.render.CameraUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.wallhacks.spark.systems.module.modules.movement.NoSlow;
import me.wallhacks.spark.systems.module.modules.movement.Step;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

@Mixin({EntityPlayerSP.class})
public class MixinEntityPlayerSP extends AbstractClientPlayer implements MC {

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void onUpdateWalkingPlayerPre(final CallbackInfo info) {

        UpdateWalkingPlayerEvent.Pre event = new UpdateWalkingPlayerEvent.Pre();
        Spark.eventBus.post(event);

        if (event.isCanceled()) {
            info.cancel();
        }
    }


    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    public void onUpdateWalkingPlayerPost(final CallbackInfo callbackInfo) {
        UpdateWalkingPlayerEvent.Post event = new UpdateWalkingPlayerEvent.Post();
        Spark.eventBus.post(event);
    }


    @Inject(method = "updateEntityActionState", at = @At("HEAD"))
    public void updateEntityActionState(final CallbackInfo info) {
        UpdateEntityAction event = new UpdateEntityAction();
        Spark.eventBus.post(event);

    }


    @Inject(method = "onUpdate", at = @At("HEAD"))
    public void onUpdate(final CallbackInfo info) {
        PlayerPreUpdateEvent event = new PlayerPreUpdateEvent();
        Spark.eventBus.post(event);
    }


    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    public void onLivingUpdate(final CallbackInfo info) {
        PlayerLivingTickEvent event = new PlayerLivingTickEvent();
        Spark.eventBus.post(event);
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(AbstractClientPlayer player, MoverType type, double x, double y, double z) {
        PlayerMoveEvent moveEvent = new PlayerMoveEvent(type, x, y, z);
        Spark.eventBus.post(moveEvent);
        x = moveEvent.getX();
        y = moveEvent.getY();
        z = moveEvent.getZ();
        EntityPlayerSP _this = (EntityPlayerSP) (Object) this;
        if (this.noClip) {
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
            this.resetPositionToBB();
        } else {
            BlockPos blockpos1;
            IBlockState iblockstate1;
            Block block1;
            if (type == MoverType.PISTON) {
                long i = this.world.getTotalWorldTime();
                if (i != this.pistonDeltasGameTime) {
                    Arrays.fill(this.pistonDeltas, 0.0);
                    this.pistonDeltasGameTime = i;
                }
                if (x != 0.0) {
                    int j = EnumFacing.Axis.X.ordinal();
                    double d0 = MathHelper.clamp((double) (x + this.pistonDeltas[j]), (double) -0.51, (double) 0.51);
                    x = d0 - this.pistonDeltas[j];
                    this.pistonDeltas[j] = d0;
                    if (Math.abs(x) <= (double) 1.0E-5f) {
                        return;
                    }
                } else if (y != 0.0) {
                    int l4 = EnumFacing.Axis.Y.ordinal();
                    double d12 = MathHelper.clamp((double) (y + this.pistonDeltas[l4]), (double) -0.51, (double) 0.51);
                    y = d12 - this.pistonDeltas[l4];
                    this.pistonDeltas[l4] = d12;
                    if (Math.abs(y) <= (double) 1.0E-5f) {
                        return;
                    }
                } else {
                    if (z == 0.0) {
                        return;
                    }
                    int i5 = EnumFacing.Axis.Z.ordinal();
                    double d13 = MathHelper.clamp((double) (z + this.pistonDeltas[i5]), (double) -0.51, (double) 0.51);
                    z = d13 - this.pistonDeltas[i5];
                    this.pistonDeltas[i5] = d13;
                    if (Math.abs(z) <= (double) 1.0E-5f) {
                        return;
                    }
                }
            }
            this.world.profiler.startSection("move");
            double d10 = this.posX;
            double d11 = this.posY;
            double d1 = this.posZ;
            if (this.isInWeb) {
                if (!NoSlow.INSTANCE.isEnabled() || !NoSlow.INSTANCE.webs.is("Vanilla")) {
                    this.isInWeb = false;
                    if (NoSlow.INSTANCE.isEnabled() && NoSlow.INSTANCE.webs.is("Fast")) {
                        x *= NoSlow.INSTANCE.webSpeed.getFloatValue();
                        y *= NoSlow.INSTANCE.webSpeedY.getFloatValue();
                        z *= NoSlow.INSTANCE.webSpeed.getFloatValue();
                    } else {
                        x *= 0.25;
                        y *= 0.05;
                        z *= 0.25;
                    }
                    this.motionX = 0.0;
                    this.motionY = 0.0;
                    this.motionZ = 0.0;
                }
            }
            double d2 = x;
            double d3 = y;
            double d4 = z;
            if ((type == MoverType.SELF || type == MoverType.PLAYER) && this.onGround && this.isSneaking() && _this instanceof EntityPlayer) {
                double d5 = 0.05;
                while (x != 0.0 && this.world.getCollisionBoxes(_this, this.getEntityBoundingBox().offset(x, (double)(-this.stepHeight), 0.0)).isEmpty()) {
                    x = x < 0.05 && x >= -0.05 ? 0.0 : (x > 0.0 ? (x -= 0.05) : (x += 0.05));
                    d2 = x;
                }
                while (z != 0.0 && this.world.getCollisionBoxes(_this, this.getEntityBoundingBox().offset(0.0, (double)(-this.stepHeight), z)).isEmpty()) {
                    z = z < 0.05 && z >= -0.05 ? 0.0 : (z > 0.0 ? (z -= 0.05) : (z += 0.05));
                    d4 = z;
                }
                while (x != 0.0 && z != 0.0 && this.world.getCollisionBoxes(_this, this.getEntityBoundingBox().offset(x, (double)(-this.stepHeight), z)).isEmpty()) {
                    x = x < 0.05 && x >= -0.05 ? 0.0 : (x > 0.0 ? (x -= 0.05) : (x += 0.05));
                    d2 = x;
                    z = z < 0.05 && z >= -0.05 ? 0.0 : (z > 0.0 ? (z -= 0.05) : (z += 0.05));
                    d4 = z;
                }
            }
            List list1 = this.world.getCollisionBoxes(_this, this.getEntityBoundingBox().expand(x, y, z));
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
            if (y != 0.0) {
                int l = list1.size();
                for (int k = 0; k < l; ++k) {
                    y = ((AxisAlignedBB)list1.get(k)).calculateYOffset(this.getEntityBoundingBox(), y);
                }
                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
            }
            if (x != 0.0) {
                int l5 = list1.size();
                for (int j5 = 0; j5 < l5; ++j5) {
                    x = ((AxisAlignedBB)list1.get(j5)).calculateXOffset(this.getEntityBoundingBox(), x);
                }
                if (x != 0.0) {
                    this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0, 0.0));
                }
            }
            if (z != 0.0) {
                int i6 = list1.size();
                for (int k5 = 0; k5 < i6; ++k5) {
                    z = ((AxisAlignedBB)list1.get(k5)).calculateZOffset(this.getEntityBoundingBox(), z);
                }
                if (z != 0.0) {
                    this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, 0.0, z));
                }
            }
            boolean flag = this.onGround || d3 != y && d3 < 0.0;
            if (this.stepHeight > 0.0f && flag && (d2 != x || d4 != z)) {
                double d14 = x;
                double d6 = y;
                double d7 = z;
                AxisAlignedBB axisalignedbb1 = this.getEntityBoundingBox();
                this.setEntityBoundingBox(axisalignedbb);
                y = stepHeight;
                if (Step.INSTANCE.isEnabled() && Step.INSTANCE.mode.is("Vanilla")) {
                    y = Step.INSTANCE.height.getValue();
                }
                List list = this.world.getCollisionBoxes(_this, this.getEntityBoundingBox().expand(d2, y, d4));
                AxisAlignedBB axisalignedbb2 = this.getEntityBoundingBox();
                AxisAlignedBB axisalignedbb3 = axisalignedbb2.expand(d2, 0.0, d4);
                double d8 = y;
                int k1 = list.size();
                for (int j1 = 0; j1 < k1; ++j1) {
                    d8 = ((AxisAlignedBB)list.get(j1)).calculateYOffset(axisalignedbb3, d8);
                }
                axisalignedbb2 = axisalignedbb2.offset(0.0, d8, 0.0);
                double d18 = d2;
                int i2 = list.size();
                for (int l1 = 0; l1 < i2; ++l1) {
                    d18 = ((AxisAlignedBB)list.get(l1)).calculateXOffset(axisalignedbb2, d18);
                }
                axisalignedbb2 = axisalignedbb2.offset(d18, 0.0, 0.0);
                double d19 = d4;
                int k2 = list.size();
                for (int j2 = 0; j2 < k2; ++j2) {
                    d19 = ((AxisAlignedBB)list.get(j2)).calculateZOffset(axisalignedbb2, d19);
                }
                axisalignedbb2 = axisalignedbb2.offset(0.0, 0.0, d19);
                AxisAlignedBB axisalignedbb4 = this.getEntityBoundingBox();
                double d20 = y;
                int i3 = list.size();
                for (int l2 = 0; l2 < i3; ++l2) {
                    d20 = ((AxisAlignedBB)list.get(l2)).calculateYOffset(axisalignedbb4, d20);
                }
                axisalignedbb4 = axisalignedbb4.offset(0.0, d20, 0.0);
                double d21 = d2;
                int k3 = list.size();
                for (int j3 = 0; j3 < k3; ++j3) {
                    d21 = ((AxisAlignedBB)list.get(j3)).calculateXOffset(axisalignedbb4, d21);
                }
                axisalignedbb4 = axisalignedbb4.offset(d21, 0.0, 0.0);
                double d22 = d4;
                int i4 = list.size();
                for (int l3 = 0; l3 < i4; ++l3) {
                    d22 = ((AxisAlignedBB)list.get(l3)).calculateZOffset(axisalignedbb4, d22);
                }
                axisalignedbb4 = axisalignedbb4.offset(0.0, 0.0, d22);
                double d23 = d18 * d18 + d19 * d19;
                double d9 = d21 * d21 + d22 * d22;
                if (d23 > d9) {
                    x = d18;
                    z = d19;
                    y = -d8;
                    this.setEntityBoundingBox(axisalignedbb2);
                } else {
                    x = d21;
                    z = d22;
                    y = -d20;
                    this.setEntityBoundingBox(axisalignedbb4);
                }
                int k4 = list.size();
                for (int j4 = 0; j4 < k4; ++j4) {
                    y = ((AxisAlignedBB)list.get(j4)).calculateYOffset(this.getEntityBoundingBox(), y);
                }
                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
                if (d14 * d14 + d7 * d7 >= x * x + z * z) {
                    x = d14;
                    y = d6;
                    z = d7;
                    this.setEntityBoundingBox(axisalignedbb1);
                }
            }
            this.world.profiler.endSection();
            this.world.profiler.startSection("rest");
            this.resetPositionToBB();
            this.collidedHorizontally = d2 != x || d4 != z;
            this.collidedVertically = d3 != y;
            this.onGround = this.collidedVertically && d3 < 0.0;
            this.collided = this.collidedHorizontally || this.collidedVertically;
            int j6 = MathHelper.floor((double)this.posX);
            int i1 = MathHelper.floor((double)(this.posY - (double)0.2f));
            int k6 = MathHelper.floor((double)this.posZ);
            BlockPos blockpos = new BlockPos(j6, i1, k6);
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            if (iblockstate.getMaterial() == Material.AIR && ((block1 = (iblockstate1 = this.world.getBlockState(blockpos1 = blockpos.down())).getBlock()) instanceof BlockFence || block1 instanceof BlockWall || block1 instanceof BlockFenceGate)) {
                iblockstate = iblockstate1;
                blockpos = blockpos1;
            }
            this.updateFallState(y, this.onGround, iblockstate, blockpos);
            if (d2 != x) {
                this.motionX = 0.0;
            }
            if (d4 != z) {
                this.motionZ = 0.0;
            }
            Block block = iblockstate.getBlock();
            if (d3 != y) {
                block.onLanded(this.world, _this);
            }
            if (!(!this.canTriggerWalking() || this.onGround && this.isSneaking() && _this instanceof EntityPlayer || this.isRiding())) {
                double d15 = this.posX - d10;
                double d16 = this.posY - d11;
                double d17 = this.posZ - d1;
                if (block != Blocks.LADDER) {
                    d16 = 0.0;
                }
                if (block != null && this.onGround) {
                    block.onEntityWalk(this.world, blockpos, _this);
                }
                this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt((double)(d15 * d15 + d17 * d17)) * 0.6);
                this.distanceWalkedOnStepModified = (float)((double)this.distanceWalkedOnStepModified + (double)MathHelper.sqrt((double)(d15 * d15 + d16 * d16 + d17 * d17)) * 0.6);
                if (this.distanceWalkedOnStepModified > (float)this.nextStepDistance && iblockstate.getMaterial() != Material.AIR) {
                    this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;
                    if (this.isInWater()) {
                        Entity entity = this.isBeingRidden() && this.getControllingPassenger() != null ? this.getControllingPassenger() : _this;
                        float f = entity == _this ? 0.35f : 0.4f;
                        float f1 = MathHelper.sqrt((double)(entity.motionX * entity.motionX * (double)0.2f + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ * (double)0.2f)) * f;
                        if (f1 > 1.0f) {
                            f1 = 1.0f;
                        }
                        this.playSound(this.getSwimSound(), f1, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                    } else {
                        this.playStepSound(blockpos, block);
                    }
                } else if (this.distanceWalkedOnStepModified > this.nextFlap && this.makeFlySound() && iblockstate.getMaterial() == Material.AIR) {
                    this.nextFlap = this.playFlySound(this.distanceWalkedOnStepModified);
                }
            }
            try {
                this.doBlockCollisions();
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport((Throwable)throwable, (String)"Checking entity block collision");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }
            boolean flag1 = this.isWet();
            if (this.world.isFlammableWithin(this.getEntityBoundingBox().shrink(0.001))) {
                this.dealFireDamage(1);
                if (!flag1) {
                    ++this.fire;
                    if (this.fire == 0) {
                        this.setFire(8);
                    }
                }
            } else if (this.fire <= 0) {
                this.fire = -this.getFireImmuneTicks();
            }
            if (flag1 && this.isBurning()) {
                this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7f, 1.6f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                this.fire = -this.getFireImmuneTicks();
            }
            this.world.profiler.endSection();
        }
    }

    @Inject(method = "isUser", at = @At("HEAD"), cancellable = true)
    private void overrideIsUser(CallbackInfoReturnable<Boolean> cir) {
        if (CameraUtil.freecamEnabled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isCurrentViewEntity", at = @At("HEAD"), cancellable = true)
    private void allowPlayerMovementInFreeCameraMode(CallbackInfoReturnable<Boolean> cir) {
        if (CameraUtil.freecamEnabled()) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;closeScreen()V"))
    public void closeScreen(EntityPlayerSP player) {
        if (!PortalChat.INSTANCE.isEnabled()) mc.player.closeScreen();
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    public void displayGuiScreen(Minecraft minecraft, GuiScreen screen) {
        if (!PortalChat.INSTANCE.isEnabled()) mc.displayGuiScreen((GuiScreen) null);
    }
}
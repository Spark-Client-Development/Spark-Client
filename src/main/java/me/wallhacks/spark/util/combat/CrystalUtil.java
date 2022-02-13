package me.wallhacks.spark.util.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.RaytraceUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import me.wallhacks.spark.util.objects.PredictedEntity;

import java.util.Arrays;
import java.util.List;

public class CrystalUtil implements MC {
    public static boolean rayTraceSolidCheck(Vec3d start, Vec3d end, boolean shouldIgnore, boolean preplace) {
        if (!Double.isNaN(start.x) && !Double.isNaN(start.y) && !Double.isNaN(start.z)) {
            if (!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
                int currX = MathHelper.floor(start.x);
                int currY = MathHelper.floor(start.y);
                int currZ = MathHelper.floor(start.z);

                int endX = MathHelper.floor(end.x);
                int endY = MathHelper.floor(end.y);
                int endZ = MathHelper.floor(end.z);

                BlockPos blockPos = new BlockPos(currX, currY, currZ);
                IBlockState blockState = mc.world.getBlockState(blockPos);
                net.minecraft.block.Block block = blockState.getBlock();

                if ((blockState.getCollisionBoundingBox(mc.world, blockPos) != Block.NULL_AABB) &&
                        block.canCollideCheck(blockState, false) && (getBlocks().contains(block) || !shouldIgnore) && (!isBreakBlock(blockPos) || !preplace)) {
                    return true;
                }

                double seDeltaX = end.x - start.x;
                double seDeltaY = end.y - start.y;
                double seDeltaZ = end.z - start.z;

                int steps = 200;

                while (steps-- >= 0) {
                    if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) return false;
                    if (currX == endX && currY == endY && currZ == endZ) return false;

                    boolean unboundedX = true;
                    boolean unboundedY = true;
                    boolean unboundedZ = true;

                    double stepX = 999.0;
                    double stepY = 999.0;
                    double stepZ = 999.0;
                    double deltaX = 999.0;
                    double deltaY = 999.0;
                    double deltaZ = 999.0;

                    if (endX > currX) {
                        stepX = currX + 1.0;
                    } else if (endX < currX) {
                        stepX = currX;
                    } else {
                        unboundedX = false;
                    }

                    if (endY > currY) {
                        stepY = currY + 1.0;
                    } else if (endY < currY) {
                        stepY = currY;
                    } else {
                        unboundedY = false;
                    }

                    if (endZ > currZ) {
                        stepZ = currZ + 1.0;
                    } else if (endZ < currZ) {
                        stepZ = currZ;
                    } else {
                        unboundedZ = false;
                    }

                    if (unboundedX) deltaX = (stepX - start.x) / seDeltaX;
                    if (unboundedY) deltaY = (stepY - start.y) / seDeltaY;
                    if (unboundedZ) deltaZ = (stepZ - start.z) / seDeltaZ;

                    if (deltaX == 0.0) deltaX = -1.0e-4;
                    if (deltaY == 0.0) deltaY = -1.0e-4;
                    if (deltaZ == 0.0) deltaZ = -1.0e-4;

                    EnumFacing facing;

                    if (deltaX < deltaY && deltaX < deltaZ) {
                        facing = endX > currX ? EnumFacing.WEST : EnumFacing.EAST;
                        start = new Vec3d(stepX, start.y + seDeltaY * deltaX, start.z + seDeltaZ * deltaX);
                    } else if (deltaY < deltaZ) {
                        facing = endY > currY ? EnumFacing.DOWN : EnumFacing.UP;
                        start = new Vec3d(start.x + seDeltaX * deltaY, stepY, start.z + seDeltaZ * deltaY);
                    } else {
                        facing = endZ > currZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        start = new Vec3d(start.x + seDeltaX * deltaZ, start.y + seDeltaY * deltaZ, stepZ);
                    }

                    currX = MathHelper.floor(start.x) - (facing == EnumFacing.EAST ? 1 : 0);
                    currY = MathHelper.floor(start.y) - (facing == EnumFacing.UP ? 1 : 0);
                    currZ = MathHelper.floor(start.z) - (facing == EnumFacing.SOUTH ? 1 : 0);

                    blockPos = new BlockPos(currX, currY, currZ);
                    blockState = mc.world.getBlockState(blockPos);
                    block = blockState.getBlock();

                    if (block.canCollideCheck(blockState, false) && (getBlocks().contains(block) || !shouldIgnore) && (!isBreakBlock(blockPos) || !preplace)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean isBreakBlock(BlockPos pos) {
        for (DestroyBlockProgress progress : mc.renderGlobal.damagedBlocks.values()) {
            if (progress.getPosition().equals(pos)) return true;
        }
        return false;
    }

    public static float getDamageFromDifficulty(float damage) {
        switch (mc.world.getDifficulty()) {
            case PEACEFUL:
                return 0;
            case EASY:
                return Math.min(damage / 2 + 1, damage);
            case HARD:
                return damage * 3 / 2;
            default:
                return damage;
        }
    }

    public static AxisAlignedBB PredictCrystalBBFromPos(Vec3d crystal) {
        return new AxisAlignedBB(crystal.x - 0.6, crystal.y, crystal.z - 0.6, crystal.x + 0.6, crystal.y + 1.2, crystal.z + 0.6);
    }

    //use this to calculate damage for prediction
    public static float calculateDamageCrystal(Vec3d expolsionPoint, PredictedEntity predicted, boolean prePlace) {
        return calculateDamageCrystal(expolsionPoint, predicted.entity, predicted.predictedBB, prePlace);
    }

    public static float calculateDamageCrystal(Vec3d expolsionPoint, EntityLivingBase entity, AxisAlignedBB predicted, boolean prePlace) {

        float finald = 0;

        //save old bb
        AxisAlignedBB realBB = entity.boundingBox;
        entity.boundingBox = predicted;
        entity.resetPositionToBB();

        //damage calculation
        finald = calculateDamageCrystal(expolsionPoint, entity, true, prePlace);
        //set enttiy bb back to normal one
        entity.boundingBox = realBB;
        entity.resetPositionToBB();

        return finald;
    }


    public static float calculateDamageCrystal(EntityEnderCrystal crystal, Entity target, boolean shouldIgnore) {
        return calculateDamageCrystal(new Vec3d(crystal.posX, crystal.posY, crystal.posZ), target, shouldIgnore, false);
    }

    public static float calculateDamageCrystal(BlockPos pos, Entity target, boolean shouldIgnore, boolean prePlace) {
        return calculateDamageCrystal(new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5), target, shouldIgnore, prePlace);
    }

    public static float calculateDamageCrystal(Vec3d explosionPosition, Entity target, boolean shouldIgnore, boolean prePlace) {
        return getExplosionDamage(target, explosionPosition, 6.0f, shouldIgnore, prePlace);
    }


    public static float getExplosionDamage(Entity targetEntity, Vec3d explosionPosition, float explosionPower, boolean shouldIgnore, boolean preplace) {
        Vec3d entityPosition = new Vec3d(targetEntity.posX, targetEntity.posY, targetEntity.posZ);
        if (targetEntity.isImmuneToExplosions()) return 0.0f;
        explosionPower *= 2.0f;
        double distanceToSize = entityPosition.distanceTo(explosionPosition) / explosionPower;
        double blockDensity = 0.0;
        // Offset to "fake position"
        AxisAlignedBB bbox = targetEntity.getEntityBoundingBox().offset(targetEntity.getPositionVector().subtract(entityPosition));
        Vec3d bboxDelta = new Vec3d(
                1.0 / ((bbox.maxX - bbox.minX) * 2.0 + 1.0),
                1.0 / ((bbox.maxY - bbox.minY) * 2.0 + 1.0),
                1.0 / ((bbox.maxZ - bbox.minZ) * 2.0 + 1.0)
        );

        double xOff = (1.0 - Math.floor(1.0 / bboxDelta.x) * bboxDelta.x) / 2.0;
        double zOff = (1.0 - Math.floor(1.0 / bboxDelta.z) * bboxDelta.z) / 2.0;

        if (bboxDelta.x >= 0.0 && bboxDelta.y >= 0.0 && bboxDelta.z >= 0.0) {
            int nonSolid = 0;
            int total = 0;

            for (double x = 0.0; x <= 1.0; x += bboxDelta.x) {
                for (double y = 0.0; y <= 1.0; y += bboxDelta.y) {
                    for (double z = 0.0; z <= 1.0; z += bboxDelta.z) {
                        Vec3d startPos = new Vec3d(
                                xOff + bbox.minX + (bbox.maxX - bbox.minX) * x,
                                bbox.minY + (bbox.maxY - bbox.minY) * y,
                                zOff + bbox.minZ + (bbox.maxZ - bbox.minZ) * z
                        );

                        if (!rayTraceSolidCheck(startPos, explosionPosition, shouldIgnore, preplace)) ++nonSolid;
                        ++total;
                    }
                }
            }
            blockDensity = (double) nonSolid / (double) total;
        }

        double densityAdjust = (1.0 - distanceToSize) * blockDensity;
        float damage = (float) (int) ((densityAdjust * densityAdjust + densityAdjust) / 2.0 * 7.0 * explosionPower + 1.0);

        if (targetEntity instanceof EntityLivingBase)
            damage = getBlastReduction((EntityLivingBase) targetEntity, CrystalUtil.getDamageFromDifficulty(damage),
                    new Explosion(mc.world, null, explosionPosition.x, explosionPosition.y, explosionPosition.z,
                            explosionPower / 2.0f, false, true));

        return damage;
    }


    public static List<Block> getBlocks() {
        return Arrays.asList(
                Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.COMMAND_BLOCK, Blocks.BARRIER, Blocks.ENCHANTING_TABLE, Blocks.ENDER_CHEST, Blocks.END_PORTAL_FRAME, Blocks.BEACON, Blocks.ANVIL
        );
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(),
                (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

        float enchantmentModifierDamage = 0.0f;
        try {
            enchantmentModifierDamage = (float) EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(),
                    DamageSource.causeExplosionDamage(explosion));
        } catch (Exception ignored) {
        }
        enchantmentModifierDamage = MathHelper.clamp(enchantmentModifierDamage, 0.0f, 20.0f);

        damage *= 1.0f - enchantmentModifierDamage / 25.0f;
        PotionEffect resistanceEffect = entity.getActivePotionEffect(MobEffects.RESISTANCE);

        if (entity.isPotionActive(MobEffects.RESISTANCE) && resistanceEffect != null)
            damage = damage * (25.0f - (resistanceEffect.getAmplifier() + 1) * 5.0f) / 25.0f;

        damage = Math.max(damage, 0.0f);
        return damage;
    }

    public static void breakCrystal(EntityEnderCrystal target, BlockPos bestPos){
        Vec3d pos = target.getPositionEyes(mc.getRenderPartialTicks());
        if (bestPos != null)
            pos = CrystalUtil.getRotationPos(false, bestPos, target);
        else {
            List<Vec3d> vecs = RaytraceUtil.getVisiblePointsForEntity(target);
            if (!vecs.isEmpty())
                pos = PlayerUtil.getClosestPoint(vecs);
        }


        //rotate if needed
        if (!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(pos), AntiCheatConfig.getInstance().getCrystalRotStep(), 4, false, true))
            return;

        mc.player.connection.sendPacket(new CPacketUseEntity(target));

        //swing
        breakSwing();
    }

    public static void breakSwing() {
        EnumHand hand = EnumHand.OFF_HAND;
        switch (AntiCheatConfig.getInstance().crystalBreakHand.getValue()) {
            case "Both":
                if (mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal))
                    hand = EnumHand.OFF_HAND;

                break;
            case "OffHand":
                hand = EnumHand.OFF_HAND;
                break;
            case "MainHand":
                hand = EnumHand.MAIN_HAND;
                break;
        }

        switch (AntiCheatConfig.getInstance().crystalBreakSwing.getValue()) {
            case "Normal":
                mc.player.swingArm(hand);
                break;
            case "Packet":
                mc.player.connection.sendPacket(new CPacketAnimation(hand));
                break;
        }
    }

    public static Vec3d getRotationPos(boolean forBreak, BlockPos placePos, EntityEnderCrystal crystal) {
        if (placePos != null)
            if (crystal == null || crystal.getPosition().add(0, -1, 0).equals(placePos)) {
                Vec3d pos = RaytraceUtil.getPointToBreakPlaceCrystal(placePos);

                if (pos != null) return pos;
                else if (crystal == null)
                    return new Vec3d(placePos.getX() + 0.5, placePos.getY() + 1, placePos.getZ() + 0.5);
                ;
            }

        if (forBreak || placePos == null) {
            if (crystal != null) {
                Vec3d pos = PlayerUtil.getClosestPoint(RaytraceUtil.getVisiblePointsForBox(CrystalUtil.PredictCrystalBBFromPos(crystal.getPositionVector())));
                if (pos != null) return pos;
                return crystal.getPositionVector();
            }
        } else {
            if (placePos != null) {
                Vec3d pos = PlayerUtil.getClosestPoint(RaytraceUtil.getPointToLookAtBlock(placePos));
                if (pos != null) return pos;
                else
                    return new Vec3d(placePos.getX() + 0.5, placePos.getY() + 1, placePos.getZ() + 0.5);
            }
        }

        return null;
    }


}

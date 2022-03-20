package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.manager.RotationManager;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.exploit.PacketMine;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.combat.CrystalUtil;
import me.wallhacks.spark.util.combat.HoleUtil;
import me.wallhacks.spark.util.objects.Pair;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.RaytraceUtil;
import me.wallhacks.spark.util.player.RotationUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.ShulkerSwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecBlockSwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecItemSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Module.Registration(name = "ShulkerAura", description = "uses shulkers to push crystals in to blocks and kill enemies")
public class ShulkerAura extends Module {
    public static ShulkerAura INSTANCE;
    BooleanSetting craft = new BooleanSetting("Craft", this, false);
    IntSetting shulkerSlot = new IntSetting("Slot", this, 8, 1, 9);
    BlockPos targetPos;
    EnumFacing targetFacing;
    int windowId = -1;
    int delay = 0;
    private boolean flag = false;

    public ShulkerAura() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        windowId = -1;
        AntiCheatConfig cfg = AntiCheatConfig.getInstance();
        targetPos = null;
        targetFacing = null;
        String message = "Could not find target";
        for (EntityPlayer player : mc.world.playerEntities) {
            if (mc.player.getDistance(player) > 6) continue;
            if (mc.player == player) continue;
            if (Spark.socialManager.isFriend(player)) continue;
            //i dont like this but its a really easy check for now
            if (!HoleUtil.isInHole(player)) continue;
            BlockPos pos = new BlockPos(player.posX, player.posY + 2, player.posZ);
            //check every direction for shulker aura posibilitys
            EnumFacing face = EnumFacing.UP;
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                //check places being possible to place at

                //blocks oppisite of the trap need to be air to prevent the shulker from being blown up
                BlockPos blockedCheck = pos.offset(facing.getOpposite());
                if (!(mc.world.getBlockState(blockedCheck).getBlock() instanceof BlockAir))
                    continue;
                if (!(mc.world.getBlockState(blockedCheck.up()).getBlock() instanceof BlockAir))
                    continue;
                boolean blocked = false;
                for (EnumFacing f : EnumFacing.HORIZONTALS) {
                    if (f == facing || f == facing.getOpposite()) continue;
                    if (!(mc.world.getBlockState(blockedCheck.offset(f)).getBlock() instanceof BlockAir) || !(mc.world.getBlockState(blockedCheck.offset(f).up()).getBlock() instanceof BlockAir)) {
                        blocked = true;
                        break;
                    }
                }
                if (blocked) continue;

                //actual block needs to be air so we can place crystals
                if (!(mc.world.getBlockState(pos.offset(facing)).getBlock() instanceof BlockAir && BlockInteractUtil.blockCollisionCheck(pos.offset(facing), null)))
                    continue;

                //block above it needs to be air too
                if (!(mc.world.getBlockState(pos.add(facing.getDirectionVec()).up()).getBlock() instanceof BlockAir))
                    continue;

                //block underneath needs to be air bedrock or obby so we can actually place crystal there after trapping
                Block placeBlock = mc.world.getBlockState(pos.offset(facing).down()).getBlock();
                if (!BlockInteractUtil.blockCollisionCheck(pos.offset(facing).down(), true) || !BlockInteractUtil.blockCollisionCheck(pos.offset(facing), true) || !BlockInteractUtil.blockCollisionCheck(pos.offset(facing, 2).down(), true)) continue;
                if (!(placeBlock instanceof BlockAir || placeBlock instanceof BlockObsidian || placeBlock == Blocks.BEDROCK) && BlockInteractUtil.blockCollisionCheck(pos.offset(facing).down(), null))
                    continue;

                //the position 2 blocks out needs to be air for placing the shulk
                BlockPos shulkPos = pos.offset(facing, 2);
                BlockPos outerPos = pos.offset(facing, 3);
                Block shulkBlock = mc.world.getBlockState(shulkPos).getBlock();
                if (!(shulkBlock instanceof BlockShulkerBox)) {
                    if (!shulkBlock.isReplaceable(mc.world, shulkPos) || !BlockInteractUtil.blockCollisionCheck(shulkPos, null))
                        continue;
                } else outerPos = shulkPos;
                message = "Found target to attack but its to far away";
                //raytrace and range checks
                RayTraceResult outer = mc.world.rayTraceBlocks(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(outerPos).add(0.5, 0.5, 0.5));
                double distance = mc.player.getDistance(outerPos.getX() + 0.5, outerPos.getY() + 0.5, outerPos.getZ() + 0.5);
                if (distance < cfg.placeRange.getValue() && (AntiCheatConfig.getInstance().raytrace.getValue() || outer == null || outerPos.equals(outer.getBlockPos()) || distance < cfg.placeWallRange.getValue())) {
                    face = facing;
                    break;
                }
            }
            if (face != EnumFacing.UP) {
                targetPos = pos;
                targetFacing = face;
                PacketMine.instance.pos = null;
                return;
            }
        }
        Spark.sendInfo(message);
        disable();
    }

    @SubscribeEvent
    void onUpdate(PlayerUpdateEvent event) {
        if (targetPos == null) {
            disable();
            return;
        }
        boolean disable = true;
        for (EntityPlayer e : mc.world.playerEntities) {
            if (!e.isDead)
                if (new BlockPos(e.posX, e.posY + 2, e.posZ).equals(targetPos)) {
                    disable = false;
                    break;
                }
        }
        if (disable) {
            Spark.sendInfo("No more target in the trap");
            this.disable();
        }
        //trap player on sides...
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos pos = targetPos.down().add(facing.getDirectionVec());
            Block block = mc.world.getBlockState(pos).getBlock();
            if (block != Blocks.AIR)
                continue;
            BlockInteractUtil.BlockPlaceResult r = BlockInteractUtil.tryPlaceBlock(pos, new SpecBlockSwitchItem(Blocks.OBSIDIAN), true);
            if (r != BlockInteractUtil.BlockPlaceResult.FAILED) return;
        }

        //trap the upperblock...
        if (mc.world.getBlockState(targetPos).getBlock() == Blocks.AIR) {
            BlockInteractUtil.BlockPlaceResult r = BlockInteractUtil.tryPlaceBlock(targetPos, new SpecBlockSwitchItem(Blocks.OBSIDIAN),  true);
            if (r == BlockInteractUtil.BlockPlaceResult.FAILED) {
                //failed lets add support
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    if (targetFacing == facing || targetFacing == facing.getOpposite()) continue;
                    BlockPos pos = targetPos.add(facing.getDirectionVec());
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (block != Blocks.AIR)
                        continue;
                    BlockInteractUtil.BlockPlaceResult r2 = BlockInteractUtil.tryPlaceBlock(pos, new SpecBlockSwitchItem(Blocks.OBSIDIAN),  true);
                    if (r2 != BlockInteractUtil.BlockPlaceResult.FAILED) return;
                }
            } else return;
        }

        //check if the shulk is there
        if (mc.world.getBlockState(targetPos.offset(targetFacing).offset(targetFacing)).getBlock() instanceof BlockShulkerBox) {
            doShulkerAura();
        } else {
            flag = false;
            int slot = -1;
            if (mc.currentScreen == null) {
                for (int i = 0; i < 9; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemShulkerBox) {
                        slot = i;
                        break;
                    }
                }
            }
            if (slot != -1 || !craft.getValue()) {
                BlockPos support = targetPos.add(targetFacing.getDirectionVec()).add(targetFacing.getDirectionVec()).add(targetFacing.getDirectionVec());
                if (mc.world.getBlockState(support).getBlock().isReplaceable(mc.world, support)) {
                    if (BlockInteractUtil.tryPlaceBlock(support, new SpecBlockSwitchItem(Blocks.OBSIDIAN),  true) == BlockInteractUtil.BlockPlaceResult.FAILED) {
                        if (BlockInteractUtil.tryPlaceBlock(support.down(), new SpecBlockSwitchItem(Blocks.OBSIDIAN),  true) == BlockInteractUtil.BlockPlaceResult.FAILED) {
                            if (BlockInteractUtil.tryPlaceBlock(support.down().offset(targetFacing.getOpposite()), new SpecBlockSwitchItem(Blocks.OBSIDIAN), true) == BlockInteractUtil.BlockPlaceResult.FAILED) {
                                //wtf i hope we never get here but nothing left to do but return ig
                            } else return;
                        } else return;
                    } else return;
                } else {
                    BlockInteractUtil.tryPlaceBlockOnBlock(support.offset(targetFacing.getOpposite()), targetFacing.getOpposite(), new ShulkerSwitchItem(), false, true,true);
                }
            } else if (craft.getValue() && mc.currentScreen == null) {
                boolean flag = false;
                BlockPos craftPos = null;
                double best = 0;
                for (BlockPos pos : WorldUtils.getSphere(PlayerUtil.getPlayerPosFloored(mc.player), 6, 6, 1)) {
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.CRAFTING_TABLE) {
                        Pair<Vec3d, EnumFacing> pair = BlockInteractUtil.getInteractPoint(pos);
                        if (pair != null) {
                            craftPos = pos;
                            flag = true;
                            break;
                        }
                    }
                    if (!BlockInteractUtil.canPlaceBlockAtPos(pos, true)) continue;
                    if (BlockInteractUtil.getDirForPlacingBlockAtPos(pos) == null) continue;
                    if (pos.equals(targetPos.offset(targetFacing)) || pos.equals(targetPos.offset(targetFacing, 2)) || pos.equals(targetPos.offset(targetFacing).down()))  continue;
                    double distance = mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    if (craftPos == null || distance < best) {
                        best = distance;
                        craftPos = pos;
                    }
                }
                if (flag || BlockInteractUtil.tryPlaceBlock(craftPos, new SpecBlockSwitchItem(Blocks.CRAFTING_TABLE),  true) == BlockInteractUtil.BlockPlaceResult.PLACED) {
                    Vec3d pos = new Vec3d(craftPos).add(0.5, 0.5, 0.5);
                    List<Vec3d> vecs = RaytraceUtil.getVisiblePointsForBox(new AxisAlignedBB(craftPos));
                    if (!vecs.isEmpty())
                        pos = PlayerUtil.getClosestPoint(vecs);
                    final RayTraceResult result = mc.world.rayTraceBlocks(PlayerUtil.getEyePos(), pos, false, true, false);
                    EnumFacing facing = (result == null || !craftPos.equals(result.getBlockPos()) || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
                    if (flag && !Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(pos), true))
                        return;
                    this.flag = false;
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(craftPos, facing, EnumHand.MAIN_HAND, (float) pos.x, (float) pos.y, (float) pos.z));
                }
            } else if (craft.getValue() && mc.currentScreen instanceof GuiCrafting) {
                int id = mc.player.openContainer.windowId;
                if (mc.player.openContainer.getSlot(0).getStack().getItem() instanceof ItemShulkerBox) {
                    mc.playerController.windowClick(id, 0, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(id, 36 + shulkerSlot.getValue(), 0, ClickType.PICKUP, mc.player);
                    mc.player.closeScreen();
                } else if (!flag) {
                    flag = true;
                    IRecipe recipe = CraftingManager.getRecipe(new ResourceLocation("minecraft:purple_shulker_box"));
                    mc.player.connection.sendPacket(new CPacketPlaceRecipe(id, recipe, false));
                }
            }
        }
    }

    public void doShulkerAura() {
        //loop through all crystals and find the one we need
        EntityEnderCrystal crystal = null;
        int crystalState = -1;
        for (Entity e : mc.world.loadedEntityList) {
            if (e instanceof EntityEnderCrystal) {
                if (e.getPosition().equals(targetPos.offset(targetFacing))) {
                    crystal = (EntityEnderCrystal) e;
                    crystalState = 0;
                } else if (new AxisAlignedBB(targetPos).contains(e.getPositionVector().add(0, 0.1, 0))) {
                    crystal = (EntityEnderCrystal) e;
                    crystalState = 1;
                }
            }
        }
        if (crystalState == 1) {
            if (delay == 0) {
                if (CrystalUtil.breakCrystal(crystal, null)) delay = 3;
            } else delay--;
            if (windowId != -1)
                mc.player.connection.sendPacket(new CPacketCloseWindow(windowId));
            windowId = -1;
        } else if (crystalState == 0) {
            delay = 0;
            if (!flag)
                openShulk(targetPos.offset(targetFacing).offset(targetFacing));
            else flag = false;
        }
        if (crystalState == -1) {
            flag = false;
            delay = 0;
            if (placeCrystal(targetPos.offset(targetFacing).down())) return;
            else {
                Spark.sendInfo("No crystals found to place");
                disable();
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketOpenWindow) {
            SPacketOpenWindow p = event.getPacket();
            if (p.getGuiId().equals("minecraft:shulker_box")) {
                windowId = p.getWindowId();
                event.setCanceled(true);
            }
        }
    }

    @Override
    public void onDisable() {
        if (windowId != -1) {
            mc.player.connection.sendPacket(new CPacketCloseWindow(windowId));
            windowId = -1;
        }
    }

    private void openShulk(BlockPos shulkPos) {
        Vec3d pos = new Vec3d(shulkPos).add(0.5, 0.5, 0.5);
        List<Vec3d> vecs = RaytraceUtil.getVisiblePointsForBox(mc.world.getBlockState(shulkPos).getBoundingBox(mc.world, shulkPos));
        if (!vecs.isEmpty())
            pos = PlayerUtil.getClosestPoint(vecs);
        final RayTraceResult result = mc.world.rayTraceBlocks(PlayerUtil.getEyePos(), pos, false, true, false);
        EnumFacing facing = (result == null || !shulkPos.equals(result.getBlockPos()) || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        //rotate if needed
        if (!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(pos), true)) {
            return;
        }

        //send packet
        EnumHand hand = EnumHand.OFF_HAND;
        if (mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal))
            hand = EnumHand.MAIN_HAND;
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(shulkPos, facing, hand, (float) pos.x, (float) pos.y, (float) pos.z));
        flag = true;
    }

    private boolean placeCrystal(BlockPos placePos) {
        Vec3d pos = CrystalUtil.getRotationPos(true, placePos, null);
        if (pos == null)
            pos = new Vec3d(placePos).add(0.5, 1, 0.5);
        final RayTraceResult result = mc.world.rayTraceBlocks(PlayerUtil.getEyePos(), pos, false, true, false);
        EnumFacing facing = (result == null || !placePos.equals(result.getBlockPos()) || result.sideHit == null) ? EnumFacing.UP : result.sideHit;

        Vec3d v = new Vec3d(placePos).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
        if (result != null && placePos.equals(result.getBlockPos()) && result.hitVec != null)
            v = result.hitVec;

        //offhand
        EnumHand hand = Spark.switchManager.Switch(new SpecItemSwitchItem(Items.END_CRYSTAL), ItemSwitcher.usedHand.Both, ItemSwitcher.switchType.Normal);
        if (hand == null) {
            disable();
            return false;
        }


        //rotate if needed
        if (!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(pos), true)) {
            return true;
        }

        //send packet
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, facing, hand, (float) v.x, (float) v.y, (float) v.z));

        //swing da arms
        switch (AntiCheatConfig.getInstance().placeSwing.getValue()) {
            case "Normal":
                mc.player.swingArm(hand);
                break;
            case "Packet":
                mc.player.connection.sendPacket(new CPacketAnimation(hand));
                break;
        }

        return true;
    }

    public boolean isInAttackZone(EntityPlayer player) {
        if (isEnabled() && targetPos != null) {
            BlockPos floored = PlayerUtil.getPlayerPosFloored(player);
            if (floored.add(0, 2, 0).equals(targetPos))
                return true;
        }
        return false;
    }
}
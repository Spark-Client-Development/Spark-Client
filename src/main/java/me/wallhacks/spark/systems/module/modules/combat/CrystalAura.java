package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.EntityAddEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.combat.CrystalUtil;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.objects.PredictedEntity;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.RaytraceUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.ItemForFightSwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecItemSwitchItem;
import me.wallhacks.spark.util.render.EspUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;

@Module.Registration(name = "CrystalAura", description = "Superior module litarly best ever")
public class CrystalAura extends Module {
    IntSetting prediction = new IntSetting("Prediction", this, 0, 0, 20, "Prediction");
    BooleanSetting prePlace = new BooleanSetting("PrePlace", this, true, "Prediction");

    BooleanSetting PlayersOnly = new BooleanSetting("PlayersOnly", this, true, "Attacking");


    IntSetting maxSelfdamage = new IntSetting("MaxSelfDam", this, 15, 0, 20, "Attacking");
    IntSetting minEnemydamage = new IntSetting("MinEnemyDam", this, 6, 0, 20, "Attacking");
    IntSetting FacePlaceHealth = new IntSetting("FacePlaceH", this, 5, 0, 36, "Attacking");
    DoubleSetting MinDamFacePlace = new DoubleSetting("MinDamFace", this, 0.5, 0, 10, "Attacking");

    DoubleSetting ProtectSelf = new DoubleSetting("ProtectSelf", this, 0.5, 0, 1, "Attacking");

    BooleanSetting breakArmour = new BooleanSetting("BreakArmour", this, true, "Attacking");
    BooleanSetting NoSuicide = new BooleanSetting("NoSuicide", this, true, "Attacking");

    BooleanSetting Replace = new BooleanSetting("InstantReplace", this, true, "Time");

    IntSetting breakCooldown = new IntSetting("BreakCooldown", this, 6, 0, 10, "Time");
    IntSetting placeCooldown = new IntSetting("PlaceCooldown", this, 6, 0, 10, "Time");

    IntSetting breakDelay = new IntSetting("BreakDelay", this, 1, 0, 5, "Time");
    IntSetting placeDelay = new IntSetting("PlaceDelay", this, 1, 0, 5, "Time");

    ModeSetting Switch = new ModeSetting("Switch", this, "Auto", Arrays.asList("Off", "Auto", "Silent", "NoGap"), "Other");


    BooleanSetting InstantBreak = new BooleanSetting("InstantBreak", this, false, "Time");

    ModeSetting render = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Fancy", "Off"), "Render");
    ColorSetting fill = new ColorSetting("Fill", this, new Color(0x38DC5E5E, true), "Render");
    ColorSetting outline = new ColorSetting("Outline", this, new Color(0x91F60A51, true), "Render");


    //enemies predicted
    ArrayList<PredictedEntity> predictedEnemies = new ArrayList<>();
    PredictedEntity predictedPlayer;
    public static EntityLivingBase targetEntity = null;


    //targets
    EntityEnderCrystal lastAttackedEntity = null;
    EntityEnderCrystal currentCrystalEntity = null;
    BlockPos currentCrystalBlockPos = null;
    EnumFacing renderVec = null;

    //tick timers
    int DelayTimer = 0;
    int PlacePauseTimer = 0;
    int BreakPauseTimer = 0;
    int PlaceTries = 0;

    boolean isUpdate = false;


    //use this event for good rotation support
    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        //get enemies list and run ca
        predictTarget();

        //isUpdate is rotations thing, it needs that for better rotating
        isUpdate = true;

        //run ca loop
        ca();

        isUpdate = false;
    }

    //main ca logic
    void ca() {

        DelayTimer++;

        if (BreakPauseTimer > 0)
            BreakPauseTimer--;
        if (PlacePauseTimer > 0)
            PlacePauseTimer--;


        //get crystal  entity to break
        getCrystalEntityTarget();
        //break that entity if we can
        doBreak();


        //if broke crystal or sent packet and replace is on we can place it
        if (currentCrystalEntity == null || (Replace.isOn() && currentCrystalEntity == lastAttackedEntity)) {
            //get ca pos
            getCrystalPlacePos();
            //place it
            doPlace();
        }

        if (currentCrystalBlockPos != null && render.is("Normal"))
            new FadePos(currentCrystalBlockPos, outline, fill, 50, true);

        //rotate every tick to keep looking at crystal
        if (currentCrystalEntity != null || currentCrystalBlockPos != null)
            rotate(true);
    }

    //spawn entity event
    //todo: make it only fire on clinet side, in singleplayer it also fires on server side rn
    //VVVVVVVVVVVVVVVVVVVVVVV did it maybe to lazy to test tbh
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void spawnEntity(EntityAddEvent event) {
        Entity e = event.getEntity();
        if (e instanceof EntityEnderCrystal) {
            EntityEnderCrystal c = (EntityEnderCrystal) e;
            try {
                //see if this crystal was planned to be placed
                if (c.getPosition().add(0, -1, 0).equals(currentCrystalBlockPos)) {
                    //reset to allow break
                    BreakPauseTimer = 0;
                    if (InstantBreak.isOn()) {
                        //if insta break is one we set break target and call break
                        if ( CrystalUtil.calculateDamageCrystal(c.getPositionVector(), predictedPlayer,false) > minEnemydamage.getValue()) {
                            currentCrystalEntity = c;
                            doBreak();
                        }
                    }

                }
            } catch (ConcurrentModificationException ex) {
                //prevent crash
                ex.printStackTrace();
            }

        }
    }

    @Override
    public void onEnable() {
        //reset values for ca to start
        currentCrystalBlockPos = null;
        currentCrystalEntity = null;
        lastAttackedEntity = null;
        PlacePauseTimer = 0;
        BreakPauseTimer = 0;
        PlaceTries = 0;
        DelayTimer = 0;
    }


    void predictTarget() {
        predictedPlayer = new PredictedEntity(MC.mc.player, prediction.getValue());
        predictedEnemies.clear();
        for (Entity e : MC.mc.world.loadedEntityList) {

            if (e instanceof EntityLivingBase)
                if (e != MC.mc.player)
                    if (!PlayersOnly.isOn() || e instanceof EntityPlayer)
                        if (AttackUtil.CanAttackEntity((EntityLivingBase) e, 15))
                            predictedEnemies.add(new PredictedEntity((EntityLivingBase) e, prediction.getValue()));
        }
    }


    void doPlace() {
        if (PlacePauseTimer <= 0 && DelayTimer >= placeDelay.getValue() && currentCrystalBlockPos != null) {
            //place crystal

            //get point for rotations
            Vec3d pos = getRotationPos(true);
            if (pos == null)
                pos = new Vec3d(currentCrystalBlockPos).add(0.5, 1, 0.5);
            final RayTraceResult result = MC.mc.world.rayTraceBlocks(PlayerUtil.getEyePos(), pos, false, true, false);
            EnumFacing facing = (result == null || !currentCrystalBlockPos.equals(result.getBlockPos()) || result.sideHit == null) ? EnumFacing.UP : result.sideHit;

            Vec3d v = new Vec3d(currentCrystalBlockPos).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));

            if (result != null && currentCrystalBlockPos.equals(result.getBlockPos()) && result.hitVec != null)
                v = result.hitVec;
            if (currentCrystalBlockPos.getY() >= 254)
                facing = EnumFacing.EAST;
            renderVec = facing;

            //save old slot
            int oldSlot = MC.mc.player.inventory.currentItem;

            //offhand
            EnumHand hand = ItemSwitcher.Switch(new SpecItemSwitchItem(Items.END_CRYSTAL), Switch.isValueName("Off") ? ItemSwitcher.switchType.NoSwitch : (Switch.isValueName("NoGap") && MC.mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE ? ItemSwitcher.switchType.Offhand : ItemSwitcher.switchType.Both));
            if (hand == null)
                return;


            //rotate if needed
            if (!rotate(false))
                return;


            //send packet
            MC.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(currentCrystalBlockPos, facing, hand, (float) v.x, (float) v.y, (float) v.z));

            //swing
            switch (AntiCheatConfig.getInstance().CrystalPlaceSwing.getValue()) {
                case "Normal":
                    MC.mc.player.swingArm(hand);
                    break;
                case "Packet":
                    MC.mc.player.connection.sendPacket(new CPacketAnimation(hand));
                    break;
            }

            //if silent switch we switch back to old slot
            if (Switch.isValueName("Silent")) {
                MC.mc.player.inventory.currentItem = oldSlot;
                MC.mc.playerController.syncCurrentPlayItem();
            }

            //apply cooldown/timeout if failed 2 places
            PlaceTries++;
            if (PlaceTries >= 2)
                PlacePauseTimer = placeCooldown.getValue();

            //reset delay to wait time before next action
            DelayTimer = 0;
        }
    }

    void doBreak() {
        if (currentCrystalEntity != lastAttackedEntity)
            BreakPauseTimer = 0;
        if (BreakPauseTimer <= 0 && DelayTimer >= breakDelay.getValue())
            if (currentCrystalEntity != null) {
                //try break

                //rotate if needed
                if (!rotate(true))
                    return;

                //weakness thing
                if (MC.mc.player.isPotionActive(MobEffects.WEAKNESS) && !(MC.mc.player.isPotionActive(MobEffects.STRENGTH) && MC.mc.player.getActivePotionEffect(MobEffects.STRENGTH).getAmplifier() >= 1)) {
                    ItemSwitcher.Switch(new ItemForFightSwitchItem(), ItemSwitcher.switchType.Mainhand);
                }

                //packets
                MC.mc.player.connection.sendPacket(new CPacketUseEntity(currentCrystalEntity));

                EnumHand hand = EnumHand.MAIN_HAND;


                switch (AntiCheatConfig.getInstance().CrystalBreakHand.getValue()) {
                    case "Both":
                        if (MC.mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal && !(MC.mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal))
                            hand = EnumHand.OFF_HAND;

                        break;
                    case "OffHand":
                        hand = EnumHand.OFF_HAND;
                        break;
                    case "MainHand":
                        hand = EnumHand.MAIN_HAND;
                        break;
                }




                switch (AntiCheatConfig.getInstance().CrystalBreakSwing.getValue()) {
                    case "Normal":
                        MC.mc.player.swingArm(hand);
                        break;
                    case "Packet":
                        MC.mc.player.connection.sendPacket(new CPacketAnimation(hand));
                }

                //set last attacked
                lastAttackedEntity = currentCrystalEntity;

                //set break coodown to precvent spam of packets
                BreakPauseTimer = breakCooldown.getValue();

                //place cooldown is set to 0 to replace
                PlacePauseTimer = 0;

                //place tries is reset
                PlaceTries = 0;

                //reset delay to wait time before next action
                DelayTimer = 0;
            }


    }


    void getCrystalEntityTarget() {
        currentCrystalEntity = null;


        float bestValue = 0;
        for (Object o : MC.mc.world.loadedEntityList.toArray()) {

            if (o instanceof EntityEnderCrystal) {
                EntityEnderCrystal e = (EntityEnderCrystal) o;

                if (canBreakCrystal(e)) {
                    float Value = getValueForCrystalExplodingAtPoint(e.getPositionVector(), false).value;


                    if (Value > bestValue) {
                        bestValue = Value;
                        currentCrystalEntity = e;
                    }//make sure we target latest spawned crystal
                    else if (Value == bestValue && currentCrystalEntity.ticksExisted > e.ticksExisted) {
                        bestValue = Value;
                        currentCrystalEntity = e;
                    }


                }
            }


        }

    }

    void getCrystalPlacePos() {
        BlockPos bestPos = null;
        targetEntity = null;
        float bestValue = 0;
        for (BlockPos pos : WorldUtils.getSphere(PlayerUtil.GetPlayerPosFloored(MC.mc.player), 7, 7, 1)) {
            if (CanPlaceOnBlock(pos)) {
                ValueForExplodingCrystalAtPoint Value = getValueForCrystalExplodingAtPoint(new Vec3d(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f), prePlace.getValue());

                if (Value.value > bestValue) {
                    bestValue = Value.value;
                    if(Value.target != null)
                        targetEntity = Value.target.entity;
                    bestPos = pos;
                }
            }
        }

        currentCrystalBlockPos = bestPos;

    }


    //methods to calculate crystal things

    class ValueForExplodingCrystalAtPoint {
        public final float value;
        public final PredictedEntity target;
        public ValueForExplodingCrystalAtPoint(PredictedEntity target,float value){
            this.value = value;
            this.target = target;
        }
    }

    ValueForExplodingCrystalAtPoint getValueForCrystalExplodingAtPoint(Vec3d pos, boolean prePlace) {
        float bestValue = -1;

        float myhealth = MC.mc.player.getHealth() + MC.mc.player.getAbsorptionAmount();
        float selfdam = CrystalUtil.calculateDamageCrystal(pos, predictedPlayer, false);
        PredictedEntity target = null;

        if (selfdam + 2 < myhealth || !NoSuicide.isOn())
            if (maxSelfdamage.getValue() > selfdam || maxSelfdamage.getValue() == 0) {

                for (PredictedEntity ct : predictedEnemies) {
                    EntityLivingBase e = ct.entity;
                    float Edam = CrystalUtil.calculateDamageCrystal(pos, ct, prePlace);

                    if (Edam > MinDamFacePlace.getValue())
                        if (FacePlaceHealth.getValue() > e.getHealth() + e.getAbsorptionAmount() || Edam > minEnemydamage.getValue() || (breakArmour.isOn() && AttackUtil.isArmourLow(e))) {
                            float Value = (float) (Edam - (selfdam * ProtectSelf.getValue()));

                            if (Value > bestValue) {
                                bestValue = Value;
                                target = ct;
                            }
                        }
                }


            }
        return new ValueForExplodingCrystalAtPoint(target,bestValue);
    }



    boolean CanPlaceOnBlock(BlockPos p) {

        final Block block = MC.mc.world.getBlockState(p).getBlock();
        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
            final Block floor = MC.mc.world.getBlockState(p.add(0, 1, 0)).getBlock();
            final Block ceil = MC.mc.world.getBlockState(p.add(0, 2, 0)).getBlock();

            //in the end crystal have a fire block in them

            if ((floor == Blocks.AIR || (floor == Blocks.FIRE && MC.mc.player.dimension == 1)) && ceil == Blocks.AIR) {
                double d0 = (double) p.getX();
                double d1 = (double) p.getY() + 1;
                double d2 = (double) p.getZ();
                double d0b = d0 + 1;
                double d1b = d1 + 2;
                double d2b = d2 + 1;

                AxisAlignedBB bb = new AxisAlignedBB(d0, d1, d2, d0b, d1b, d2b);
                for (Entity entity : MC.mc.world.getEntitiesWithinAABBExcludingEntity(null, bb)) {
                    if (entity.isDead) continue;

                    if (entity instanceof EntityEnderCrystal) {
                        if (lastAttackedEntity != null && lastAttackedEntity == currentCrystalEntity && lastAttackedEntity.getPosition().equals(entity.getPosition()))
                            continue;
                    }


                    return false;


                }


                Vec3d pos = PlayerUtil.getClosestPoint(RaytraceUtil.getPointToLookAtBlock(p));

                if (PlayerUtil.getDistance(p) > (pos != null ? AntiCheatConfig.getInstance().getCrystalPlaceRange() : AntiCheatConfig.getInstance().getCrystalWallRange()))
                    return false;
                //predict can break
                if (!canBreakCrystal(new Vec3d(p.getX() + 0.5, p.getY() + 1, p.getZ() + 0.5)))
                    return false;


                return true;

            }
        }

        return false;


    }

    boolean canBreakCrystal(EntityEnderCrystal e) {
        if (e == null || e.isDead)
            return false;
        if (canBreakCrystal(e.getPositionVector()))
            return true;
        return false;
    }

    boolean canBreakCrystal(Vec3d crystal) {

        if (!PlayerUtil.CanInteractVanillaCheck(crystal, 1.7f))
            return false;

        Vec3d pos = PlayerUtil.getClosestPoint(RaytraceUtil.getVisiblePointsForBox(CrystalUtil.PredictCrystalBBFromPos(crystal)));

        //if can see
        if (PlayerUtil.getDistance(crystal) > (pos != null ? AntiCheatConfig.getInstance().getCrystalBreakRange() : AntiCheatConfig.getInstance().getCrystalWallRange()))
            return false;

        return true;
    }


    //rotation methods
    boolean rotate(boolean forBreak) {
        if (!AntiCheatConfig.getInstance().CrystalRotate()) return true;
        Vec3d pos = getRotationPos(forBreak);


        if (pos == null) return false;
        return Spark.rotationManager.Rotate(Spark.rotationManager.getLegitRotations(pos), AntiCheatConfig.getInstance().getCrystalRotStep(), 4, false, isUpdate);
    }

    Vec3d getRotationPos(boolean forBreak) {
        if (currentCrystalBlockPos != null)
            if (currentCrystalEntity == null || currentCrystalEntity.getPosition().add(0, -1, 0).equals(currentCrystalBlockPos)) {
                Vec3d pos = RaytraceUtil.getPointToBreakPlaceCrystal(currentCrystalBlockPos);

                if (pos != null) return pos;
                else if (currentCrystalEntity == null)
                    return new Vec3d(currentCrystalBlockPos.getX() + 0.5, currentCrystalBlockPos.getY() + 1, currentCrystalBlockPos.getZ() + 0.5);
                ;
            }

        if (forBreak || currentCrystalBlockPos == null) {
            if (currentCrystalEntity != null) {
                Vec3d pos = PlayerUtil.getClosestPoint(RaytraceUtil.getVisiblePointsForBox(CrystalUtil.PredictCrystalBBFromPos(currentCrystalEntity.getPositionVector())));
                if (pos != null) return pos;
                return currentCrystalEntity.getPositionVector();
            }
        } else {
            if (currentCrystalBlockPos != null) {
                Vec3d pos = PlayerUtil.getClosestPoint(RaytraceUtil.getPointToLookAtBlock(currentCrystalBlockPos));
                if (pos != null) return pos;
                else
                    return new Vec3d(currentCrystalBlockPos.getX() + 0.5, currentCrystalBlockPos.getY() + 1, currentCrystalBlockPos.getZ() + 0.5);
            }
        }

        return null;
    }

    private AxisAlignedBB getFacingVec(EnumFacing facing, BlockPos pos) {
        switch (renderVec) {
            case UP:
                return new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
            case DOWN:
                return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY(), pos.getZ() + 1);
            case EAST:
                return new AxisAlignedBB(pos.getX() + 1, pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
            case WEST:
                return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + 1, pos.getZ() + 1);
            case NORTH:
                return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ());
            case SOUTH:
                return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ() + 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        }
        return null;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (currentCrystalBlockPos == null || renderVec == null || !render.is("Fancy")) return;
        AxisAlignedBB render = getFacingVec(renderVec, currentCrystalBlockPos);
        EspUtil.boundingESPBoxFilled(render, fill.getValue());
        EspUtil.boundingESPBox(render, outline.getValue(), 2.0f);
    }
}

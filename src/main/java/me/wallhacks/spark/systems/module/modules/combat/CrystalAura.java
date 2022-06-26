package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.EntityAddEvent;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.player.Offhand;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.combat.CrystalUtil;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.objects.Pair;
import me.wallhacks.spark.util.objects.PredictedEntity;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.RaytraceUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecItemSwitchItem;
import me.wallhacks.spark.util.render.EspUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;

import static me.wallhacks.spark.systems.module.modules.combat.CrystalAura.RotateType.*;

@Module.Registration(name = "CrystalAura", description = "Superior module litarly best ever")
public class CrystalAura extends Module {
    public static CrystalAura instance;
    SettingGroup predictionG = new SettingGroup("Prediction", this);
    IntSetting prediction = new IntSetting("Prediction", predictionG, 0, 0, 20);
    BooleanSetting prePlace = new BooleanSetting("PrePlace", predictionG, true);

    SettingGroup attackingG = new SettingGroup("Attacking", this);
    BooleanSetting PlayersOnly = new BooleanSetting("PlayersOnly", attackingG, true);
    IntSetting maxSelfdamage = new IntSetting("MaxSelfDam", attackingG, 15, 0, 20);
    IntSetting minEnemydamage = new IntSetting("MinEnemyDam", attackingG, 6, 0, 20);
    public DoubleSetting protectSelf = new DoubleSetting("ProtectSelf", attackingG, 0.5, 0, 1);
    BooleanSetting NoSuicide = new BooleanSetting("NoSuicide", attackingG, true);
    IntSetting switchThreshold = new IntSetting("SwitchThreshold", attackingG, 3, 0, 6);

    SettingGroup timeG = new SettingGroup("Timing", this);
    public IntSetting speed = new IntSetting("Speed", timeG, 18, 1, 20);
    public IntSetting breakCooldown = new IntSetting("BreakCooldown", timeG, 6, 0, 10);
    public IntSetting placeCooldown = new IntSetting("PlaceCooldown", timeG, 6, 0, 10);

    SettingGroup otherG = new SettingGroup("Other", this);
    public IntSetting placeTries = new IntSetting("PlaceTries", otherG, 2, 1, 5);
    public ModeSetting switchingMode = new ModeSetting("Switch", otherG, "Normal", ItemSwitcher.modes);
    public BooleanSetting instantReplace = new BooleanSetting("InstantReplace", otherG, true);
    public BooleanSetting InstantBreak = new BooleanSetting("InstantBreak", otherG, true);
    BooleanSetting DebugCs = new BooleanSetting("DebugSpeed", otherG, false);
    BooleanSetting Debug = new BooleanSetting("Debug", otherG, false);

    SettingGroup faceplaceG = new SettingGroup("FacePlace", this);
    public IntSetting breakArmor = new IntSetting("Armor", faceplaceG, 15, 0, 100);
    public IntSetting facePlaceHealth = new IntSetting("HP", faceplaceG, 5, 0, 36);
    KeySetting facePlaceKey = new KeySetting("Force", faceplaceG, -1);
    BooleanSetting slowFacePlace = new BooleanSetting("Slow", faceplaceG, true);

    SettingGroup renderG = new SettingGroup("Render", this);
    ModeSetting render = new ModeSetting("Mode", renderG, "Normal", Arrays.asList("Normal", "Fancy", "Off"));
    ColorSetting fill = new ColorSetting("Color", renderG, new Color(0x38DC5E5E, true));

    SettingGroup pauseG = new SettingGroup("Pause", this);
    BooleanSetting surround = new BooleanSetting("Surround", pauseG, false);
    BooleanSetting cevBreaker = new BooleanSetting("CEVBreaker", pauseG, true);
    BooleanSetting shulkerAura = new BooleanSetting("ShulkerAura", pauseG, true);
    IntSetting hp = new IntSetting("HP", pauseG, 3, 0, 20);
    BooleanSetting eating = new BooleanSetting("Eating", pauseG, false);
    BooleanSetting mining = new BooleanSetting("Mining", pauseG, false);
    //enemies predicted
    ArrayList<PredictedEntity> predictedEnemies = new ArrayList<>();
    PredictedEntity predictedPlayer;
    EntityLivingBase targetEntity = null;
    //targets
    EntityEnderCrystal lastAttackedEntity = null;
    EntityEnderCrystal currentCrystalEntity = null;
    BlockPos currentCrystalBlockPos = null;
    EnumFacing renderVec = null;
    FadePos currentPos = null;
    //tick timers
    int delayTimer = 0;
    int placePauseTimer = 0;
    int breakPauseTimer = 0;
    boolean ready = false;
    int PlaceTries = 0;
    float tick;
    boolean isUpdate = false;
    boolean facePlace = false;
    int placeCounter = 0;
    int placeCounterTimer = 0;
    boolean raytrace = false;


    public CrystalAura() {
        instance = this;

    }

    //use this event for good rotation support
    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        placeCounterTimer++;
        if (placeCounterTimer >= 20) {
            if (DebugCs.isOn())
                mc.player.sendStatusMessage(new TextComponentString("Ca Speed: " + placeCounter + " c/s"), true);

            placeCounter = 0;
            placeCounterTimer = 0;
        }

        //get enemies list and run ca
        predictTarget();

        //isUpdate is rotations thing, it needs that for better rotating
        isUpdate = true;

        //run ca loop
        ca();

        isUpdate = false;
    }

    private boolean shouldPause() {
        if (hp.getValue() > mc.player.getHealth()) return true;
        if (shulkerAura.getValue() && ShulkerAura.INSTANCE.isEnabled()) return true;
        if (cevBreaker.getValue() && CevBreaker.INSTANCE.isEnabled()) return true;
        if (surround.getValue() && Surround.instance.isPlacing()) return true;
        if (eating.getValue() && mc.gameSettings.keyBindUseItem.isKeyDown() && ((mc.player.getHeldItemMainhand().getItem() instanceof ItemFood && mc.player.activeHand == EnumHand.MAIN_HAND) || (mc.player.getHeldItemOffhand().getItem() instanceof ItemFood && mc.player.activeHand == EnumHand.OFF_HAND)))
            return true;
        if (mining.getValue() && mc.playerController.isHittingBlock) return true;
        return false;
    }

    //main ca logic
    void ca() {
        delayTimer++;

        if (breakPauseTimer > 0)
            breakPauseTimer--;
        if (placePauseTimer > 0)
            placePauseTimer--;

        tick += (20f - speed.getValue()) / 20f;
        if (tick < 1f) {
            if (shouldPause()) return;
            //get crystal  entity to break
            getCrystalEntityTarget();
            //break that entity if we can
            getCrystalPlacePos();
            if (!doBreak() || !AntiCheatConfig.getInstance().attackRotate.getValue() || !AntiCheatConfig.getInstance().placeRotate.getValue())
                if (!doPlace() && (currentCrystalEntity != null || currentCrystalBlockPos != null))
                    rotate(BOTH);
        } else
            tick -= 1;

        rotate(BOTH);

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
                    breakPauseTimer = 0;
                    placeCounter++;

                    if (Debug.isOn())
                        Spark.sendInfo("Crystal spawned with id " + c.getEntityId());

                    if (InstantBreak.isOn()) {
                        //if insta break is one we set break target and call break
                        if (Debug.isOn())
                            Spark.sendInfo("Insta breaking crystal " + c.getEntityId());
                        if (getValueForCrystalExplodingAtPoint(c.getPositionVector(), false).value > 0 && canBreakCrystal(c)) {
                            currentCrystalEntity = c;
                            if (tick < 1f)
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

    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event) {
        Packet p = event.getPacket();
        if (p instanceof SPacketExplosion) {
            SPacketExplosion ex = (SPacketExplosion) p;

            if (PlayerUtil.getPlayerPosFloored(ex.getX(), ex.getY() + 0.1, ex.getZ()).add(0, -1, 0).equals(currentCrystalBlockPos)) {
                if (instantReplace.getValue() && tick < 1f) {
                    doPlace();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (currentPos != null)
            currentPos.startFade();
    }

    @Override
    public void onEnable() {
        //reset values for ca to start
        ready = false;
        facePlace = false;
        currentCrystalBlockPos = null;
        currentCrystalEntity = null;
        lastAttackedEntity = null;
        placePauseTimer = 0;
        breakPauseTimer = 0;
        PlaceTries = 0;
        delayTimer = 0;
    }


    void predictTarget() {
        predictedPlayer = new PredictedEntity(mc.player, prediction.getValue());
        predictedEnemies.clear();
        for (Entity e : mc.world.loadedEntityList) {

            if (e instanceof EntityLivingBase)
                if (e != mc.player)
                    if (!PlayersOnly.isOn() || e instanceof EntityPlayer)
                        if (AttackUtil.canAttackEntity((EntityLivingBase) e, 15))
                            predictedEnemies.add(new PredictedEntity((EntityLivingBase) e, prediction.getValue()));
        }
    }


    boolean doPlace() {
        if (currentCrystalBlockPos == null || !CanPlaceOnBlock(currentCrystalBlockPos, true).getKey()) return false;
        if (placePauseTimer <= 0) {
            //place crystal
            //get point for rotations
            if (delayTimer < 5 && slowFacePlace.getValue() && facePlace) {
                return false;
            }
            Vec3d pos = CrystalUtil.getRotationPos(true, currentCrystalBlockPos, currentCrystalEntity);
            if (pos == null)
                pos = new Vec3d(currentCrystalBlockPos).add(0.5, 0.5, 0.5);
            final RayTraceResult result = mc.world.rayTraceBlocks(PlayerUtil.getEyePos(), pos, false, true, false);
            EnumFacing facing = (result == null || !currentCrystalBlockPos.equals(result.getBlockPos()) || result.sideHit == null) ? EnumFacing.UP : result.sideHit;

            Vec3d v = new Vec3d(currentCrystalBlockPos).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));

            if (result != null && currentCrystalBlockPos.equals(result.getBlockPos()) && result.hitVec != null)
                v = result.hitVec;
            if (currentCrystalBlockPos.getY() >= 254)
                facing = EnumFacing.EAST;
            renderVec = facing;

            //update offhand
            Offhand.instance.update();
            //hand

            EnumHand hand = Spark.switchManager.Switch(new SpecItemSwitchItem(Items.END_CRYSTAL), ItemSwitcher.usedHand.Both, Spark.switchManager.getModeFromString(switchingMode.getValue()), 10);
            if (hand == null)
                return false;


            //rotate if needed
            if (!rotate(PLACE))
                return true;

            v = v.add(-currentCrystalBlockPos.getX(),-currentCrystalBlockPos.getY(),-currentCrystalBlockPos.getZ());

            //send packet
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(currentCrystalBlockPos, facing, hand, (float) v.x, (float) v.y, (float) v.z));

            //swing
            switch (AntiCheatConfig.getInstance().placeSwing.getValue()) {
                case "Normal":
                    mc.player.swingArm(hand);
                    break;
                case "Packet":
                    mc.player.connection.sendPacket(new CPacketAnimation(hand));
                    break;
            }

            if (!isUpdate) {
                Spark.switchManager.OnLateUpdate();
            }

            //apply cooldown/timeout if failed xamount(2by default) places
            PlaceTries++;
            if (PlaceTries >= placeTries.getValue())
                placePauseTimer = placeCooldown.getValue();

            //reset delay to wait time before next action
            delayTimer = 0;

            if (Debug.isOn())
                Spark.sendInfo("Place packet sent!");

        }
        return true;
    }

    boolean doBreak() {
        if (currentCrystalEntity != lastAttackedEntity)
            breakPauseTimer = 0;
        if (currentCrystalEntity != null) {
            if (breakPauseTimer <= 0) {
                //try break

                //rotate if needed
                if (!rotate(BREAK))
                    return true;


                CrystalUtil.sendAttackPackets(currentCrystalEntity);

                //set last attacked
                lastAttackedEntity = currentCrystalEntity;

                //set break coodown to precvent spam of packets
                breakPauseTimer = breakCooldown.getValue();

                //place cooldown is set to 0 to replace
                placePauseTimer = 0;

                //place tries is reset
                PlaceTries = 0;

                //reset delay to wait time before next action
                delayTimer = 0;
                if (Debug.isOn())
                    Spark.sendInfo("Crystal break packet for id: " + currentCrystalEntity.getEntityId());
                return true;
            } else return true;

            //no entity to attack lets place a crystal
        } else return false;
    }

    void getCrystalEntityTarget() {
        currentCrystalEntity = null;


        float bestValue = 0;
        for (Object o : mc.world.loadedEntityList.toArray()) {

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
        for (BlockPos pos : WorldUtils.getSphere(PlayerUtil.getPlayerPosFloored(mc.player), 7, 7, 1)) {
            Pair<Boolean, Boolean> pair = CanPlaceOnBlock(pos, false);
            if (pair.getKey()) {
                ValueForExplodingCrystalAtPoint Value = getValueForCrystalExplodingAtPoint(new Vec3d(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f), prePlace.getValue());
                if (pos.equals(currentCrystalBlockPos) && Value.value > 0)
                    Value.value += switchThreshold.getValue();
                if (pair.getValue())
                    Value.value -= 1;
                if (Value.value > bestValue) {
                    bestValue = Value.value;
                    if (Value.target != null)
                        targetEntity = Value.target.entity;
                    bestPos = pos;
                    facePlace = Value.facePlace;
                    raytrace = pair.getValue();
                }
            }
        }
        currentCrystalBlockPos = bestPos;
        if (bestPos != null) {
            if (currentPos == null) {
                currentPos = new FadePos(bestPos, fill, false);
            } else if (currentPos.pos != bestPos) {
                currentPos.startFade();
                currentPos = new FadePos(bestPos, fill, false);
            }
        } else if (currentPos != null) {
            currentPos.startFade();
            currentPos = null;
        }
        if (currentPos != null)
            Spark.fadeManager.getPositions().removeIf(fadePos -> fadePos.pos.equals(currentPos.pos) && fadePos != currentPos);

    }

    public EntityLivingBase getTarget() {
        return (isEnabled() ? targetEntity : null);
    }


    //methods to calculate crystal things

    ValueForExplodingCrystalAtPoint getValueForCrystalExplodingAtPoint(Vec3d pos, boolean prePlace) {
        float bestValue = -1;

        float myhealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        float selfdam = CrystalUtil.calculateDamageCrystal(pos, predictedPlayer, prePlace);
        PredictedEntity target = null;
        boolean facePlace = false;
        if (selfdam + 2 < myhealth || !NoSuicide.isOn())
            if (maxSelfdamage.getValue() > selfdam || maxSelfdamage.getValue() == 0) {

                for (PredictedEntity ct : predictedEnemies) {
                    EntityLivingBase e = ct.entity;
                    float d = (float) (CrystalUtil.calculateDamageCrystal(pos, ct, prePlace) - selfdam * protectSelf.getValue());
                    double minD = minEnemydamage.getValue();
                    boolean f = false;
                    if (d < minD)
                        if (e.getHealth() + e.getAbsorptionAmount() < facePlaceHealth.getValue() || facePlaceKey.isDown() || armor(e)) {
                            f = true;
                            minD = 1.5D;
                        }
                    if (d > Math.max(minD, bestValue)) {
                        bestValue = d;
                        facePlace = f;
                        target = ct;
                    }
                }


            }
        return new ValueForExplodingCrystalAtPoint(target, bestValue, facePlace);
    }

    boolean armor(EntityLivingBase player) {
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack == null || stack.getItem() == Items.AIR) continue;

            float armor = ((float) (stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage()) * 100f;

            if (breakArmor.getValue() >= armor && stack.stackSize < 2) return true;
        }
        return false;
    }

    Pair<Boolean, Boolean> CanPlaceOnBlock(BlockPos p, boolean canReallyPlace) {

        //prevent place on block we mining
        if (AutoCity.INSTANCE.isEnabled() && p.equals(AutoCity.INSTANCE.GetBreakeBlock()))
            return new Pair<>(false, false);

        final Block block = mc.world.getBlockState(p).getBlock();
        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
            final Block floor = mc.world.getBlockState(p.add(0, 1, 0)).getBlock();
            final Block ceil = mc.world.getBlockState(p.add(0, 2, 0)).getBlock();

            //in the end crystal have a fire block in them

            if ((floor == Blocks.AIR || (floor == Blocks.FIRE && mc.player.dimension == 1)) && ceil == Blocks.AIR) {
                double d0 = (double) p.getX();
                double d1 = (double) p.getY() + 1;
                double d2 = (double) p.getZ();
                double d0b = d0 + 1;
                double d1b = d1 + 2;
                double d2b = d2 + 1;

                AxisAlignedBB bb = new AxisAlignedBB(d0, d1, d2, d0b, d1b, d2b);
                for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, bb)) {
                    if (entity.isDead) continue;

                    if (entity instanceof EntityEnderCrystal) {
                        if (!canReallyPlace && entity.getPosition().equals(p.add(0, 1, 0)))
                            continue;
                        if (lastAttackedEntity != null && lastAttackedEntity == currentCrystalEntity && lastAttackedEntity.getPosition().equals(entity.getPosition()))
                            continue;
                    }


                    return new Pair<>(false, false);


                }


                Vec3d pos = PlayerUtil.getClosestPoint(RaytraceUtil.getPointToLookAtBlock(p));
                boolean raytrace = false;
                double d = PlayerUtil.getDistance(p);
                if (d > AntiCheatConfig.getInstance().placeRange.getValue())
                    return new Pair<>(false, false);
                if (pos == null && d > AntiCheatConfig.getInstance().placeWallRange.getValue()) {
                    float[] rotation = Spark.rotationManager.getLegitRotations(new Vec3d(p.getX() + 0.5, p.getY() + 1, p.getZ() + 0.5));
                    if (AntiCheatConfig.getInstance().raytrace.getValue() && RaytraceUtil.getRotationForBypass(rotation[0]) != null) {
                        raytrace = true;
                    } else return new Pair<>(false, false);
                }
                //predict can break

                if (!canBreakCrystal(new Vec3d(p.getX() + 0.5, p.getY() + 1, p.getZ() + 0.5)))
                    return new Pair<>(false, false);


                return new Pair<>(true, raytrace);

            }
        }

        return new Pair<>(false, false);


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
        if (PlayerUtil.getDistance(crystal) > (pos != null ? AntiCheatConfig.getInstance().attackRange.getValue() : AntiCheatConfig.getInstance().attackWallRange.getValue()))
            return false;

        return true;
    }

    //rotation methods
    boolean rotate(RotateType type) {
        AntiCheatConfig cfg = AntiCheatConfig.getInstance();

        if (currentCrystalBlockPos == null && currentCrystalEntity == null) return true;

        Vec3d pos = CrystalUtil.getRotationPos((type != PLACE),currentCrystalBlockPos,currentCrystalEntity);
        boolean needsRotating = false;
        boolean needsRaytracebypass = false;


        switch (type) {
            case PLACE:
                needsRotating = cfg.placeRotate.getValue();
                needsRaytracebypass = true;
                break;
            case BREAK:
                needsRotating = cfg.attackRotate.getValue();
                break;
            case BOTH:
                needsRotating = cfg.attackRotate.getValue() || cfg.placeRotate.getValue();
                needsRaytracebypass = currentCrystalEntity == null;
                break;
        }

        float rot[] = Spark.rotationManager.getLegitRotations(pos);


        if (raytrace && needsRaytracebypass)
        {
            if(!Spark.rotationManager.isRaytraceBypassDone())
            {
                float[] rotBypass = RaytraceUtil.getRotationForBypass(rot[0]);
                if (rotBypass == null)
                    return false;
                Spark.rotationManager.rotate(rotBypass, isUpdate,false);
                return false;
            }
        }
        if(!needsRotating)
            return true;
        return Spark.rotationManager.rotate(rot, isUpdate,false);
    }



    private AxisAlignedBB getFacingVec(EnumFacing facing, BlockPos pos) {
        switch (facing) {
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
        EspUtil.boundingESPBoxFilled(render, fill.getColor());
        EspUtil.boundingESPBox(render, fill.getColor().brighter(), 2.0f);
    }

    enum RotateType {
        BREAK,
        PLACE,
        BOTH;
    }

    class ValueForExplodingCrystalAtPoint {
        public float value;
        public PredictedEntity target;
        boolean facePlace;

        public ValueForExplodingCrystalAtPoint(PredictedEntity target, float value, boolean facePlace) {
            this.value = value;
            this.target = target;
            this.facePlace = facePlace;
        }
    }
}

package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.RaytraceUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.ItemForFightSwitchItem;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

import java.util.Arrays;

@Module.Registration(name = "KillAura", description = "Superior module litarly best ever")
public class KillAura extends Module {

    IntSetting targetFov = new IntSetting("TargetFov", this, 180, 45, 180, "Targeting");

    DoubleSetting range = new DoubleSetting("Range", this, 4, 0, 6, 0.25, "Targeting");
    DoubleSetting wallsReach = new DoubleSetting("WallRange", this, 0, 0, 6, 0.25, "Targeting");


    BooleanSetting delay = new BooleanSetting("Delay", this, true, "Time");
    IntSetting cps = new IntSetting("APS", this, 10, 5, 22, "Time");
    IntSetting failPercentage = new IntSetting("FailPercentage", this, 0, 0, 50, "Time");


    BooleanSetting mobs = new BooleanSetting("Mobs", this, false, "Attacking");
    BooleanSetting living = new BooleanSetting("Living", this, false, "Attacking");

    BooleanSetting FilterInvisible = new BooleanSetting("FilterInvisible", this, false, "Attacking");
    ModeSetting switchMode = new ModeSetting("Switch", this, "Off", Arrays.asList("Off", "Auto", "OnlySword"), "Attacking");


    BooleanSetting Rotate = new BooleanSetting("Rotate", this, true, "Rotation");
    IntSetting RotStep = new IntSetting("RotStep", this, 180, 45, 180, "Rotation");
    IntSetting StayTicks = new IntSetting("StayTicks", this, 1, 0, 5, "Rotation");


    public static EntityLivingBase killaurTarget = null;

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {
        if (!switchMode.is("OnlySword") || MC.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {
            killaurTarget = getTarget(killaurTarget);
            if (killaurTarget != null)
                attack(killaurTarget);
        } else killaurTarget = null;
    }

    Timer time = new Timer();

    public boolean attack(EntityLivingBase target) {
        Vec3d lookAt = RaytraceUtil.getPointToLookAtEntity(target);
        if (lookAt == null) lookAt = target.boundingBox.getCenter();

        if (Rotate.isOn())
            if (!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(lookAt), RotStep.getValue(), StayTicks.getValue(), false))
                return false;
        if (switchMode.is("Auto"))
            ItemSwitcher.Switch(new ItemForFightSwitchItem(target), ItemSwitcher.switchType.Mainhand);


        if (delay.isOn()) {
            if (MC.mc.player.ticksSinceLastSwing <= MC.mc.player.getCooldownPeriod())
                return false;
        } else {
            if (!time.passedMs((int) (1000.0 / cps.getNumber())))
                return false;
        }

        time.reset();


        if (Math.random() * 100 >= failPercentage.getValue())
            MC.mc.playerController.attackEntity(MC.mc.player, target);

        switch (AntiCheatConfig.getInstance().AttackSwing.getValue()) {
            case "Normal":
                MC.mc.player.swingArm(EnumHand.MAIN_HAND);
                break;
            case "Packet":
                MC.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        }

        return true;
    }

    EntityLivingBase getTarget(EntityLivingBase prioEntity) {

        //if we can still attack old target we keep it as target
        if (prioEntity != null && canAttack(prioEntity))
            return prioEntity;

        double bestValue = Double.MAX_VALUE;
        EntityLivingBase target = null;

        //find new target
        for (Object o : MC.mc.world.loadedEntityList.toArray()) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase e = (EntityLivingBase) o;
                if (canAttack(e)) {
                    double thisValue = MC.mc.player.getDistance(e);
                    if (thisValue < bestValue) {
                        bestValue = thisValue;
                        target = e;
                    }

                }
            }
        }
        return target;
    }


    boolean canAttack(EntityLivingBase e) {
        if (e == MC.mc.player || e.isDead)
            return false;
        if (!PlayerUtil.CanInteractVanillaCheck(e))
            return false;
        if (e instanceof EntityPlayer) {
            if (!AttackUtil.canAttackPlayer(((EntityPlayer) e))) return false;
            if (e.isInvisible() && FilterInvisible.isOn()) return false;
        } else if (e instanceof EntityMob) {
            if (!mobs.isOn()) return false;
        } else {
            if (!living.isOn()) return false;
        }

        if (targetFov.getValue() < 180) {
            if (Math.abs(RenderUtil.getViewRotations(e.getPositionVector(), MC.mc.player)[0] - MC.mc.player.rotationYaw) > targetFov.getValue())
                return false;
        }

        return MC.mc.player.getDistance(e) < ((RaytraceUtil.getVisiblePointsForEntity(e).size() > 0) ? range : wallsReach).getFloatValue();
    }
}

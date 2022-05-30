package me.wallhacks.spark.systems.autoconfigs.configs;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.autoconfigs.AutoConfig;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.command.Command;
import me.wallhacks.spark.systems.module.modules.combat.CevBreaker;
import me.wallhacks.spark.systems.module.modules.combat.Criticals;
import me.wallhacks.spark.systems.module.modules.combat.CrystalAura;
import me.wallhacks.spark.systems.module.modules.exploit.AntiEatDesync;
import me.wallhacks.spark.systems.module.modules.render.ViewModel;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;

public class Const extends BaseSetup {

    public Const() {
        super("Const");
    }


    public void config() {
        super.config();

        CrystalAura.instance.InstantBreak.setValue(true);
        CrystalAura.instance.instantReplace.setValue(true);
        CrystalAura.instance.switchingMode.setValue("Const");
        CrystalAura.instance.placeTries.setValue(2);

        CrystalAura.instance.speed.setValue(20);
        CrystalAura.instance.breakCooldown.setValue(6);
        CrystalAura.instance.placeCooldown.setValue(6);

        AntiCheatConfig.getInstance().placeRotate.setValue(true);
        AntiCheatConfig.getInstance().attackRotate.setValue(false);

        SystemManager.getModule(AntiEatDesync.class).enable();

        AntiCheatConfig.getInstance().placeRange.setNumber(4);
        AntiCheatConfig.getInstance().placeWallRange.setNumber(0);
        AntiCheatConfig.getInstance().attackRange.setNumber(4);
        AntiCheatConfig.getInstance().attackWallRange.setNumber(0);
        AntiCheatConfig.getInstance().raytrace.setValue(true);

        CevBreaker.INSTANCE.switchingMode.setValue("Const");

        SystemManager.getModule(Criticals.class).reverse.setValue("ReverseCons");
    }


}

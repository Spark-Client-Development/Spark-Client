package me.wallhacks.spark.systems.autoconfigs.configs;

import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.autoconfigs.AutoConfig;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.modules.combat.CevBreaker;
import me.wallhacks.spark.systems.module.modules.combat.Criticals;
import me.wallhacks.spark.systems.module.modules.combat.CrystalAura;

public class CCpvp extends BaseSetup {

    public CCpvp() {
        super("CCpvp");
    }


    public void config() {
        super.config();

        CrystalAura.instance.InstantBreak.setValue(true);
        CrystalAura.instance.instantReplace.setValue(true);
        CrystalAura.instance.switchingMode.setValue("Silent");
        CrystalAura.instance.placeTries.setValue(2);

        CrystalAura.instance.protectSelf.setNumber(0);

        CrystalAura.instance.speed.setValue(20);
        CrystalAura.instance.breakCooldown.setValue(3);
        CrystalAura.instance.placeCooldown.setValue(3);

        AntiCheatConfig.getInstance().placeRotate.setValue(false);
        AntiCheatConfig.getInstance().attackRotate.setValue(false);

        AntiCheatConfig.getInstance().placeRange.setNumber(4);
        AntiCheatConfig.getInstance().placeWallRange.setNumber(4);
        AntiCheatConfig.getInstance().attackRange.setNumber(4);
        AntiCheatConfig.getInstance().attackWallRange.setNumber(4);
        AntiCheatConfig.getInstance().raytrace.setValue(false);


        SystemManager.getModule(Criticals.class).reverse.setValue("Up");
    }


}

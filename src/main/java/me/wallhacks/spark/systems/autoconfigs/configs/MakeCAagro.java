package me.wallhacks.spark.systems.autoconfigs.configs;

import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.autoconfigs.AutoConfig;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.modules.combat.CevBreaker;
import me.wallhacks.spark.systems.module.modules.combat.Criticals;
import me.wallhacks.spark.systems.module.modules.combat.CrystalAura;

public class MakeCAagro extends AutoConfig {

    public MakeCAagro() {
        super("MakeCAagro");
    }


    public void config() {
        super.config();

        CrystalAura.instance.protectSelf.setNumber(0);
        CrystalAura.instance.breakArmor.setNumber(15);
        CrystalAura.instance.facePlaceHealth.setNumber(10);
    }


}

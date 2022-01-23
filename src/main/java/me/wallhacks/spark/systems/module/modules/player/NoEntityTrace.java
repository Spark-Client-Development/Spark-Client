package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import org.lwjgl.input.Mouse;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;

@Module.Registration(name = "NoEntityTrace", description = "Steals from chests")
public class NoEntityTrace extends Module {

    BooleanSetting all = new BooleanSetting("All",this,false,"General");

    public boolean noTrace() {

        if(Mouse.isButtonDown(2))
            return false;

        if (all.isOn()) return true;
        Item item = MC.mc.player.getHeldItemMainhand().getItem();

        if (item instanceof ItemPickaxe) return true;

        if (item instanceof ItemBlock && Mouse.isButtonDown(1)) return true;
        return false;
    }



}

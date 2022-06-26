package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.MC;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import org.lwjgl.input.Mouse;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;

import java.util.Arrays;

@Module.Registration(name = "NoEntityTrace", description = "Steals from chests")
public class NoEntityTrace extends Module {
    ModeSetting interact = new ModeSetting("Interact", this, "RightClick", Arrays.asList("RightClick", "LeftClick", "Both"));
    BooleanSetting all = new BooleanSetting("PickaxeOnly",this,false);

    public boolean noTrace() {
        if(Mouse.isButtonDown(1) && interact.is("LeftClick"))
            return false;
        if (Mouse.isButtonDown(2) && interact.is("RightClick"))
            return false;
        if (!all.getValue()) return true;
        Item item = mc.player.getHeldItemMainhand().getItem();
        if (item instanceof ItemPickaxe) return true;
        return false;
    }



}

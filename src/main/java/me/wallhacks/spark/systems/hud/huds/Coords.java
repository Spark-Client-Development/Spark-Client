package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.systems.hud.AlignedHudElement;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.objects.Pair;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;

@HudElement.Registration(name = "Coords", description = "Shows your coords", posY = 0.9, posX = 0, height = 0, width = 0, drawBackground = false)
public class Coords extends AlignedHudElement {
    ModeSetting mode = new ModeSetting("Mode", this, "Horizontal", Arrays.asList("Horizontal", "List"));
    BooleanSetting y = new BooleanSetting("Y", this, false);
    BooleanSetting dimensional = new BooleanSetting("Dimensional", this, true);
    ArrayList<Pair<String, Integer>> list = new ArrayList<>();

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        list = new ArrayList<>();
        double dFactor = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell") ? 8 : 0.125;
        int color = HudSettings.getInstance().getGuiHudMainColor().getRGB();
        if (mode.is("List")) {
            list.add(new Pair<>("Z: " + ChatFormatting.WHITE + MathUtil.roundAvoid(mc.player.posZ, 1) + ChatFormatting.GRAY + (dimensional.getValue() ? " (" + MathUtil.roundAvoid(mc.player.posZ * dFactor, 1) + ")" : ""), color));
            if (y.getValue())
                list.add(new Pair<>("Y: " + ChatFormatting.WHITE + MathUtil.roundAvoid(mc.player.posX, 1), color));
            list.add(new Pair<>("X: " + ChatFormatting.WHITE + MathUtil.roundAvoid(mc.player.posX, 1) + ChatFormatting.GRAY + (dimensional.getValue() ? " (" + MathUtil.roundAvoid(mc.player.posX * dFactor, 1) + ")" : ""), color));
        } else {
            list.add(new Pair<>("X " + (y.getValue() ? "Y Z " : "Z ") + ChatFormatting.WHITE + MathUtil.roundAvoid(mc.player.posX, 1) + " " + (y.getValue() ? MathUtil.roundAvoid(mc.player.posY, 1) + " " + MathUtil.roundAvoid(mc.player.posZ, 1) : MathUtil.roundAvoid(mc.player.posZ, 1)) + ChatFormatting.GRAY + (dimensional.getValue() ? " (" + MathUtil.roundAvoid(mc.player.posX * dFactor, 1) + " " + MathUtil.roundAvoid(mc.player.posZ * dFactor, 1) + ")" : ""), color));
        }
    }

    @Override
    public void draw(float deltaTime) {
        super.draw(deltaTime);
        drawList(list);
    }
}

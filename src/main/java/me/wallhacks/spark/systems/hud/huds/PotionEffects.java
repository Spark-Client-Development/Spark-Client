package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.systems.hud.AlignedHudElement;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.util.objects.Pair;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@HudElement.Registration(name = "PotionEffects", width = 0, height = 0, description = "Shows active potion effects", posX = 0.9, posY = 0.4, drawBackground = false)
public class PotionEffects extends AlignedHudElement {
    @Override
    public void draw(float partialTicks) {
        ArrayList<Pair<String, Integer>> potions = new ArrayList<>();
        mc.player.getActivePotionEffects().forEach(effect -> {

            potions.add(new Pair<>(ChatFormatting.WHITE + I18n.format(effect.getPotion().getName()) + " " + (effect.getAmplifier() + 1) + " " + ChatFormatting.GRAY + Potion.getPotionDurationString(effect, 1.0f), new Color(effect.getPotion().getLiquidColor()).getRGB()));
        });
        Collections.sort(potions, new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair s1, Pair s2) {
                return fontManager.getTextWidth((String) s2.getKey()) - fontManager.getTextWidth((String) s1.getKey());
            }
        });
        if (potions.isEmpty()) potions.add(new Pair<>(ChatFormatting.WHITE + "No active potions", HudSettings.getInstance().getGuiHudMainColor().getRGB()));
        drawList(potions);
    }
}

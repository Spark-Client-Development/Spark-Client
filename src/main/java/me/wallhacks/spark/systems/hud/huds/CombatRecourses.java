package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.systems.hud.AlignedHudElement;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.util.objects.Pair;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@HudElement.Registration(name = "CombatResources", description = "Shows your coords", posY = 0.4, posX = 0.2, height = 0, width = 0, drawBackground = false)
public class CombatRecourses extends AlignedHudElement {
    BooleanSetting exp = new BooleanSetting("EXP", this, true);
    BooleanSetting totems = new BooleanSetting("Totems", this, true);
    BooleanSetting crystals = new BooleanSetting("Crystals", this, true);
    BooleanSetting gaps = new BooleanSetting("Gaps", this, true);
    BooleanSetting obsidian = new BooleanSetting("Obsidian", this, true);
    ArrayList<Pair<String, Integer>> render = new ArrayList<>();

    @Override
    public void draw(float partialTicks) {
        drawList(render);
    }

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        int color = HudSettings.getInstance().getGuiHudMainColor().getRGB();
        ArrayList<Pair<String, Integer>> resources = new ArrayList<>();
        if (exp.getValue()) {
            int exp = Spark.dataTrackingManager.getAmountOfItem(Items.EXPERIENCE_BOTTLE);
            resources.add(new Pair<>(ChatFormatting.GRAY + "EXP: " + ChatFormatting.WHITE + exp, color));
        }
        if (totems.getValue()) {
            int totems = Spark.dataTrackingManager.getAmountOfItem(Items.TOTEM_OF_UNDYING);
            resources.add(new Pair<>(ChatFormatting.GRAY + "Totems: " + ChatFormatting.WHITE + totems, color));
        }
        if (crystals.getValue()) {
            int crystals = Spark.dataTrackingManager.getAmountOfItem(Items.END_CRYSTAL);
            resources.add(new Pair<>(ChatFormatting.GRAY + "Crystals: " + ChatFormatting.WHITE + crystals, color));
        }
        if (gaps.getValue()) {
            int gaps = Spark.dataTrackingManager.getAmountOfItem(Items.GOLDEN_APPLE);
            resources.add(new Pair<>(ChatFormatting.GRAY + "Gaps: " + ChatFormatting.WHITE + gaps, color));
        }
        if (obsidian.getValue()) {
            int gaps = Spark.dataTrackingManager.getAmountOfItem(Item.getItemFromBlock(Blocks.OBSIDIAN));
            resources.add(new Pair<>(ChatFormatting.GRAY + "Obsidian: " + ChatFormatting.WHITE + gaps, color));
        }
        Collections.sort(resources, new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair s1, Pair s2) {
                return fontManager.getTextWidth((String) s2.getKey()) - fontManager.getTextWidth((String) s1.getKey());
            }
        });
        render = new ArrayList<>(resources);
    }
}

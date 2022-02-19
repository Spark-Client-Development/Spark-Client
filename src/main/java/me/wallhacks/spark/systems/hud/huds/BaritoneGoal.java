package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalComposite;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.hud.InfoHudElement;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;

@HudElement.Registration(name = "BaritoneGoal", description = "Shows the current baritone goal", posX = 0.0, posY = 0.65, width = 30, height = 12)
public class BaritoneGoal extends InfoHudElement {
    @Override
    public void draw(float deltaTime) {
        String Goal = "not active";
        if (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
            if (!(BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getGoal() instanceof GoalComposite))
                Goal = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getGoal().toString();
            else
                Goal = "multiple goals";

        }

        setInfo(String.format(ChatFormatting.GRAY + "Baritone: %s%s", ChatFormatting.WHITE, Goal));

        drawInfo();
    }
}

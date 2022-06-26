package me.wallhacks.spark.systems.hud.huds;


import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.hud.AlignedHudElement;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.objects.Pair;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.module.Module;

import java.awt.*;
import java.util.*;
import java.util.List;

@HudElement.Registration(name = "ModuleList", description = "List of modules", posX = 1, posY = 0, width = 40, height = 100, enabled = true)
public class ModuleList extends AlignedHudElement {
    ModeSetting sort = new ModeSetting("Sorting",this,"Length",Arrays.asList("Length","CharLength","Name"));
    ModeSetting mode = new ModeSetting("Mode", this, "Rainbow", Arrays.asList("AlphaStep", "Rainbow", "Normal", "Module"));
    DoubleSetting speed = new DoubleSetting("RainbowSpeed", this, 20.0D, 1.0D, 100.0D, v -> mode.is("Rainbow"));
    DoubleSetting saturation = new DoubleSetting("RainbowSaturation", this, 0.8D, 0.0D, 1.0D,v -> mode.is("Rainbow"));
    DoubleSetting brightness = new DoubleSetting("RainbowBrightness", this, 0.8D, 0.0D, 1.0D,v -> mode.is("Rainbow"));
    DoubleSetting difference = new DoubleSetting("RainbowDifference", this, 20.0D, 1.0D, 100.0D,v -> mode.is("Rainbow"));


    ArrayList<Pair<String, Integer>> list = new ArrayList<>();

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        List<Module> modules = new ArrayList(SystemManager.getModules());

        switch (sort.getValueName()){
            case "Length":
                Collections.sort(modules, new Comparator<Module>() {
                    @Override
                    public int compare(Module s1, Module s2) {
                        return fontManager.getTextWidth(s2.getName()) - fontManager.getTextWidth(s1.getName());
                    }
                });
                break;
            case "CharLength":
                Collections.sort(modules, new Comparator<Module>() {
                    @Override
                    public int compare(Module s1, Module s2) {
                        return s2.getName().length() - s1.getName().length();
                    }
                });
                break;
            case "Name":
                Collections.sort(modules, new Comparator<Module>() {
                    @Override
                    public int compare(Module s1, Module s2) {
                        return s1.getName().compareToIgnoreCase(s2.getName());
                    }
                });
                break;
        }

        list = new ArrayList<>();
        int i = 0;
        for (Module mod : modules) {
            if (mod.isEnabled() && mod.isVisible()) {
                list.add(new Pair<>(mod.getName(), getColor(i * 10, mod.getName())));
                i++;
            }
        }
    }

    @Override
    public void draw(float deltaTime) {
        super.draw(deltaTime);
        drawList(list);
    }

    public int getColor(int y, String text) {
        y/=fontManager.getTextHeight();
        switch (mode.getValue()) {
            case "AlphaStep":
                return alphaStep(new Color(hudSettings.getGuiHudMainColor().getRed(),
                        hudSettings.getGuiHudMainColor().getGreen(),
                        hudSettings.getGuiHudMainColor().getBlue()),
                        50, (y * 2) + 10).getRGB();
            case "Rainbow":
                return rainbow(y);
            case "Normal":
                return hudSettings.getGuiHudMainColor().getRGB();
            case "Module":
                return ColorUtil.generateColor(text).getRGB();
        }
        return -1;
    }

    private int rainbow(long offset) {
        float hue = (float) ((((System.currentTimeMillis() * (speed.getValue() / 10)) + (offset * 500)) % (30000L / (difference.getValue() / 100))) / (30000.0f / (difference.getValue() / 20)));
        int rgb = Color.HSBtoRGB(hue, saturation.getFloatValue(), brightness.getFloatValue());
        int red = rgb >> 16 & 255;
        int green = rgb >> 8 & 255;
        int blue = rgb & 255;
        return ColorUtil.toRGBA(red, green, blue, 255);
    }

    private Color alphaStep(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float) (System.currentTimeMillis() % 2000L) / 1000.0F + (float) index / (float) count * 2.0F) % 2.0F - 1.0F);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }


}

package me.wallhacks.spark.gui.components;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.ClickGui;
import me.wallhacks.spark.gui.components.settings.*;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.StringUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

import static me.wallhacks.spark.gui.ClickGui.maxOffset;
import static me.wallhacks.spark.gui.ClickGui.minOffset;

public class ModuleComponent {
    public Module module;
    double renderX;
    double renderY;
    boolean open = false;
    double state;
    boolean listening = false;
    static final ResourceLocation muted = new ResourceLocation("textures/icons/mutedicon.png");
    static final ResourceLocation unMuted = new ResourceLocation("textures/icons/unmutedicon.png");
    static final ResourceLocation drawn = new ResourceLocation("textures/icons/visibleicon.png");
    static final ResourceLocation hidden = new ResourceLocation("textures/icons/notvisibleicon.png");
    ArrayList<SettingComponent> components = new ArrayList<>();

    public ModuleComponent(Module module) {
        this.module = module;
        ArrayList<SettingGroup> groups = new ArrayList<SettingGroup>();
        for (Setting setting : module.getSettings()) {
            if (setting.getSettingsHolder() instanceof SettingGroup) {
                if (!groups.contains(setting.getsettingsHolder())) {
                    groups.add((SettingGroup) setting.getsettingsHolder());
                    components.add(new ModuleCategory((SettingGroup) setting.getsettingsHolder()));
                }
            } else if (setting instanceof IntSetting) {
                components.add(new IntSlider((IntSetting) setting));
            } else if (setting instanceof DoubleSetting) {
                components.add(new DoubleSlider((DoubleSetting) setting));
            } else if (setting instanceof BooleanSetting) {
                components.add(new BooleanComponent((BooleanSetting) setting));
            } else if (setting instanceof ModeSetting) {
                components.add(new ModeSelector((ModeSetting) setting));
            }
        }
    }

    public void setPosition(int posX, int posY) {
        renderY = posY;
        renderX = posX;
    }

    public int drawComponent(int posX, int posY, int smoothScroll, double deltaTime, int click, int mouseX, int mouseY) {
        if (renderX > posX) {
            renderX = Math.max(posX, renderX - deltaTime);
        } else if (renderX < posX) {
            renderX = Math.min(posX, renderX + deltaTime);
        }
        if (renderY > posY) {
            renderY = Math.max(posY, renderY - deltaTime);
        } else if (renderY < posY) {
            renderY = Math.min(posY, renderY + deltaTime);
        }
        if (click >= 0) {
            if (mouseX > renderX && mouseX < renderX + 200 && mouseY > renderY - smoothScroll && mouseY < renderY - smoothScroll + 30 - (open ? 3 : 0)) {
                if (click == 0) {
                    module.toggle();
                } else if (click == 1) {
                    open = !open;
                }
            }
        }
        int offset = 15;
        for (SettingComponent setting : components) {
            if (setting.visible())
                offset += setting.getHeight();
        }
        if (open && state != 1) {
            state = Math.min(1, state + deltaTime * 0.01);
        } else if (!open && state != 0) {
            state = Math.max(0, state - deltaTime * 0.01);
        }
        int renderOffset = (int) (state * offset);
        GuiUtil.rounded((int) renderX, (int) renderY - smoothScroll, (int) renderX + 200, (int) renderY + 30 - smoothScroll + renderOffset, ClickGui.background3(), 5);
        Spark.fontManager.getThickFont().drawString(module.getName(), (int) renderX + 5, (int) renderY + 10 - smoothScroll, -1, false);
        int max = minOffset > renderY - smoothScroll + 25 ? maxOffset : (int) Math.max(minOffset + maxOffset - (renderY - smoothScroll + 25), 0);
        int min = Math.max((int) renderY - smoothScroll + 25, minOffset);
        if (renderOffset != 0) {
            int currentY = (int) (-offset + renderOffset + renderY + 25);
            maxOffset = max;
            minOffset = min;
            GuiUtil.glScissor((int) renderX, minOffset, 200, maxOffset);
            GuiUtil.drawRect((float) renderX, currentY - smoothScroll, (float) renderX + 200, (float) renderY + renderOffset - smoothScroll + 10, ClickGui.background2());
            boolean hoverBind = false;
            boolean hoverHoldToggle = false;
            boolean hoverMute = false;
            boolean hoverDrawn = false;
            if (mouseX > renderX && mouseX < renderX + 200 && mouseY > currentY - smoothScroll + renderOffset - 15 && mouseY < currentY - smoothScroll + renderOffset + 5) {
                if (mouseX < renderX + 34) {
                    hoverHoldToggle = true;
                    if (click == 0) {
                        module.setHold(!module.isHold());
                    }
                } else if (mouseX < renderX + 80) {
                    hoverBind = true;
                    if (click >= 0)
                        if (listening) {
                            key(-2 - click);
                        } else {
                            listening = true;
                        }
                } else if (mouseX > renderX + 160) {
                    if (mouseX < renderX + 180) {
                        if (click == 0) module.setMuted(!module.isMuted());
                        hoverMute = true;
                    } else {
                        if (click == 0) module.setVisible(!module.isVisible());
                        hoverDrawn = true;
                    }
                }
            }

            if (!hoverBind && click >= 0) listening = false;

            //hold
            GuiUtil.setup(hoverHoldToggle ? ClickGui.background6() : ClickGui.background4());
            GL11.glVertex2d(renderX + 34, currentY - smoothScroll + renderOffset + 5);
            GL11.glVertex2d(renderX + 34, currentY - smoothScroll + renderOffset - 15);
            GL11.glVertex2d(renderX, currentY - smoothScroll + renderOffset - 15);
            GuiUtil.corner((int) (renderX + 5), currentY - smoothScroll + renderOffset, 5, 270, 360);
            GuiUtil.finish();
            String holdToggle = module.isHold() ? "Hold:" : "Toggle:";
            int mid = 17 - Spark.fontManager.getTextWidth(holdToggle) / 2;
            Spark.fontManager.drawString(holdToggle, (int) renderX + mid, currentY - smoothScroll + renderOffset - 8, -1);

            //bind
            GuiUtil.drawRect((float) renderX + 34, currentY - smoothScroll + renderOffset + 5, (float) renderX + 80, currentY - smoothScroll + renderOffset - 15, hoverBind ? ClickGui.background6() : ClickGui.background4());
            String bind = listening ? "Listening" + GuiUtil.getLoadingText(false) : StringUtil.getNameForKey(module.getBind());
            if (!listening)
                mid = 57 - Spark.fontManager.getTextWidth(bind) / 2;
            else mid = 37;
            Spark.fontManager.drawString(bind, (int) renderX + mid, currentY - smoothScroll + renderOffset - 8, -1);

            //filler
            GuiUtil.drawRect((float) renderX + 80, currentY - smoothScroll + renderOffset + 5, (float) renderX + 160, currentY - smoothScroll + renderOffset - 15, ClickGui.background4());

            //muted
            GuiUtil.drawRect((float) renderX + 160, currentY - smoothScroll + renderOffset + 5, (float) renderX + 180, currentY - smoothScroll + renderOffset - 15, hoverMute ? ClickGui.background6() : ClickGui.background4());
            GuiUtil.drawCompleteImage(renderX + 163, currentY - smoothScroll + renderOffset - 12, 14, 14, module.isMuted() ? muted : unMuted, Color.WHITE);

            //drawn
            GuiUtil.setup(hoverDrawn ? ClickGui.background6() : ClickGui.background4());
            GuiUtil.corner((int) (renderX + 195), currentY - smoothScroll + renderOffset, 5, 0, 90);
            GL11.glVertex2d(renderX + 200, currentY - smoothScroll + renderOffset - 15);
            GL11.glVertex2d(renderX + 180, currentY - smoothScroll + renderOffset - 15);
            GL11.glVertex2d(renderX + 180, currentY - smoothScroll + renderOffset + 5);
            GuiUtil.finish();
            GuiUtil.drawCompleteImage(renderX + 183, currentY - smoothScroll + renderOffset - 12, 14, 14, module.isVisible() ? drawn : hidden, Color.WHITE);

            //the actual settings
            for (SettingComponent setting : components) {
                if (!setting.visible()) continue;
                if (currentY - smoothScroll > minOffset + maxOffset) {
                    break;
                }
                if (setting.getHeight() + currentY - smoothScroll < minOffset) {
                    currentY += setting.getHeight();
                } else {
                    GuiUtil.glScissor((int) renderX, minOffset, 200, maxOffset);
                    currentY += setting.drawComponent((int) renderX, currentY - smoothScroll, deltaTime, state == 1 ? click : -1, mouseX, mouseY);
                }
            }
        }
        return 35 + renderOffset;
    }

    void key(int k) {
        listening = false;
        if (k == Keyboard.KEY_DELETE)
            module.setBind(Keyboard.KEY_NONE);
        else if (k != Keyboard.KEY_ESCAPE)
            module.setBind(k);
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (listening)
            key(keyCode);
    }
}

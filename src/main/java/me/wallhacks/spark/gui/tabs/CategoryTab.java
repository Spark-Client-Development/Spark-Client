package me.wallhacks.spark.gui.tabs;

import me.wallhacks.spark.gui.ClickGui;
import me.wallhacks.spark.gui.components.ModuleComponent;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MathUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class CategoryTab extends ClickGuiTab {
    public Module.Category category;
    ArrayList<ModuleComponent> modules = new ArrayList<>();

    boolean flag = true;
    double scroll = 0;
    double smoothScroll = 0;
    int height;

    public CategoryTab(Module.Category category) {
        super(category.getName(), new ResourceLocation("textures/categoryicons/" + category.getName() + ".png"));
        this.category = category;
        SystemManager.getModules().forEach(module -> {
            if (module.getCategory() == category) modules.add(new ModuleComponent(module));
        });
    }

    @Override
    public void drawTab(int mouseX, int mouseY, int click, int posX, int posY, double deltaTime) {
        if (mouseX > posX && mouseX > posY && mouseY < posX + 400 && mouseX < posX + 440) {
            scroll = (-(Mouse.getDWheel()*0.3) + scroll);
        }
        scroll = Math.max(0, Math.min(scroll, height-380));

        smoothScroll = MathUtil.lerp(smoothScroll,scroll,deltaTime*0.02);
        int heightL = 10 + posY;
        int heightR = 10 + posY;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        for (ModuleComponent module : modules) {
            ClickGui.maxOffset=380;
            ClickGui.minOffset=posY+10;
            GuiUtil.glScissor(posX, ClickGui.minOffset, 440, ClickGui.maxOffset);
            if (heightL <= heightR) {
                if (ClickGui.moving || flag) {
                    module.setPosition(posX + 10, heightL);
                }
                heightL += module.drawComponent(posX + 10, heightL, (int) smoothScroll, deltaTime, click, mouseX, mouseY);
            } else {
                if (ClickGui.moving || flag) {
                    module.setPosition(posX + 220, heightR);
                }
                heightR += module.drawComponent(posX + 220, heightR, (int) smoothScroll, deltaTime, click, mouseX, mouseY);
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        height = Math.max(heightL, heightR);
        height-=10+posY;
        flag = false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        modules.forEach(moduleComponent -> moduleComponent.keyTyped(typedChar, keyCode));
    }
}

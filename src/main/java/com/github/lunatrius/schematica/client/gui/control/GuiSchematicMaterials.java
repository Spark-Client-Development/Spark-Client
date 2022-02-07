package com.github.lunatrius.schematica.client.gui.control;

import com.github.lunatrius.core.client.gui.GuiScreenBase;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.client.util.BlockList;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;

public class GuiSchematicMaterials extends GuiScreenBase {
    private GuiSchematicMaterialsSlot guiSchematicMaterialsSlot;
    private GuiButton btnDump = null;
    private GuiButton btnDone = null;

    protected final List<BlockList.WrappedItemStack> blockList;

    public GuiSchematicMaterials(final GuiScreen guiScreen) {
        super(guiScreen);
        final Minecraft minecraft = Minecraft.getMinecraft();
        final SchematicWorld schematic = ClientProxy.schematic;
        this.blockList = new BlockList().getList(minecraft.player, schematic, minecraft.world);
        sort(blockList);
    }

    @Override
    public void initGui() {
        int id = 0;
        this.btnDump = new GuiButton(++id, this.width / 2 - 50, this.height - 30, 100, 20, "Save to file");
        this.buttonList.add(this.btnDump);

        this.btnDone = new GuiButton(++id, this.width / 2 + 54, this.height - 30, 100, 20, "Done");
        this.buttonList.add(this.btnDone);

        this.guiSchematicMaterialsSlot = new GuiSchematicMaterialsSlot(this);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.guiSchematicMaterialsSlot.handleMouseInput();
    }

    @Override
    protected void actionPerformed(final GuiButton guiButton) {
        if (guiButton.enabled) {
            if (guiButton.id == this.btnDump.id) {
                dumpMaterialList(this.blockList);
            } else if (guiButton.id == this.btnDone.id) {
                this.mc.displayGuiScreen(this.parentScreen);
            } else {
                this.guiSchematicMaterialsSlot.actionPerformed(guiButton);
            }
        }
    }

    @Override
    public void renderToolTip(final ItemStack stack, final int x, final int y) {
        super.renderToolTip(stack, x, y);
    }

    @Override
    public void drawScreen(final int x, final int y, final float partialTicks) {
        this.guiSchematicMaterialsSlot.drawScreen(x, y, partialTicks);

        String strMaterialName = "Material";
        drawString(this.fontRenderer, strMaterialName, this.width / 2 - 108, 4, 0x00FFFFFF);
        String strMaterialAmount = "Amount";
        drawString(this.fontRenderer, strMaterialAmount, this.width / 2 + 108 - this.fontRenderer.getStringWidth(strMaterialAmount), 4, 0x00FFFFFF);
        super.drawScreen(x, y, partialTicks);
    }

    private void dumpMaterialList(final List<BlockList.WrappedItemStack> blockList) {
        if (blockList.size() <= 0) {
            return;
        }

        int maxLengthName = 0;
        int maxSize = 0;
        for (final BlockList.WrappedItemStack wrappedItemStack : blockList) {
            maxLengthName = Math.max(maxLengthName, wrappedItemStack.getItemStackDisplayName().length());
            maxSize = Math.max(maxSize, wrappedItemStack.total);
        }

        final int maxLengthSize = String.valueOf(maxSize).length();
        final String formatName = "%-" + maxLengthName + "s";
        final String formatSize = "%" + maxLengthSize + "d";

        final StringBuilder stringBuilder = new StringBuilder((maxLengthName + 1 + maxLengthSize) * blockList.size());
        final Formatter formatter = new Formatter(stringBuilder);
        for (final BlockList.WrappedItemStack wrappedItemStack : blockList) {
            formatter.format(formatName, wrappedItemStack.getItemStackDisplayName());
            stringBuilder.append(" ");
            formatter.format(formatSize, wrappedItemStack.total);
            stringBuilder.append(System.lineSeparator());
        }

        final File dumps = Schematica.proxy.getDirectory("dumps");
        try {
            try (FileOutputStream outputStream = new FileOutputStream(new File(dumps, Reference.MODID + "-materials.txt"))) {
                IOUtils.write(stringBuilder.toString(), outputStream, Charset.forName("utf-8"));
            }
        } catch (final Exception e) {
            Reference.logger.error("Could not dump the material list!", e);
        }
    }

    private void sort(List<BlockList.WrappedItemStack> list) {
        Collections.sort(list, new Comparator<BlockList.WrappedItemStack>() {
            public int compare(BlockList.WrappedItemStack a, BlockList.WrappedItemStack b) {
                return Integer.compare(a.total, b.total);
            }
        });

    }

}

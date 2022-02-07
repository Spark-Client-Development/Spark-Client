package com.github.lunatrius.schematica.world.schematic;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.gen.structure.template.Template;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.nbt.NBTHelper;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.world.storage.Schematic;

public class SchematicStructure extends SchematicFormat {
    @Override
    public ISchematic readFromNBT(final NBTTagCompound tagCompound) {
        final ItemStack icon = SchematicUtil.getIconFromNBT(tagCompound);

        final Template template = new Template();
        template.read(tagCompound);

        final Schematic schematic = new Schematic(icon,
                template.size.getX(), template.size.getY(), template.size.getZ(), template.getAuthor());

        for (Template.BlockInfo block : template.blocks) {
            schematic.setBlockState(block.pos, block.blockState);
            if (block.tileentityData != null) {
                try {
                    // This position isn't included by default
                    block.tileentityData.setInteger("x", block.pos.getX());
                    block.tileentityData.setInteger("y", block.pos.getY());
                    block.tileentityData.setInteger("z", block.pos.getZ());

                    final TileEntity tileEntity = NBTHelper.readTileEntityFromCompound(block.tileentityData);
                    if (tileEntity != null) {
                        schematic.setTileEntity(block.pos, tileEntity);
                    }
                } catch (final Exception e) {
                    Reference.logger.error("TileEntity failed to load properly!", e);
                }
            }
        }

        // for (Template.EntityInfo entity : template.entities) {
        //     schematic.addEntity(...);
        // }

        return schematic;
    }


    @Override
    public String getName() {
        return "Structure block";
    }

    @Override
    public String getExtension() {
        return ".nbt";
    }
}

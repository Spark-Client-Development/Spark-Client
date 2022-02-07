package com.github.lunatrius.schematica.world.schematic;

import com.github.lunatrius.core.util.math.MBlockPos;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.nbt.NBTHelper;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.world.storage.Schematic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SchematicAlpha extends SchematicFormat {
    @Override
    public ISchematic readFromNBT(final NBTTagCompound tagCompound) {
        final ItemStack icon = SchematicUtil.getIconFromNBT(tagCompound);

        final byte[] localBlocks = tagCompound.getByteArray("Blocks");
        final byte[] localMetadata = tagCompound.getByteArray("Data");

        boolean extra = false;
        byte[] extraBlocks = null;
        byte[] extraBlocksNibble = null;
        if (tagCompound.hasKey("AddBlocks")) {
            extra = true;
            extraBlocksNibble = tagCompound.getByteArray("AddBlocks");
            extraBlocks = new byte[extraBlocksNibble.length * 2];
            for (int i = 0; i < extraBlocksNibble.length; i++) {
                extraBlocks[i * 2 + 0] = (byte) ((extraBlocksNibble[i] >> 4) & 0xF);
                extraBlocks[i * 2 + 1] = (byte) (extraBlocksNibble[i] & 0xF);
            }
        } else if (tagCompound.hasKey("Add")) {
            extra = true;
            extraBlocks = tagCompound.getByteArray("Add");
        }

        final short width = tagCompound.getShort("Width");
        final short length = tagCompound.getShort("Length");
        final short height = tagCompound.getShort("Height");

        Short id = null;
        final Map<Short, Short> oldToNew = new HashMap<Short, Short>();
        if (tagCompound.hasKey("SchematicaMapping")) {
            final NBTTagCompound mapping = tagCompound.getCompoundTag("SchematicaMapping");
            final Set<String> names = mapping.getKeySet();
            for (final String name : names) {
                oldToNew.put(mapping.getShort(name), (short) Block.REGISTRY.getIDForObject(Block.REGISTRY.getObject(new ResourceLocation(name))));
            }
        }

        final MBlockPos pos = new MBlockPos();
        final ISchematic schematic = new Schematic(icon, width, height, length);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    final int index = x + (y * length + z) * width;
                    int blockID = (localBlocks[index] & 0xFF) | (extra ? ((extraBlocks[index] & 0xFF) << 8) : 0);
                    final int meta = localMetadata[index] & 0xFF;

                    if ((id = oldToNew.get((short) blockID)) != null) {
                        blockID = id;
                    }

                    final Block block = Block.REGISTRY.getObjectById(blockID);
                    pos.set(x, y, z);
                    try {
                        final IBlockState blockState = block.getStateFromMeta(meta);
                        schematic.setBlockState(pos, blockState);
                    } catch (final Exception e) {
                        Reference.logger.error("Could not set block state at {} to {} with metadata {}", pos, Block.REGISTRY.getNameForObject(block), meta, e);
                    }
                }
            }
        }

        final NBTTagList tileEntitiesList = tagCompound.getTagList("TileEntities", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tileEntitiesList.tagCount(); i++) {
            try {
                final TileEntity tileEntity = NBTHelper.readTileEntityFromCompound(tileEntitiesList.getCompoundTagAt(i));
                if (tileEntity != null) {
                    schematic.setTileEntity(tileEntity.getPos(), tileEntity);
                }
            } catch (final Exception e) {
                Reference.logger.error("TileEntity failed to load properly!", e);
            }
        }

        return schematic;
    }


    @Override
    public String getName() {
        return "Alpha (standard)";
    }

    @Override
    public String getExtension() {
        return ".schematic";
    }
}

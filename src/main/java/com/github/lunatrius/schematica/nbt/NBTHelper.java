package com.github.lunatrius.schematica.nbt;

import com.github.lunatrius.schematica.world.WorldDummy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NBTHelper {
    public static TileEntity reloadTileEntity(TileEntity tileEntity, final int offsetX, final int offsetY, final int offsetZ) throws NBTConversionException {
        if (tileEntity == null) {
            return null;
        }

        try {
            final NBTTagCompound tileEntityCompound = writeTileEntityToCompound(tileEntity);
            tileEntity = readTileEntityFromCompound(tileEntityCompound);
            final BlockPos pos = tileEntity.getPos();
            tileEntity.setPos(pos.add(-offsetX, -offsetY, -offsetZ));
        } catch (final Throwable t) {
            throw new NBTConversionException(tileEntity, t);
        }

        return tileEntity;
    }

    public static Entity reloadEntity(Entity entity, final int offsetX, final int offsetY, final int offsetZ) throws NBTConversionException {
        if (entity == null) {
            return null;
        }

        try {
            final NBTTagCompound entityCompound = writeEntityToCompound(entity);
            if (entityCompound != null) {
                entity = readEntityFromCompound(entityCompound, WorldDummy.instance());

                if (entity != null) {
                    entity.posX -= offsetX;
                    entity.posY -= offsetY;
                    entity.posZ -= offsetZ;
                }
            }
        } catch (final Throwable t) {
            throw new NBTConversionException(entity, t);
        }

        return entity;
    }

    public static NBTTagCompound writeTileEntityToCompound(final TileEntity tileEntity) {
        final NBTTagCompound tileEntityCompound = new NBTTagCompound();
        tileEntity.writeToNBT(tileEntityCompound);
        return tileEntityCompound;
    }

    public static TileEntity readTileEntityFromCompound(final NBTTagCompound tileEntityCompound) {
        // TODO: world should NOT be null...
        return TileEntity.create(null, tileEntityCompound);
    }

    public static NBTTagCompound writeEntityToCompound(final Entity entity) {
        final NBTTagCompound entityCompound = new NBTTagCompound();
        if (entity.writeToNBTOptional(entityCompound)) {
            return entityCompound;
        }

        return null;
    }

    public static Entity readEntityFromCompound(final NBTTagCompound nbtTagCompound, final World world) {
        return EntityList.createEntityFromNBT(nbtTagCompound, world);
    }
}

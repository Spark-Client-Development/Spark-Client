package com.github.lunatrius.schematica.world.schematic;

import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;

public abstract class SchematicFormat {
    // LinkedHashMap to ensure defined iteration order
    public static final Map<String, SchematicFormat> FORMATS = new LinkedHashMap<String, SchematicFormat>();
    public static String FORMAT_DEFAULT;

    public abstract ISchematic readFromNBT(NBTTagCompound tagCompound);


    /**
     * Gets the translation key used for this format.
     */
    public abstract String getName();

    /**
     * Gets the file extension used for this format, including the leading dot.
     */
    public abstract String getExtension();

    public static ISchematic readFromFile(final File file) {
        try {
            final NBTTagCompound tagCompound = SchematicUtil.readTagCompoundFromFile(file);
            final SchematicFormat schematicFormat;
            if (tagCompound.hasKey("Materials")) {
                final String format = tagCompound.getString("Materials");
                schematicFormat = FORMATS.get(format);

                if (schematicFormat == null) {
                    throw new UnsupportedFormatException(format);
                }
            } else {
                schematicFormat = FORMATS.get("Structure");
            }

            return schematicFormat.readFromNBT(tagCompound);
        } catch (final Exception ex) {
            Reference.logger.error("Failed to read schematic!", ex);
        }

        return null;
    }

    public static ISchematic readFromFile(final File directory, final String filename) {
        return readFromFile(new File(directory, filename));
    }

    /**
     * Gets the extension used by the given format.
     *
     * If the format is invalid, returns the default format's extension.
     *
     * @param format The format (or null to use {@link #FORMAT_DEFAULT the default}).
     */
    public static String getExtension(@Nullable String format) {
        if (format == null) {
            format = FORMAT_DEFAULT;
        }
        if (!FORMATS.containsKey(format)) {
            Reference.logger.warn("No format with id {}; returning default extension", format, new UnsupportedFormatException(format).fillInStackTrace());
            format = FORMAT_DEFAULT;
        }
        return FORMATS.get(format).getExtension();
    }

    static {
        // TODO?
        // FORMATS.put(Names.NBT.FORMAT_CLASSIC, new SchematicClassic());
        FORMATS.put("Alpha", new SchematicAlpha());
        FORMATS.put("Structure", new SchematicStructure());

        FORMAT_DEFAULT = "Alpha";
    }
}

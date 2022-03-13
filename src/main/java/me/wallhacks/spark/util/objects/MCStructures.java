package me.wallhacks.spark.util.objects;


import net.minecraft.util.ResourceLocation;

public enum MCStructures {
    Village (new ResourceLocation("textures/icons/structures/village.png"), 7),
    EndCity (new ResourceLocation("textures/icons/structures/endcity.png"), 8),
    Mansion (new ResourceLocation("textures/icons/structures/mansion.png"), 15),
    Mineshaft (new ResourceLocation("textures/icons/structures/mineshaft.png"), 5),
    Stronghold (new ResourceLocation("textures/icons/structures/stronghold.png"), 18),
    OceanMonument (new ResourceLocation("textures/icons/structures/oceanmonument.png"), 6),
    DesertTemple (new ResourceLocation("textures/icons/structures/deserttemple.png"), 5),
    WitchHut (new ResourceLocation("textures/icons/structures/witchhut.png"), 5),
    Igloo (new ResourceLocation("textures/icons/structures/igloo.png"), 5),
    JungleTemple (new ResourceLocation("textures/icons/structures/jungletemple.png"), 5),
    NetherFortress (new ResourceLocation("textures/icons/structures/fortress.png"), 7);

    final ResourceLocation resourceLocation;
    final int size;

    MCStructures(ResourceLocation resourceLocation, int size) {
        this.resourceLocation = resourceLocation;
        this.size = size;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public int getSize() {
        return size;
    }
}

package com.github.lunatrius.schematica;

import com.github.lunatrius.schematica.proxy.CommonProxy;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.util.Map;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class Schematica {
    public static File schematicDirectory;
    @Mod.Instance(Reference.MODID)
    public static Schematica instance;

    @SidedProxy(clientSide = Reference.PROXY_CLIENT)
    public static CommonProxy proxy;

    @NetworkCheckHandler
    public boolean checkModList(final Map<String, String> versions, final Side side)
    {
        return true;
    }

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        proxy.init(event);
        schematicDirectory = new File(Schematica.proxy.getDataDirectory(), "./schematics");
    }

    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}

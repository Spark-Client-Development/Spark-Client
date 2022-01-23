package me.wallhacks.spark.util;

import net.minecraft.client.Minecraft;

public interface MC {
    Minecraft mc = Minecraft.getMinecraft();

    public default boolean nullCheck() {
        return mc.player == null || mc.world == null;
    }
    
    public default double posX() {
    	if(!nullCheck()) {
    		return mc.player.posX;
    	}
    	return 0d;
    }
    
    public default double posY() {
    	if(!nullCheck()) {
    		return mc.player.posY;
    	}
    	return 0d;
    }
    
    public default double posZ() {
    	if(!nullCheck()) {
    		return mc.player.posZ;
    	}
    	return 0d;
    }
    
    public default float rotationYaw() {
    	if(!nullCheck()) {
    		return mc.player.rotationYaw;
    	}
    	return 0f;
    }
    
    public default float rotationPitch() {
    	if(!nullCheck()) {
    		return mc.player.rotationPitch;
    	}
    	return 0f;
    }
}
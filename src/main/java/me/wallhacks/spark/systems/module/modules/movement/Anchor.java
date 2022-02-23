package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PlayerMoveEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.combat.HoleUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "Anchor", description = "Stops movement when over a hole to easily get in")
public class Anchor extends Module {
    IntSetting pitch = new IntSetting("MinPitch", this, 70, 0, 90);
    IntSetting height = new IntSetting("Height", this, 5, 1, 15);
    private static Anchor instance;
    public Anchor() {
        instance = this;
    }

    //nvm we just set priority to lowest to run this after all other stuff
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        if (event.isCanceled()) return;
        if (shouldAnchor()) {
            event.setCanceled(true);
            event.setX(0);
            event.setZ(0);
        }
    }

    //made this static so we can have other movement modules pause
    public static boolean shouldAnchor() {
        if (mc.player.rotationPitch < instance.pitch.getValue()) return false;
        AxisAlignedBB bb = mc.player.getEntityBoundingBox();
        if (bb.maxX % 1 < bb.minX % 1) return false;
        if (bb.maxZ % 1 < bb.minZ % 1) return false;
        for (int i = (int) bb.minY; i > bb.minY - instance.height.getValue();) {
            BlockPos p = new BlockPos(MathHelper.floor(mc.player.posX), i, MathHelper.floor(mc.player.posZ));
            i--;
            if (HoleUtil.isHole(p)) {
                return true;
            } else if (mc.world.getBlockState(p).getCollisionBoundingBox(mc.world, p) != null) return false;
        }
        return false;
    }
}

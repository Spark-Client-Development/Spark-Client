package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PlayerMoveEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.RotationUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "ElytraFly", description = "fly better")
public class ElytraFly extends Module {
    DoubleSetting movespeed = new DoubleSetting("MoveSpeed", this, 1,0,5);
    DoubleSetting upspeed = new DoubleSetting("UpSpeed", this, 0.4,0,4);

    BooleanSetting autoClose = new BooleanSetting("AutoClose", this, false);


    @SubscribeEvent
    public void onUpdate(PlayerMoveEvent event) {
        if(mc.player.isElytraFlying()) {


            if(autoClose.isOn()){
                BlockPos p = PlayerUtil.getPlayerPosFloored(mc.player);
                if(mc.world.getBlockState(p.add(0, -5, 0)).getBlock() != Blocks.AIR && mc.player.getTicksElytraFlying() > 40){
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    mc.player.setVelocity(0, 0, 0);
                }
            }

            final double[] dir = RotationUtil.directionSpeed(movespeed.getValue());
            if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0)
            {

                mc.player.motionX = dir[0];

                mc.player.motionZ = dir[1];

                mc.player.motionX -= (mc.player.motionX*(Math.abs(mc.player.rotationPitch)+90)/90) - mc.player.motionX;
                mc.player.motionZ -= (mc.player.motionZ*(Math.abs(mc.player.rotationPitch)+90)/90) - mc.player.motionZ;
            }
            else
            {
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
            }
            double y = 0;
            if(mc.gameSettings.keyBindJump.isKeyDown())
                y = upspeed.getValue();




            double Ymove = y+(-degToRad(mc.player.rotationPitch)) * mc.player.movementInput.moveForward;


            if(upspeed.getValue() == 0 && Ymove > 0)
                Ymove = 0;

            mc.player.motionY = Ymove;
        }
    }

    double degToRad(double deg)
    {
        return deg * (float) (Math.PI / 180.0f);
    }


}

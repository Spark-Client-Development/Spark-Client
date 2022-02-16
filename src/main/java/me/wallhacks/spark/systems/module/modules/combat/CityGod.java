package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.objects.Vec2i;
import me.wallhacks.spark.util.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

@Module.Registration(name = "CityGod", description = "Clips you into the corner of blocks so you take less crystal damage.")
public class CityGod extends Module implements MC {



	IntSetting ClipAmount = new IntSetting("ClipAmount",this,5,1,6);
	BooleanSetting FlagPlayer = new BooleanSetting("FlagPlayer",this,true);
	IntSetting ClipStep = new IntSetting("ClipStep",this,3,1,6);




	@SubscribeEvent
	public void onTick(LivingUpdateEvent e) {
		if(nullCheck())
			return;

		Vec2i dir = GetWallDir();


		BlockPos pos = PlayerUtil.GetPlayerPosHighFloored(mc.player);
		if(dir == null)
		{
			Spark.sendInfo("No clip pos Found!");
			this.disable();
			return;
		}


		double clipp = (ClipAmount.getValue()*0.01-0.001);


		Vec3d Center = new Vec3d(pos.getX()+0.5 + 0.2*dir.x,pos.getY(),pos.getZ()+0.5 + 0.2*dir.y);


		Vec3d Target = new Vec3d(Center.x+clipp*dir.x,Center.y,Center.z+clipp*dir.y);


		double l_XDiff = Math.abs(Target.x - mc.player.posX);
		double l_ZDiff = Math.abs(Target.z - mc.player.posZ);

		if (l_XDiff <= clipp+0.01 && l_ZDiff <= clipp+0.01)
		{
			mc.player.setVelocity(0, 0, 0);



			if (l_XDiff <= clipp+0.001 && l_ZDiff <= clipp+0.001){


				if (l_XDiff <= 0.001 && l_ZDiff <= 0.001)
				{

					return;
				}

				double mx = Math.abs(Math.max(0,clipp - l_XDiff) + ClipStep.getValue()*0.01);
				double mz = Math.abs(Math.max(0,clipp - l_ZDiff) + ClipStep.getValue()*0.01);



				Target = new Vec3d(
						Center.x+dir.x*Math.min(mx, clipp)
						,Center.y
						,Center.z+dir.y*Math.min(mz, clipp)
				);


				mc.player.setPosition(Target.x, Target.y, Target.z);



				if(FlagPlayer.isOn()){
					mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,mc.player.posY,mc.player.posZ,mc.player.onGround));
					Spark.rotationManager.sendPosPacketAndCancelNextUpdatePacket(new Vec3d(mc.player.posX+0.1*dir.y, mc.player.posY, mc.player.posZ+0.1*dir.y));

				}



			}
			else
				mc.player.setPosition(Center.x, mc.player.posY, Center.z);

		}
		else
		{
			double l_MotionX = Center.x-mc.player.posX;
			double l_MotionZ = Center.z-mc.player.posZ;

			mc.player.motionX = (l_MotionX/3);
			mc.player.motionZ = (l_MotionZ/3);
		}
	}



	int getBlocks(Vec2i vec2i){
		int i = 0;
		BlockPos pos = PlayerUtil.GetPlayerPosHighFloored(mc.player);

		if(vec2i.x != 0)
		{
			if(isBlockHard(pos.add(vec2i.x,0,0)))
				i++;
			else
				return 0;
		}
		if(vec2i.y != 0)
		{
			if(isBlockHard(pos.add(0,0,vec2i.y)))
				i++;
			else
				return 0;
		}

		return i;
	}
	boolean isBlockHard(BlockPos pos){
		Block b1 = mc.world.getBlockState(pos).getBlock();
		return b1 == Blocks.BEDROCK || b1 == Blocks.OBSIDIAN;
	}

	Vec2i GetWallDir(){
		Vec2i[] vecs = new Vec2i[]{new Vec2i(1,1),new Vec2i(1,-1),new Vec2i(-1,-1),new Vec2i(-1,1)
				,new Vec2i(0,1),new Vec2i(0,-1),new Vec2i(1,0),new Vec2i(-1,0)};

		BlockPos pos = PlayerUtil.GetPlayerPosHighFloored(mc.player);

		double bestv = 0;
		Vec2i best = null;

		for (Vec2i vec2i : vecs) {
			double v = getBlocks(vec2i)*10 - mc.player.getDistance(pos.getX()+0.5+vec2i.x, mc.player.posY, pos.getZ()+0.5+vec2i.y);

			if(v > bestv)
			{
				bestv = v;
				best = vec2i;
			}
		}
		return best;
	}


	//utils for geting dir
}

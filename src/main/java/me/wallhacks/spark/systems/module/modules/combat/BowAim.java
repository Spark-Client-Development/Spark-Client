package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.combat.PredictionUtil;
import me.wallhacks.spark.util.player.RotationUtil;
import me.wallhacks.spark.util.render.EspUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Module.Registration(name = "BowAimAssist", description = "Makes you not suck with the bow")
public class BowAim extends Module implements MC {

	BooleanSetting SilentRotate = new BooleanSetting("SilentRotate",this,false);

	BooleanSetting PlayersOnly = new BooleanSetting("PlayersOnly", this, true);

	BooleanSetting Esp = new BooleanSetting("Esp", this, true);

	EntityLivingBase target = null;


	@SubscribeEvent
	void OnUpdate(PlayerUpdateEvent event) {


		if (mc.player.getHeldItemMainhand().getItem() != Items.BOW)
			return;

		if (!mc.player.isHandActive() || mc.player.getActiveItemStack().isEmpty())
			return;



		target = GetTarget();

		if(target == null)
			return;







		float[] rot = getRot(target);

		if(SilentRotate.isOn()){


			Spark.rotationManager.rotate(rot, true);


		}
		else {

			mc.player.rotationYaw = rot[0];
			mc.player.rotationPitch = rot[1];

		}

	}


	public float[] getRot(EntityLivingBase target){

		double time = Math.max(2,mc.player.getDistance(target))/2;


		AxisAlignedBB bb = PredictionUtil.PredictedTarget(target, (int)time);
		Vec3d cen = bb.getCenter();





		Vec3d dir = cen.subtract(mc.player.boundingBox.getCenter());


		float velocity = 1;



		double hDistance = Math.sqrt(dir.x * dir.x + dir.z * dir.z);
		double hDistanceSq = hDistance * hDistance;
		float g = 0.006F;
		float velocitySq = velocity * velocity;

		float velocityPow4 = velocitySq * velocitySq;
		float pitch = (float)-Math.toDegrees(Math.atan((velocitySq - Math
				.sqrt(velocityPow4 - g * (g * hDistanceSq + 2 * dir.y * velocitySq)))
				/ (g * hDistance)));

		float yaw = (float)Math.toDegrees(Math.atan2(dir.z, dir.x)) - 90;


		float[] myRot = new float[]{mc.player.rotationYaw,mc.player.rotationPitch};

		if(Spark.rotationManager.getFakeRotationPitch() != null)
			myRot[1] = Spark.rotationManager.getFakeRotationPitch();
		if(Spark.rotationManager.getFakeRotationYaw() != null)
			myRot[0] = Spark.rotationManager.getFakeRotationYaw();

		return new float[] {myRot[0] + MathHelper.wrapDegrees(yaw-myRot[0]), myRot[1]+ MathHelper.wrapDegrees(pitch-myRot[1]) };

	}


	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {

		GL11.glPushMatrix();

		if(target != null && Esp.isOn() && !target.isDead){
			EspUtil.boundingESPBox(EspUtil.getRenderBB(target),new Color(33, 189, 82, 155), 2.0f);
		}

		GL11.glPopMatrix();
	}


	private EntityLivingBase GetTarget(){

		double BestDis = Double.MAX_VALUE;
		EntityLivingBase t = null;
		for(Entity entity : mc.world.loadedEntityList){

			if(entity instanceof EntityLivingBase){
				EntityLivingBase e = (EntityLivingBase)entity;
				if(PlayersOnly.isOn() && !(e instanceof EntityPlayer))
					continue;
				if(AttackUtil.canAttackEntity(e,100)) {
					double thisDis = mc.player.getDistance(e);
					if(mc.player.canEntityBeSeen(e))
					{
						thisDis *= 0.2;
						if(Math.abs(RotationUtil.getViewRotations(e.getPositionVector(), mc.player)[0]-mc.player.rotationYaw) < 20)
							thisDis *= 0.5;
					}

					if(thisDis < BestDis)
					{
						BestDis = thisDis;
						t = e;
					}

				}
			}

		}
		return t;

	}

}

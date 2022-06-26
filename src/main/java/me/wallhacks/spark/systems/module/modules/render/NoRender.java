package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.event.player.PlayerIsPotionActiveEvent;
import me.wallhacks.spark.event.render.RenderHurtCameraEffectEvent;
import me.wallhacks.spark.event.render.SpawnParticleEvent;
import me.wallhacks.spark.systems.module.Module;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.*;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;

@Module.Registration(name = "NoRender", description = "Stop rendering shit we don't want to render")
public class NoRender extends Module {

    BooleanSetting fog = new BooleanSetting("LiquidFog",this,true);
    BooleanSetting hurtCam = new BooleanSetting("HurtCam",this,true);
    //public cause used in mixin
    public BooleanSetting armor = new BooleanSetting("Armor",this,false);
    BooleanSetting blindness = new BooleanSetting("PotionEffects",this,true);



    BooleanSetting overlay = new BooleanSetting("BlockOverlay",this,true);
    BooleanSetting potionIcon = new BooleanSetting("PotionIcons",this,true);
    BooleanSetting bosshealth = new BooleanSetting("Bosshealth",this,false);


    BooleanSetting explosion = new BooleanSetting("Explosion",this,true);
    BooleanSetting firework = new BooleanSetting("Fireworks",this,false);
    BooleanSetting blockbreaking = new BooleanSetting("Blockbreaking",this,false);





    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        if(overlay.isOn())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderGameOverlayEvent event) {

        if (overlay.getValue()) {
            if (event.getType().equals(RenderGameOverlayEvent.ElementType.HELMET)) {
                event.setCanceled(true);
            }
            if (event.getType().equals(RenderGameOverlayEvent.ElementType.PORTAL)) {
                event.setCanceled(true);
            }

        }
        if(potionIcon.isOn())
        {
            if (event.getType().equals(RenderGameOverlayEvent.ElementType.POTION_ICONS))
                event.setCanceled(true);
        }


    }
    @SubscribeEvent
    public void onBossBar(RenderGameOverlayEvent.BossInfo e) {
        if(bosshealth.isOn()) {

            e.setCanceled(true);
        }
    }



    @SubscribeEvent
    public void onFog(EntityViewRenderEvent.FogDensity event) {
        if (fog.getValue()) {
            if (event.getState().getMaterial().equals(Material.WATER)
                    || event.getState().getMaterial().equals(Material.LAVA) ) {
                        event.setDensity(0);
                        event.setCanceled(true);
            }
        }
    }


    @SubscribeEvent
    public void onFog(PlayerIsPotionActiveEvent event) {
        if (blindness.isOn()){
            if(event.potion == MobEffects.BLINDNESS || event.potion == MobEffects.NAUSEA)
                event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public void onHurtCam(RenderHurtCameraEffectEvent event) {
        if(hurtCam.isOn())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onParticles(SpawnParticleEvent event) {
        if(explosion.isOn())
        {
            if(event.getParticle() instanceof ParticleExplosion
                    || event.getParticle() instanceof ParticleExplosionHuge
                    || event.getParticle() instanceof ParticleSmokeNormal
                    || event.getParticle() instanceof ParticleExplosionLarge)
                event.setCanceled(true);
        }
        if(firework.isOn())
        {
            if(event.getParticle() instanceof ParticleFirework.Overlay
                    || event.getParticle() instanceof ParticleFirework.Spark
                    || event.getParticle() instanceof ParticleFirework.Starter)
                event.setCanceled(true);
        }
        if(blockbreaking.isOn())
        {
            if(event.getParticle() instanceof ParticleBreaking)
                event.setCanceled(true);
        }
    }






}

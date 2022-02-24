package me.wallhacks.spark.manager;

import io.netty.util.internal.ConcurrentSet;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.Pair;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.Array;
import scala.Int;

import java.util.*;

public class PotionManager implements MC {


    HashMap<EntityPlayer, ArrayList<Pair<PotionEffect, Long>>> trackMap = new HashMap<>();
    HashMap<Integer, Collection<PotionEffect>> potionMap = new HashMap<Integer, Collection<PotionEffect>>();

    public PotionManager() {
        Spark.eventBus.register(this);
        MixPotions();
    }

    void MixPotions() {

        ArrayList<PotionEffect> possiblePotions = new ArrayList<PotionEffect>();

        possiblePotions.add(new PotionEffect(MobEffects.STRENGTH, 10, 0));
        possiblePotions.add(new PotionEffect(MobEffects.STRENGTH, 10, 1));

        possiblePotions.add(new PotionEffect(MobEffects.SPEED, 10, 0));
        possiblePotions.add(new PotionEffect(MobEffects.SPEED, 10, 1));

        possiblePotions.add(new PotionEffect(MobEffects.ABSORPTION, 10, 3));

        possiblePotions.add(new PotionEffect(MobEffects.REGENERATION, 10, 0));
        possiblePotions.add(new PotionEffect(MobEffects.REGENERATION, 10, 1));

        possiblePotions.add(new PotionEffect(MobEffects.RESISTANCE, 10, 0));

        possiblePotions.add(new PotionEffect(MobEffects.FIRE_RESISTANCE, 10, 0));

        possiblePotions.add(new PotionEffect(MobEffects.WEAKNESS, 10, 0));


        possiblePotions.add(new PotionEffect(MobEffects.ABSORPTION, 10, 0));

        long limit = 1 << possiblePotions.size(); // this is 2^length

        for (long l = 1; l < limit; l++) {
            Set<PotionEffect> subSet = new LinkedHashSet<>();
            for (int i = 0; i < possiblePotions.size(); i++) {
                if ((l & (1 << i)) > 0) {
                    subSet.add(possiblePotions.get(i));
                }
            }
            potionMap.put(PotionUtils.getPotionColorFromEffectList(subSet), new HashSet<>(subSet));
        }

    }

    public Collection<PotionEffect> potionEffectsForLiving(EntityLivingBase entity) {
        int i = ((Integer) entity.dataManager.get(EntityLivingBase.POTION_EFFECTS)).intValue();
        return potionEffectsForValue(i);
    }
    public Collection<PotionEffect> potionEffectsForValue(int value) {

        if (potionMap.containsKey(value))
            return potionMap.get(value);
        return new ArrayList<>();
    }

    public int getPotionStrength(EntityPlayer player,Potion potion) {
        Pair<PotionEffect,Long> longPair = getEffect(player,potion);
        return longPair == null ? 0 : longPair.getKey().getAmplifier();
    }

    public long getPotionTime(EntityPlayer player,Potion potion) {
        Pair<PotionEffect,Long> longPair = getEffect(player,potion);
        return longPair == null ? 0 : longPair.getValue();
    }
    public long getPotionDuration(EntityPlayer player,Potion potion) {

        return System.currentTimeMillis()-getPotionTime(player,potion);
    }

    public Pair<PotionEffect,Long> getEffect(EntityPlayer player,Potion potion) {
        if(trackMap.containsKey(player))
        {
            for (Pair<PotionEffect, Long> p : trackMap.get(player)) {
                if (p.getKey().getPotion() == potion) {
                    return p;
                }
            }
        }

        return null;
    }

    @SubscribeEvent
    public void updatePotions(PacketReceiveEvent event) {

        if (event.getPacket() instanceof SPacketEntityMetadata) {
            SPacketEntityMetadata packet = event.getPacket();
            if(mc.world == null)
                return;
            if(mc.world.getEntityByID(packet.getEntityId()) instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) mc.world.getEntityByID(packet.getEntityId());
                for (EntityDataManager.DataEntry v : packet.getDataManagerEntries()) {
                    if (v.getKey().equals(EntityLivingBase.POTION_EFFECTS)) {
                        Collection<PotionEffect> effects = potionEffectsForValue((int)v.getValue());

                        if(!trackMap.containsKey(player))
                            trackMap.put(player,new ArrayList<>());

                        ArrayList<Pair<PotionEffect, Long> > old = trackMap.get(player);

                        ArrayList<Pair<PotionEffect, Long> > newMap = new ArrayList<> ();

                        loop:
                        for (PotionEffect potionEffect : effects) {
                            for (Pair<PotionEffect, Long> p : old) {
                                if (p.getKey() == potionEffect)
                                {
                                    newMap.add(p);
                                    old.remove(p);
                                    continue loop;
                                }
                            }
                            newMap.add(new Pair<PotionEffect, Long>(potionEffect,System.currentTimeMillis()));
                        }

                        trackMap.put(player,newMap);


                    }
                }

            }
        }

    }
}

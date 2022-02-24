package me.wallhacks.spark.manager;

import io.netty.util.internal.ConcurrentSet;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.Pair;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.Int;

import java.util.*;

public class PotionManager implements MC {


    public HashMap<EntityPlayer, ArrayList<Pair<PotionEffect, Integer>>> trackMap = new HashMap<>();
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

        if (potionMap.containsKey(i))
            return potionMap.get(i);
        return new ArrayList<>();
    }

    @SubscribeEvent
    public void updatePotions(PlayerUpdateEvent event) {
        for (EntityPlayer player : mc.world.playerEntities) {
            ArrayList<Pair<PotionEffect, Integer>> playerMap = new ArrayList<>();
            if (trackMap.containsKey(player)) {
                for (PotionEffect potionEffect : potionEffectsForLiving(player)) {
                    int old = 1;
                    for (Pair<PotionEffect, Integer> p : trackMap.get(player)) {
                        if (p.getKey() == potionEffect) old += p.getValue();
                    }
                    playerMap.add(new Pair<>(potionEffect, old));
                }
            } else {
                for (PotionEffect potionEffect : potionEffectsForLiving(player)) {
                    playerMap.add(new Pair<>(potionEffect, 1));
                }
            }
            trackMap.put(player, playerMap);
        }
        ArrayList<EntityPlayer> remove = new ArrayList<>();
        for (EntityPlayer player : trackMap.keySet()) {
            if (!mc.world.playerEntities.contains(player)) remove.add(player);
        }
        remove.forEach(p -> trackMap.remove(p));
    }
}

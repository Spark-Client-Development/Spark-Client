package me.wallhacks.spark.systems.module.modules.misc;

import com.mojang.authlib.GameProfile;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.SessionUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.StringSetting;

import java.util.UUID;

@Module.Registration(name = "FakePlayer", description = "Adds a fake player to your world")
public class FakePlayer extends Module {

    StringSetting name = new StringSetting("Name",this,"fit");

    @SubscribeEvent
    public void onWorld(WorldEvent.Load event) {
        setEnabled(false);
    }

    private EntityOtherPlayerMP fakePlayer;

    @Override
    public void onEnable() {
        if (mc.world == null) return;

        String name = this.name.getValue();
        UUID id = SessionUtils.getid(name);

        boolean isonline = true;

        GameProfile gm = new GameProfile(id,name);

        NetworkPlayerInfo info = mc.getConnection().getPlayerInfo(name);



        if(info == null){
            info = new NetworkPlayerInfo(gm);
            if(!SessionUtils.setSkin(info, gm.getId()))
                SessionUtils.setSkin(info, SessionUtils.getid("divad10"));

            isonline = false;
        }
        else
            gm = info.getGameProfile();

        fakePlayer = new EntityOtherPlayerMP(mc.world, gm);


        fakePlayer.getDataManager().setEntryValues(mc.player.getDataManager().getAll());


        if(!isonline){
            NetworkPlayerInfo networkplayerinfo = new NetworkPlayerInfo(gm);
            networkplayerinfo.setResponseTime(9+(int)(Math.random()*100));
            fakePlayer.playerInfo = networkplayerinfo;

            mc.player.connection.playerInfoMap.put(id, networkplayerinfo);

        }



        fakePlayer.copyLocationAndAnglesFrom(mc.player);

        fakePlayer.inventory.copyInventory(mc.player.inventory);
        fakePlayer.setHealth(mc.player.getHealth());
        fakePlayer.setAbsorptionAmount(mc.player.getAbsorptionAmount());

        mc.world.addEntityToWorld(-7, fakePlayer);





        if(!isonline){
            mc.player.connection.getPlayerInfoMap().remove(id);
        }

    }

    @Override
    public void onDisable() {
        try {
            if (fakePlayer != null)
                mc.world.removeEntity(fakePlayer);
        } catch (Exception ignored) {
        }
    }

}

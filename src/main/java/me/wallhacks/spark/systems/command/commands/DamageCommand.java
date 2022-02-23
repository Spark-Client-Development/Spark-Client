package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.command.Command;
import me.wallhacks.spark.util.MC;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DamageCommand extends Command implements MC {
    int stage = 0;
    int todo = 0;
    public DamageCommand() {
        super();
        Spark.eventBus.register(this);
        addUsage("<damage>");
    }

    @Override
    public void run(String[] args) {
        if(args.length >= 1) {
            try {
                stage = 1;
                todo = Integer.parseInt(args[0])*2;
                Spark.logger.info(todo);
            } catch(NumberFormatException e) {
            }
        } else {
            noArgInfo();
        }
    }

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        if (stage == 1 && todo > 0) {
            todo--;
            mc.player.connection.sendPacket(new CPacketPlayer.Position(posX(), posY() - 1e-10, posZ(), true));
            stage = 3;
        } else if (stage == 3 || stage == 4) {
            stage++;
        } else if (stage == 5) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw + 180, mc.player.rotationPitch, true));
            stage++;
        } else if (stage == 6) {
            stage = 1;
        }
    }

    @Override
    public String getName() {
        return "damage";
    }
}

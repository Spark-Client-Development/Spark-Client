package me.wallhacks.spark.systems.module.modules.misc;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.StringSetting;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "Suffix", description = "Adds a suffix behind your chat messages")
public class ChatSuffix extends Module {
    BooleanSetting custom = new BooleanSetting("Custom", this, false);
    StringSetting suffix = new StringSetting("Suffix", this, "SingDupe.Sex");

    @SubscribeEvent
    public void onPacketReceive(PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage p = event.getPacket();
            if (p.getMessage().startsWith("/") || p.getMessage().startsWith(".") || p.getMessage().startsWith("?") || p.getMessage().startsWith("!")) return;
            else {
                String suffix = " \u23d0 " + (custom.getValue() ? toUnicode(this.suffix.getValue()) : toUnicode(Spark.NAME));
                p.message += suffix;
            }
        }
    }

    //didnt write this myself idk where i got it from anymore tho
    public static String toUnicode(String s) {
        return s.toLowerCase()
                .replace("a", "\u1d00")
                .replace("b", "\u0299")
                .replace("c", "\u1d04")
                .replace("d", "\u1d05")
                .replace("e", "\u1d07")
                .replace("f", "\ua730")
                .replace("g", "\u0262")
                .replace("h", "\u029c")
                .replace("i", "\u026a")
                .replace("j", "\u1d0a")
                .replace("k", "\u1d0b")
                .replace("l", "\u029f")
                .replace("m", "\u1d0d")
                .replace("n", "\u0274")
                .replace("o", "\u1d0f")
                .replace("p", "\u1d18")
                .replace("q", "\u01eb")
                .replace("r", "\u0280")
                .replace("s", "\ua731")
                .replace("t", "\u1d1b")
                .replace("u", "\u1d1c")
                .replace("v", "\u1d20")
                .replace("w", "\u1d21")
                .replace("x", "\u02e3")
                .replace("y", "\u028f")
                .replace("z", "\u1d22");
    }

}

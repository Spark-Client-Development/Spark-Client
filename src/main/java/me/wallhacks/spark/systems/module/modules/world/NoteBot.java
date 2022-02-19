package me.wallhacks.spark.systems.module.modules.world;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.player.UpdateWalkingPlayerEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.command.commands.NoteBotCommand;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Module.Registration(name = "NoteBot", description = "Makes music with noteblocks")
public class NoteBot extends Module {
    public static NoteBot INSTANCE;
    public NoteBot() {
        INSTANCE = this;
    }

    BooleanSetting tune = new BooleanSetting("Tune", this, false);
    BooleanSetting render = new BooleanSetting("Render", this, false);
    ColorSetting color = new ColorSetting("Color", this, new Color(0x940DEA7C, true));
    private final Map<Sound, Byte> soundBytes = new HashMap<Sound, Byte>();
    private final List<SoundEntry> soundEntries = new ArrayList<SoundEntry>();
    private final List<BlockPos> posList = new ArrayList<BlockPos>();
    private final Path path = Spark.ParentPath.toPath().resolve("songs");
    private final File file = new File(path.toString());
    private IRegister[] registers;
    private int soundIndex;
    private int index;
    private Map<BlockPos, AtomicInteger> posPitch;
    private Map<Sound, BlockPos[]> soundPositions;
    private BlockPos currentPos;
    private BlockPos nextPos;
    private BlockPos endPos;
    private int tuneStage;
    private int tuneIndex;
    private boolean tuned;

    public static Map<Sound, BlockPos[]> setUpSoundMap() {
        BlockPos var0 = mc.player.getPosition();
        LinkedHashMap<Sound, BlockPos[]> result = new LinkedHashMap<Sound, BlockPos[]>();
        HashMap atomicSounds = new HashMap();
        Arrays.asList(Sound.values()).forEach(sound -> {
            BlockPos[] var10002 = new BlockPos[25];
            result.put((Sound) sound, var10002);
            atomicSounds.put(sound, new AtomicInteger());
        });
        for (int x = -6; x < 6; ++x) {
            for (int y = -1; y < 5; ++y) {
                for (int z = -6; z < 6; ++z) {
                    Sound sound2;
                    int soundByte;
                    BlockPos pos = mc.player.getPosition().add(x, y, z);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (!(NoteBot.distanceSqToCenter(pos) < 27.040000000000003) || block != Blocks.NOTEBLOCK || (soundByte = ((AtomicInteger) atomicSounds.get(sound2 = NoteBot.getSoundFromBlockState(mc.world.getBlockState(pos.down())))).getAndIncrement()) >= 25)
                        continue;
                    result.get(sound2)[soundByte] = pos;
                }
            }
        }
        return result;
    }

    private static double distanceSqToCenter(BlockPos pos) {
        double var1 = Math.abs(mc.player.posX - (double) pos.getX() - 0.5);
        double var3 = Math.abs(mc.player.posY + (double) mc.player.getEyeHeight() - (double) pos.getY() - 0.5);
        double var5 = Math.abs(mc.player.posZ - (double) pos.getZ() - 0.5);
        return var1 * var1 + var3 * var3 + var5 * var5;
    }

    private static IRegister[] createRegister(File file) throws IOException {
        int n2;
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] arrby = new byte[fileInputStream.available()];
        fileInputStream.read(arrby);
        ArrayList<IRegister> arrayList = new ArrayList<IRegister>();
        boolean bl = true;
        byte[] arrby2 = arrby;
        int n4 = arrby2.length;
        for (int i = 0; i < arrby2.length; ++i) {
            n2 = arrby2[i];
            if (n2 != 64) continue;
            bl = false;
            break;
        }
        int n = 0;
        int n6 = 0;
        while (n6 < arrby.length) {
            n4 = arrby[n];
            if (n4 == (bl ? 5 : 64)) {
                byte[] arrby3 = new byte[]{arrby[++n], arrby[++n]};
                byte[] arrby4 = arrby3;
                n2 = arrby3[0] & 0xFF | (arrby4[1] & 0xFF) << 8;
                arrayList.add(new SimpleRegister(n2));
            } else {
                arrayList.add(new SoundRegister(Sound.values()[n4], arrby[++n]));
            }
            n6 = ++n;
        }
        ArrayList<IRegister> arrayList2 = arrayList;
        return arrayList2.toArray(new IRegister[arrayList2.size()]);
    }

    public static void unzip(File file1, File fileIn) {
        ZipEntry zipEntry;
        ZipInputStream zipInputStream;
        byte[] var2 = new byte[1024];
        try {
            if (!fileIn.exists()) {
                fileIn.mkdir();
            }
            zipInputStream = new ZipInputStream(new FileInputStream(file1));
            zipEntry = zipInputStream.getNextEntry();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        while (true) {
            FileOutputStream outputStream;
            try {
                int index;
                if (zipEntry == null) break;
                String fileName = zipEntry.getName();
                File newFile = new File(fileIn, fileName);
                new File(newFile.getParent()).mkdirs();
                outputStream = new FileOutputStream(newFile);
                while ((index = zipInputStream.read(var2)) > 0) {
                    outputStream.write(var2, 0, index);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return;
            }
            try {
                outputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return;
            }
        }
        try {
            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public static Sound getSoundFromBlockState(IBlockState state) {
        if (state.getBlock() == Blocks.CLAY) {
            return Sound.CLAY;
        }
        if (state.getBlock() == Blocks.GOLD_BLOCK) {
            return Sound.GOLD;
        }
        if (state.getBlock() == Blocks.WOOL) {
            return Sound.WOOL;
        }
        if (state.getBlock() == Blocks.PACKED_ICE) {
            return Sound.ICE;
        }
        if (state.getBlock() == Blocks.BONE_BLOCK) {
            return Sound.BONE;
        }
        if (state.getMaterial() == Material.ROCK) {
            return Sound.ROCK;
        }
        if (state.getMaterial() == Material.SAND) {
            return Sound.SAND;
        }
        if (state.getMaterial() == Material.GLASS) {
            return Sound.GLASS;
        }
        return state.getMaterial() == Material.WOOD ? Sound.WOOD : Sound.NONE;
    }

    public File getNoteBotDir() {
        return file;
    }


    @Override
    public void onEnable() {
        if (nullCheck()) {
            disable();
            return;
        }
        soundEntries.clear();
        getNoteBlocks();
        soundIndex = 0;
        index = 0;
        resetTuning();
    }

    @SubscribeEvent
    public void onSettingChange(SettingChangeEvent event) {
        if (event.getSetting() == tune && tune.getValue()) {
            resetTuning();
        }
    }

    public void setSong(String song) {
        try {
            registers = NoteBot.createRegister(new File(file.getAbsolutePath(), song));
            Spark.sendInfo("Loaded: " + song);
        } catch (Exception e) {
            Spark.sendInfo("An Error occurred with " + song);
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (tune.getValue() && event.getPacket() instanceof SPacketBlockAction && tuneStage == 0 && soundPositions != null) {
            SPacketBlockAction packet = event.getPacket();
            Sound sound = Sound.values()[packet.getData1()];
            int pitch = packet.getData2();
            BlockPos[] positions = soundPositions.get(sound);
            for (int i = 0; i < 25; ++i) {
                BlockPos position = positions[i];
                if (!packet.getBlockPosition().equals(position)) continue;
                if (posPitch.get(position).intValue() != -1) break;
                int pitchDif = i - pitch;
                if (pitchDif < 0) {
                    pitchDif += 25;
                }
                posPitch.get(position).set(pitchDif);
                if (pitchDif == 0) break;
                tuned = false;
                break;
            }
            if (endPos.equals(packet.getBlockPosition()) && tuneIndex >= posPitch.values().size()) {
                tuneStage = 1;
            }
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEventPre(PlayerUpdateEvent event) {
        if (tune.getValue()) {
            tune();
        } else if (isEnabled()) {
            noteBotPre();
            noteBotPost();
        }
    }

    public static EnumFacing getFacing(BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) pos.getX() + 0.5 + (double) facing.getDirectionVec().getX() * 1.0 / 2.0, (double) pos.getY() + 0.5 + (double) facing.getDirectionVec().getY() * 1.0 / 2.0, (double) pos.getZ() + 0.5 + (double) facing.getDirectionVec().getZ() * 1.0 / 2.0), false, true, false);
            if (rayTraceResult != null && (rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK || !rayTraceResult.getBlockPos().equals(pos)))
                continue;
            return facing;
        }
        if ((double) pos.getY() > mc.player.posY + (double) mc.player.getEyeHeight()) {
            return EnumFacing.DOWN;
        }
        return EnumFacing.UP;
    }

    private void tune() {
        currentPos = null;
        if (tuneStage == 1 && getAtomicBlockPos(null) == null) {
            if (tuned) {
                Spark.sendInfo("Done tuning ready to go");
                tune.setValue(false);
            } else {
                tuned = true;
                tuneStage = 0;
                tuneIndex = 0;
            }
        } else {
            if (tuneStage != 0) {
                nextPos = currentPos = getAtomicBlockPos(nextPos);
            } else {
                while (tuneIndex < 250 && currentPos == null) {
                    currentPos = soundPositions.get(Sound.values()[(int) Math.floor(tuneIndex / 25)])[tuneIndex % 25];
                    ++tuneIndex;
                }
            }
            if (currentPos != null) {
                Spark.rotationManager.rotate(RotationUtil.getViewRotations(new Vec3d(currentPos).add(0.5, 0.5, 0.5), mc.player), AntiCheatConfig.getInstance().getBlockRotStep(), 2, true);
            }
        }
        if (tuneStage == 0 && currentPos != null) {
            EnumFacing facing = getFacing(this.currentPos);
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, facing));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, currentPos, facing));
        } else if (currentPos != null) {
            posPitch.get(currentPos).decrementAndGet();
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(currentPos, getFacing(currentPos), EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        }
    }

    private void resetTuning() {
        if (mc.world == null || mc.player == null) {
            disable();
            return;
        }
        tuned = true;
        soundPositions = NoteBot.setUpSoundMap();
        posPitch = new LinkedHashMap<BlockPos, AtomicInteger>();
        soundPositions.values().forEach(array -> Arrays.asList(array).forEach(pos -> {
            if (pos != null) {
                endPos = pos;
                posPitch.put(pos, new AtomicInteger(-1));
            }
        }));
        tuneStage = 0;
        tuneIndex = 0;
    }

    private BlockPos getAtomicBlockPos(BlockPos blockPos) {
        AtomicInteger atomicInteger;
        BlockPos blockPos2;
        Iterator<Map.Entry<BlockPos, AtomicInteger>> iterator = posPitch.entrySet().iterator();
        do {
            if (!iterator.hasNext()) {
                return null;
            }
            Map.Entry<BlockPos, AtomicInteger> entry = iterator.next();
            blockPos2 = entry.getKey();
            atomicInteger = entry.getValue();
        } while (blockPos2 == null || blockPos2.equals(blockPos) || atomicInteger.intValue() <= 0);
        return blockPos2;
    }

    private void noteBotPre() {
        posList.clear();
        if (registers == null) {
            return;
        }
        while (index < registers.length) {
            IRegister register = registers[index];
            if (register instanceof SimpleRegister) {
                SimpleRegister simpleRegister = (SimpleRegister) register;
                if (++soundIndex >= simpleRegister.getSound()) {
                    ++index;
                    soundIndex = 0;
                }
                if (posList.size() > 0) {
                    BlockPos blockPos = posList.get(0);
                    Spark.rotationManager.rotate(RotationUtil.getViewRotations(new Vec3d(blockPos).add(0.5, 0.5, 0.5), mc.player), AntiCheatConfig.getInstance().getBlockRotStep(), 2, true);
                }
                return;
            }
            if (!(register instanceof SoundRegister)) continue;
            SoundRegister soundRegister = (SoundRegister) register;
            BlockPos pos = getRegisterPos(soundRegister);
            if (pos != null) {
                posList.add(pos);
            }
            ++index;
        }
        index = 0;
    }

    private void noteBotPost() {
        for (int i = 0; i < posList.size(); ++i) {
            BlockPos pos = posList.get(i);
            if (pos == null) continue;
            if (i != 0) {
                float[] rotations = RotationUtil.getViewRotations(new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f), mc.player);
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround));
            }
            clickNoteBlock(pos);
        }
    }

    private void getNoteBlocks() {
        fillSoundBytes();
        for (int x = -6; x < 6; ++x) {
            for (int y = -1; y < 5; ++y) {
                for (int z = -6; z < 6; ++z) {
                    Sound sound;
                    byte soundByte;
                    BlockPos pos = mc.player.getPosition().add(x, y, z);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (!(pos.distanceSqToCenter(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ) < 27.0) || block != Blocks.NOTEBLOCK || (soundByte = soundBytes.get(sound = NoteBot.getSoundFromBlockState(mc.world.getBlockState(pos.down()))).byteValue()) > 25)
                        continue;
                    soundEntries.add(new SoundEntry(pos, new SoundRegister(sound, soundByte)));
                    soundBytes.replace(sound, (byte) (soundByte + 1));
                }
            }
        }
    }

    private void fillSoundBytes() {
        soundBytes.clear();
        for (Sound sound : Sound.values()) {
            soundBytes.put(sound, (byte) 0);
        }
    }

    private void clickNoteBlock(BlockPos pos) {
        EnumFacing facing = getFacing(pos);
        if (render.getValue())
            new FadePos(pos, color);
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, facing));
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, pos, facing));
    }

    private BlockPos getRegisterPos(SoundRegister register) {
        SoundEntry soundEntry = soundEntries.stream().filter(entry -> entry.getRegister().equals(register)).findFirst().orElse(null);
        if (soundEntry == null) {
            return null;
        }
        return soundEntry.getPos();
    }

    public void downloadSongs() {
        new Thread(() -> {
            try {
                File songFile = new File(Spark.ParentPath, "songs.zip");
                FileChannel fileChannel = new FileOutputStream(songFile).getChannel();
                ReadableByteChannel readableByteChannel = Channels.newChannel(new URL("https://github.com/wallhacks0/notebotArchive/raw/main/songs.zip").openStream());
                fileChannel.transferFrom(readableByteChannel, 0L, Long.MAX_VALUE);
                NoteBot.unzip(songFile, Spark.ParentPath);
                songFile.deleteOnExit();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            NoteBotCommand.INSTANCE.refresh();
        }).start();
    }

    public enum Sound {
        NONE,
        GOLD,
        GLASS,
        BONE,
        WOOD,
        CLAY,
        ICE,
        SAND,
        ROCK,
        WOOL
    }

    public interface IRegister {
    }

    public static class SoundRegister
            implements IRegister {
        private final Sound sound;
        private final byte soundByte;

        public SoundRegister(Sound soundIn, byte soundByteIn) {
            sound = soundIn;
            soundByte = soundByteIn;
        }

        public Sound getSound() {
            return sound;
        }

        public byte getSoundByte() {
            return soundByte;
        }

        public boolean equals(Object other) {
            if (other instanceof SoundRegister) {
                SoundRegister soundRegister = (SoundRegister) other;
                return soundRegister.getSound() == getSound() && soundRegister.getSoundByte() == getSoundByte();
            }
            return false;
        }
    }

    public static class SimpleRegister
            implements IRegister {
        private int sound;

        public SimpleRegister(int soundIn) {
            sound = soundIn;
        }

        public int getSound() {
            return sound;
        }

        public void setSound(int sound) {
            sound = sound;
        }
    }

    public static class SoundEntry {
        private final BlockPos pos;
        private final SoundRegister register;

        public SoundEntry(BlockPos posIn, SoundRegister soundRegisterIn) {
            pos = posIn;
            register = soundRegisterIn;
        }

        public BlockPos getPos() {
            return pos;
        }

        public SoundRegister getRegister() {
            return register;
        }
    }
}


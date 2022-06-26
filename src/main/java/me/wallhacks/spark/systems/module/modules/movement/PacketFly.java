package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.*;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.objects.Pair;
import net.minecraft.entity.MoverType;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@Module.Registration(name = "PacketFly", description = "made by wallhacks_ without any help at all")
public class PacketFly extends Module {
    private final List<Packet> allowedPackets = new ArrayList<>();
    private final Map<Integer, Pair<Long, Vec3d>> expectedPositions = new HashMap<>();
    ModeSetting mode = new ModeSetting("Mode", this, "Fast", Arrays.asList("Fast", "Jitter", "SetBack"));
    SettingGroup jitterG = new SettingGroup("Jitter", this).setVisible(v -> mode.is("Jitter"));
    ModeSetting jitter = new ModeSetting("Jitter", jitterG, "Adaptive", Arrays.asList("Adaptive", "Fixed"));
    IntSetting rate = new IntSetting("Rate", jitterG, 10, 1, 200, v -> mode.is("Jitter"));
    DoubleSetting lowFactor = new DoubleSetting("LowFactor", jitterG, 0.05D, -10.0D, 10.0D);
    DoubleSetting highFactor = new DoubleSetting("HighFactor", jitterG, 0.5D, 0.0D, 10.0D);
    ModeSetting type = new ModeSetting("Type", this, "Up", Arrays.asList("Up", "Down", "Random"));
    BooleanSetting overSend = new BooleanSetting("OverSend", this, true, v -> mode.is("SetBack"));
    DoubleSetting factor = new DoubleSetting("Factor", this, 1D, 0.1D, 10D, v -> !mode.is("SetBack"));
    BooleanSetting antiKick = new BooleanSetting("AntiKick", this, true);
    IntSetting antiKickTicks = new IntSetting("AntiKickTicks", this, 20, 1, 80, v -> antiKick.getValue());
    ModeSetting phase = new ModeSetting("Phase", this, "Stupid", Arrays.asList("Stupid", "Full", "None"));
    DoubleSetting phaseSpeed = new DoubleSetting("PhaseSpeed", this, 0.5D, 0.1D, 1D);
    BooleanSetting aefLimit = new BooleanSetting("AEFLimit", this, false);
    DoubleSetting aefFactor = new DoubleSetting("AEFFactor", this, 0.5D, 2.0D, 0.05D, v -> !mode.is("SetBack") && aefLimit.getValue());
    BooleanSetting tpsSync = new BooleanSetting("TPSSync", this, true);

    private float invalidated;
    private int currentFactor;
    private float extraFactor;
    private int rateTicks;
    private int skippedTicks;
    private long waitingSince;
    private boolean expectedConfirm;
    private float antiKickTick;
    private boolean expectKick;
    private boolean serverSprinting;
    private boolean serverSneaking;


    @Override
    public void onEnable() {
        allowedPackets.clear();
        expectedPositions.clear();

        invalidated = 0.0f;

        currentFactor = 1;
        extraFactor = 0.0f;
        rateTicks = 0;
        skippedTicks = 0;

        waitingSince = System.currentTimeMillis();
        expectedConfirm = false;

        antiKickTick = 0.0f;
        expectKick = true;

        serverSprinting = mc.player != null && mc.player.isSprinting();
        serverSneaking = mc.player != null && mc.player.isSneaking();

        if (mc.player != null) doMoveUpdate();
    }

    @Override
    public void onDisable() {
        if (mc.player != null) doMoveUpdate();
    }

    @SubscribeEvent
    public void onEntityAdded(EntityAddEvent event) {
        if (mc.player != null && mc.player.equals(event.getEntity())) {
            allowedPackets.clear();
            expectedPositions.clear();

            currentFactor = 1;
            extraFactor = 0.0f;
            rateTicks = 0;
            skippedTicks = 0;

            waitingSince = System.currentTimeMillis();
            expectedConfirm = false;

            serverSprinting = mc.player.isSprinting();
            serverSneaking = mc.player.isSneaking();

            doMoveUpdate();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketConfirmTeleport) {
            if (!allowedPackets.contains(event.getPacket())) event.setCanceled(true);

        } else if (event.getPacket() instanceof CPacketEntityAction) {
            CPacketEntityAction packet = event.getPacket();

            switch (packet.getAction()) {
                case START_SNEAKING: {
                    serverSneaking = true;
                    break;
                }
                case STOP_SNEAKING: {
                    serverSneaking = false;
                    break;
                }
                case START_SPRINTING: {
                    serverSprinting = true;
                    break;
                }
                case STOP_SPRINTING: {
                    serverSprinting = false;
                    break;
                }
            }
        }

        allowedPackets.remove(event.getPacket());
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = event.getPacket();

            synchronized (this) {
                switch (mode.getValue()) {
                    case "SetBack": {
                        safeSendPacket(new CPacketConfirmTeleport(packet.teleportId));
                        waitingSince = System.currentTimeMillis();
                        expectedConfirm = false;
                        break;
                    }
                    case "Fast":
                    case "Jitter": {
                        Pair<Long, Vec3d> expected = expectedPositions.get(packet.getTeleportId());
                        if (expected != null && expected.getValue().equals(new Vec3d(packet.x, packet.y, packet.z))) {
                            packet.x = mc.player.posX;
                            packet.y = mc.player.posY;
                            packet.z = mc.player.posZ;
                            expectedPositions.remove(packet.getTeleportId());
                        } else {
                            safeSendPacket(new CPacketConfirmTeleport(packet.getTeleportId()));
                            expectedPositions.clear();
                            ++invalidated;
                        }
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(PlayerUpdateEvent event) {
        synchronized (this) {
            if (System.currentTimeMillis() - waitingSince >= 30000) { // Packet was dropped or something?
                waitingSince = System.currentTimeMillis();
                expectedConfirm = false;
            }

            for (Map.Entry<Integer, Pair<Long, Vec3d>> entry : new ArrayList<>(expectedPositions.entrySet())) {
                if (System.currentTimeMillis() - entry.getValue().getKey() >= 30000)
                    expectedPositions.remove(entry.getKey());
            }
        }

        switch (mode.getValue()) {
            case "SetBack": {
                currentFactor = 1;
                break;
            }
            case "Jitter": {
                float lowFactor = Math.min(this.lowFactor.getFloatValue(), this.highFactor.getFloatValue());
                float highFactor = Math.max(this.highFactor.getFloatValue(), this.lowFactor.getFloatValue());
                float rate = tickAdjust(this.rate.getValue());

                switch (jitter.getValue()) {
                    case "Adaptive": {
                        rateTicks -= invalidated * 2.0f;
                    }
                    case "Fixed": {
                        extraFactor += lowFactor + highFactor * Math.max(0, rateTicks) / rate;
                        break;
                    }
                }

                if (++rateTicks > rate) rateTicks = (int) (-rate / 2);
            }
            case "Fast": {
                float factor = this.factor.getFloatValue();
                if (aefLimit.getValue() && AntiCheatConfig.getInstance().isPacketFlyLimited())
                    factor *= aefFactor.getFloatValue();
                currentFactor = (int) Math.floor(factor);
                extraFactor += factor % 1;

                while (extraFactor >= 1.0f) {
                    --extraFactor;
                    ++currentFactor;
                }
                break;
            }
        }

        extraFactor = MathHelper.clamp(extraFactor, -1.0f, 20.0f);

        if (mc.player != null) invalidated = Math.max(0, invalidated - tickAdjust(0.1f));
    }

    @SubscribeEvent
    public void onTravel(PlayerTravelEvent event) {
        event.setCanceled(true);

        ++skippedTicks;
        while (currentFactor >= 1) { //this is a totally fine place to send these packets nothing could go possibly wrong here
            doTravel();
            doMoveUpdate();
            --currentFactor;
            skippedTicks = 0;
        }

        Vec3d pos = mc.player.getPositionVector();
        Vec3d prevPos = new Vec3d(mc.player.prevPosX, mc.player.prevPosY, mc.player.prevPosZ);

        mc.player.prevLimbSwingAmount = mc.player.limbSwingAmount;
        double deltaX = pos.x - prevPos.x;
        double deltaZ = pos.z - prevPos.z;
        float absDelta = (float) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 4.0f;

        if (absDelta > 1.0) absDelta = 1.0f;

        mc.player.limbSwingAmount = mc.player.limbSwingAmount + (absDelta - mc.player.limbSwingAmount) * 0.4f;
        mc.player.limbSwing = mc.player.limbSwing + mc.player.limbSwingAmount;
    }

    @SubscribeEvent
    public void onMoveUpdate(PlayerMoveEvent event) {
        event.setCanceled(true);
    }

    private void doTravel() {
        Vec3d motion = new Vec3d(0, 0, 0);

        float hSpeed = 0.2873F;
        float vSpeed = 0.062F;

        if (isInBlocks(mc.player.boundingBox)) {
            hSpeed*=phaseSpeed.getFloatValue();
        }

        PotionEffect speedEffect = mc.player.getActivePotionEffect(MobEffects.SPEED);
        PotionEffect slownessEffect = mc.player.getActivePotionEffect(MobEffects.SLOWNESS);

        if (mc.player.isPotionActive(MobEffects.SPEED) && speedEffect != null)
            hSpeed *= 1.0f + 0.2f * (speedEffect.getAmplifier() + 1.0f);
        if (mc.player.isPotionActive(MobEffects.SLOWNESS) && slownessEffect != null)
            hSpeed *= 1.0f + -0.15f * (slownessEffect.getAmplifier() + 1.0f);


        float yaw = mc.player.rotationYaw;

        double forward = mc.player.moveForward;
        double strafe = mc.player.moveStrafing;

        if (mc.player.movementInput.jump) {
            motion = motion.add(new Vec3d(0, vSpeed, 0));
            hSpeed = 0.007f;
        } else if (mc.player.movementInput.sneak) {
            motion = motion.subtract(new Vec3d(0, vSpeed, 0));
            hSpeed /= 2.0f;
        }

        if (forward != 0.0 || strafe != 0.0) {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                forward = ((forward > 0.0) ? 1.0 : -1.0);
            }

            double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            double sin = Math.sin(Math.toRadians(yaw + 90.0f));

            motion = motion.add(new Vec3d(
                    forward * hSpeed * cos + strafe * hSpeed * sin,
                    0,
                    forward * hSpeed * sin - strafe * hSpeed * cos
            ));
        }

        if ((mode.is("SetBack") && expectedConfirm) || (aefLimit.getValue() && AntiCheatConfig.getInstance().isPacketFlyLimited()))
            motion = new Vec3d(0, 0, 0);

        switch (phase.getValue()) {
            case "Full": {
                mc.player.noClip = true;
                break;
            }
            case "Stupid": {
                // Stupid fix for sneaking in 1.12.2
                mc.player.noClip = !mc.player.onGround && mc.player.collidedVertically && isInBlocks(mc.player.getEntityBoundingBox().offset(0, 0.0625, 0));
                break;
            }
        }
        mc.player.motionX = motion.x;
        mc.player.motionY = motion.y;
        mc.player.motionZ = motion.z;
        mc.player.move(MoverType.SELF, motion.x, motion.y, motion.z);
    }

    private double rand() {
        Random random = new Random();
        return 10000 - random.nextInt(20000);
    }

    private void doMoveUpdate() {
        if (mc.player.isSprinting() != serverSprinting)
            safeSendPacket(new CPacketEntityAction(mc.player, mc.player.isSprinting() ? CPacketEntityAction.Action.START_SPRINTING : CPacketEntityAction.Action.STOP_SPRINTING));

        if (mc.player.isSneaking() != serverSneaking)
            safeSendPacket(new CPacketEntityAction(mc.player, mc.player.isSneaking() ? CPacketEntityAction.Action.START_SNEAKING : CPacketEntityAction.Action.STOP_SNEAKING));

        Vec3d positionBefore = new Vec3d(mc.player.prevPosX, mc.player.prevPosY, mc.player.prevPosZ);
        Vec3d positionAfter = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);

        // If we are about to be timed out by AEF force the antikick so we can stay floating without getting kicked
        // FIXME: Are we about to be kicked? This just tells us if we have been limited
        //fixed already by wallhacks_
        //(fixed not skidded)
        boolean forceAntiKick = !AntiCheatConfig.getInstance().wasPacketFlyLimited() && AntiCheatConfig.getInstance().isPacketFlyLimited();

        if ((antiKickTick > antiKickTicks.getValue() || forceAntiKick) && !expectedConfirm) {
            antiKickTick = 0.0f;

            if (!mc.player.capabilities.allowFlying && (!mc.player.isPotionActive(MobEffects.LEVITATION)) && !isInBlocks(mc.player.boundingBox.grow(0.0625).expand(0.0, -0.55, 0.0))) {
                if (antiKick.getValue()) {
                    if (positionAfter.subtract(positionBefore).y >= -0.03125)
                        mc.player.setPosition(mc.player.posX, mc.player.prevPosY - 0.0313, mc.player.posZ);
                }
            }
        }

        positionAfter = mc.player.getPositionVector();

        Vec3d spoof = new Vec3d(0, 0, 0);

        switch (type.getValue()) {
            case "Up":
                spoof = new Vec3d(mc.player.posX, mc.player.posY + 20000, mc.player.posZ);
                break;
            case "Down":
                spoof = new Vec3d(mc.player.posX, mc.player.posY - 20000, mc.player.posZ);
                break;
            case "Random":
                spoof = new Vec3d(mc.player.posX + rand(), mc.player.posY, mc.player.posZ + rand());
                break;
        }

        if (positionBefore.equals(positionAfter)) {
            // If we're expecting to be kicked increment the antikick counter, since we aren't sending packets once we
            // have moved down we won't be kicked (until we start sending packets again)
            if (expectKick) antiKickTick += tickAdjust(skippedTicks);
            // Invalid move packets shouldn't mess with the floating boolean so sending this is a safe bet in case the
            // server dropped our packet
            if (expectedConfirm) safeSendPacket(new CPacketPlayer.Position(spoof.x, spoof.y, spoof.z, false));
            return; // If we haven't moved just return since everything after this is spoofing
        }

        antiKickTick += tickAdjust(skippedTicks);
        expectKick = positionAfter.subtract(positionBefore).y >= -0.03125;

        // TODO: Rotation + on ground spoofing
        safeSendPacket(new CPacketPlayer.Position(positionAfter.x, positionAfter.y, positionAfter.z, false));
        safeSendPacket(new CPacketPlayer.Position(spoof.x, spoof.y, spoof.z, false));

        switch (mode.getValue()) {
            case "SetBack": {
                synchronized (this) {
                    if (!expectedConfirm || overSend.getValue()) {
                        // safeSendPacket(new WCPacketConfirmTeleport(spleefNet.positionHandler.getLastGoodTeleportID()));
                        waitingSince = System.currentTimeMillis();
                        expectedConfirm = true;
                    }
                }
                break;
            }
            case "Fast":
            case "Jitter": {
                int teleportID = Spark.positionManager.teleportId + 1;
                safeSendPacket(new CPacketConfirmTeleport(teleportID));
                synchronized (this) {
                    expectedPositions.put(teleportID, new Pair<>(System.currentTimeMillis(), positionAfter));
                }
                break;
            }
        }
    }

    private void safeSendPacket(Packet packet) {
        if (nullCheck()) return;
        // Not necessary not to send packets but it's good practice in case we guess the tickrate wrong
        if (packet instanceof CPacketConfirmTeleport && aefLimit.getValue() && AntiCheatConfig.getInstance().isPacketFlyLimited())
            return;

        allowedPackets.add(packet);

        mc.player.connection.sendPacket(packet);
    }

    private float tickAdjust(float ticks) {
        return ticks * (tpsSync.getValue() ? Spark.tickManager.getTickRate() / 20 : 1.0f);
    }

    private boolean isInBlocks(AxisAlignedBB bb) {
        return !mc.world.getCollisionBoxes(null, bb).isEmpty();
    }

}

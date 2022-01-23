package me.wallhacks.spark.util.player;

public class RotationTask {
    public final int rotationStep;
    public final int stayTicks;
    public final boolean allowSendMultiplePacket;

    public RotationTask (int rotationStep,int stayTicks, boolean allowSendMultiplePacket)
    {
        this.allowSendMultiplePacket = allowSendMultiplePacket;
        this.stayTicks = stayTicks;
        this.rotationStep = rotationStep;
    }
    public RotationTask (int rotationStep,int stayTicks)
    {
        this(rotationStep,stayTicks,false);
    }
}

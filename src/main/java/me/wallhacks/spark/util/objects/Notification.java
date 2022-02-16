package me.wallhacks.spark.util.objects;

public class Notification {
    public Timer timer;
    public String text;
    public final int id;
    public int animateX = 0;
    public int stage;
    public int offset;
    public boolean didOffset;
    public Timer animateTimer = new Timer();
    public Notification(String text,int id) {
        animateTimer.reset();
        timer = new Timer();
        didOffset = false;
        this.id = id;
        timer.reset();
        stage = 0;
        this.text = text;
    }
    public Notification(String text)
    {
        this(text,-1);
    }
}

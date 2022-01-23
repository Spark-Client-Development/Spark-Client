package me.wallhacks.spark.util.objects;

public class Notification {
    public Timer timer;
    public String text;
    public int animateX = 0;
    public int stage;
    public int offset;
    public boolean didOffset;
    public Timer animateTimer = new Timer();
    public Notification(String text) {
        animateTimer.reset();
        timer = new Timer();
        didOffset = false;
        timer.reset();
        stage = 0;
        this.text = text;
    }
}

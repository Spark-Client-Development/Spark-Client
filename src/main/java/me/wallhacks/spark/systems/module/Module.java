package me.wallhacks.spark.systems.module;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.hud.huds.Notifications;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.Notification;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public abstract class Module extends SettingsHolder implements MC {


    private final String name = getMod().name();
    private final String description = getMod().description();
    private int bind = getMod().bind();
    private boolean hold = getMod().hold();
    private boolean visible = getMod().visible();
    private boolean muted = getMod().muted();
    private Notification not;
    private Category category;
    private boolean isEnabled = getMod().enabled();
    private boolean alwaysListening = getMod().alwaysListening();

    public Module() {
        super();
    }

    public Registration getMod() {
        return getClass().getAnnotation(Registration.class);
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled) {
        if (enabled)
            enable();
        else
            disable();
    }

    @Override
    public String getName() {
        return name;
    }

    public int getBind() {
        return bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isHold() {
        return this.hold;
    }

    public void setHold(boolean hold) {
        this.hold = hold;
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void enable() {
        if (!this.isEnabled) {

            if (!getIsAlwaysListening())
                Spark.eventBus.register(this);

            Spark.logger.info("Enabled " + getName());

            this.isEnabled = true;
            onEnable();
            if (isEnabled)
                if (Notifications.INSTANCE.toggle.getValue() && !muted) {
                    if (not == null || !Notifications.notifications.contains(not)) {
                        not = new Notification(name + " " + TextFormatting.GREEN + "enabled");
                        Notifications.addNotification(not);
                    } else {
                        not.text = name + " " + TextFormatting.GREEN + "enabled";
                        not.timer.reset();
                        not.stage = 2;
                    }
                }

        }
    }

    public void disable() {
        if (this.isEnabled) {
            if (!getIsAlwaysListening())
                Spark.eventBus.unregister(this);

            Spark.logger.info("Disabled " + getName());

            this.isEnabled = false;
            onDisable();
            if (!isEnabled)
                if (Notifications.INSTANCE.toggle.getValue() && !muted) {
                    if (not == null || !Notifications.notifications.contains(not)) {
                        not = new Notification(name + " " + TextFormatting.RED + "disabled");
                        Notifications.addNotification(not);
                    } else {
                        not.text = name + " " + TextFormatting.RED + "disabled";
                        not.timer.reset();
                        not.stage = 2;
                    }
                }
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public boolean getIsAlwaysListening() {
        return alwaysListening;
    }

    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        RENDER("Render"),
        EXPLOIT("Exploit"),
        WORLD("World"),
        PLAYER("Player"),
        MICS("Mics");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Registration {
        String name();

        String description();

        int bind() default Keyboard.KEY_NONE;

        boolean enabled() default false;

        boolean hold() default false;

        boolean visible() default true;

        boolean muted() default false;

        boolean alwaysListening() default false;
    }


}

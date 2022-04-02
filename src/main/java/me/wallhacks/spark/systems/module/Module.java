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

    private final String name = this.getMod().name(), description = this.getMod().description();
    private int bind = this.getMod().bind();
    private boolean hold = this.getMod().hold(), visible = this.getMod().visible(), muted = this.getMod().muted(), enabled = this.getMod().enabled(), this.getMod().alwaysListening();
    private Notification not;
    private Category category;
    
    protected Module() {
        if (this.getIsAlwaysListening()) Spark.eventBus.register(this);
    }
        
    public Registration getMod() {
        return this.getClass().getAnnotation(Registration.class);
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled)
            this.enable();
        else
            this.disable();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getBind() {
        return this.bind;
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
        return this.muted;
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
        this.setEnabled(!isEnabled());
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void enable() {
        if (!this.isEnabled) {

            if (!this.getIsAlwaysListening()) Spark.eventBus.register(this);

            Spark.logger.info("Enabled " + getName());

            this.isEnabled = true;
            this.onEnable();
            
            if (Notifications.INSTANCE.toggle.getValue() && isEnabled && !this.muted) {
                Notifications.addNotification(new Notification("$name${TextFormatting.GREEN} enabled",this));
            }
        }
    }

    public void disable() {
        if (this.isEnabled) {
            if (!this.getIsAlwaysListening()) Spark.eventBus.unregister(this);

            Spark.logger.info("Disabled " + getName());

            this.isEnabled = false;
            this.onDisable();
            
            if (Notifications.INSTANCE.toggle.getValue() && !isEnabled && !this.muted) {
                Notifications.addNotification(new Notification(name + " " + TextFormatting.RED + "disabled",this));
            }
        }
    }

    public void onEnable() {}

    public void onDisable() {}

    public boolean getIsAlwaysListening() {
        return this.alwaysListening;
    }

    public static enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        RENDER("Render"),
        EXPLOIT("Exploit"),
        WORLD("World"),
        PLAYER("Player"),
        MISC("Misc");

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
    public static @interface Registration {
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

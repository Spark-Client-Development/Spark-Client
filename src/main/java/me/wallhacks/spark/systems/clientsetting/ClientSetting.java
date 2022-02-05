package me.wallhacks.spark.systems.clientsetting;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.util.MC;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public abstract class ClientSetting extends SettingsHolder implements MC {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Registration {
        String name();
        String description();
        boolean safe() default true;
    }


    public ClientSetting(){
        super();
    }


    public Registration getMod(){
        return getClass().getAnnotation(Registration.class);
    }



    private final String name = getMod().name();
    private final String description = getMod().description();
    private final boolean safe = getMod().safe();

    @Override
    public String getName() {
        return name;
    }


}

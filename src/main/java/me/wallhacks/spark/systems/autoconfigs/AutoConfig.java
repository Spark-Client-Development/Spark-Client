package me.wallhacks.spark.systems.autoconfigs;

import com.google.common.collect.Lists;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.AutoConfigManager;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.systems.command.Command;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AutoConfig {

    String name;

    public AutoConfig(String name) {
        this.name = name;

        AutoConfigManager.configs.add(this);
        AutoConfigManager.confignames.add(name);
    }

    public String getName() {
        return name;
    }


    public void config() {

    }




}

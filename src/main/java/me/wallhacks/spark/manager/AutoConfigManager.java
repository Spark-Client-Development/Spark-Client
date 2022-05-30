package me.wallhacks.spark.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.systems.autoconfigs.AutoConfig;
import me.wallhacks.spark.systems.autoconfigs.configs.BaseSetup;
import me.wallhacks.spark.systems.autoconfigs.configs.CCpvp;
import me.wallhacks.spark.systems.autoconfigs.configs.Const;
import me.wallhacks.spark.systems.autoconfigs.configs.MakeCAagro;
import me.wallhacks.spark.systems.command.Command;
import me.wallhacks.spark.systems.command.commands.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AutoConfigManager {

	public static final ArrayList<AutoConfig> configs = new ArrayList<>();

	public static final ArrayList<String> confignames = new ArrayList<>();


	public AutoConfigManager() {
		new Const();
		new CCpvp();
		new MakeCAagro();

		new BaseSetup();
	}



}

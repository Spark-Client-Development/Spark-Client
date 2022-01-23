package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.systems.module.Module;
import org.codehaus.plexus.util.StringUtils;
import org.lwjgl.input.Keyboard;

import me.wallhacks.spark.systems.command.Command;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.KeySetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.systems.setting.settings.Toggleable;

public class ModuleCommand extends Command {

	Module module;
	
	@Override
	public void init() {
	}
	
	public ModuleCommand(Module module) {
		super();
		this.module = module;
		addCommandByName(getName());
		addOption("toggle", arg -> { 
			module.toggle(); 
			Spark.sendInfo(""+ CommandManager.COLOR2+""+module.getName()+ ""+CommandManager.COLOR1+" has been turned " + (module.isEnabled() ? "on" : CommandManager.ErrorColor+"off"));
		});
		if(!module.getSettings().isEmpty()) {
			for(Setting<?> setting : module.getSettings()) {
				if(setting instanceof Toggleable) {
					addOption(setting.getName().toLowerCase(), arg -> {
						((Toggleable)setting).toggle();
						Spark.sendInfo(""+CommandManager.COLOR2+""+module.getName() + " " + setting.getName() + ""+CommandManager.COLOR1+" has been turned " + (((Toggleable)setting).isOn() ? "on" : ""+CommandManager.ErrorColor+"off"));
					});
				}
				if(setting instanceof IntSetting) {
					addOption(setting.getName().toLowerCase(), arg -> {
						if(arg != null) {
							try {
								int i = Integer.parseInt(arg);
								((IntSetting) setting).setValue(i);
								Spark.sendInfo(""+CommandManager.COLOR2+""+module.getName() + " " + setting.getName() + ""+CommandManager.COLOR1+" has been set to "+CommandManager.COLOR2+"" + i);
							} catch(NumberFormatException e) {
								Spark.sendInfo(""+CommandManager.ErrorColor+"Invalid argument! Argument is not an integer!");
							}
						} else {
							noArgInfo();
						}
					}, "<integer>");
				}
				if(setting instanceof DoubleSetting) {
					addOption(setting.getName().toLowerCase(),arg -> {
						if(arg != null) {
							try {
								double i = Double.parseDouble(arg);
								((DoubleSetting) setting).setValue(i);
								Spark.sendInfo(""+CommandManager.COLOR2+""+module.getName() + " " + setting.getName() + ""+CommandManager.COLOR1+" has been set to "+CommandManager.COLOR2+"" + i);
							} catch(NumberFormatException e) {
								Spark.sendInfo(CommandManager.ErrorColor+"Invalid argument! Argument is not a number!");
							}
						} else {
							noArgInfo();
						}
					}, "<number>");
				}
				if(setting instanceof ModeSetting) {
					addOption(setting.getName().toLowerCase(), arg -> {
						if(arg != null) {
							arg = StringUtils.capitalise(arg);
							ModeSetting modesetting = (ModeSetting) setting;
							if(modesetting.setValueString(arg)) {
								Spark.sendInfo(CommandManager.COLOR2 +module.getName() + " " + setting.getName() + CommandManager.COLOR1+" has been set to "+CommandManager.COLOR2 + arg);
							} else {
								Spark.sendInfo(CommandManager.ErrorColor+"Invalid argument! Mode doesn't exist.");
							}
						} else {
							noArgInfo();
						}
					}, ((ModeSetting) setting).getModes().toArray(new String[0]));
				}
				if(setting instanceof KeySetting) {
					addOption(setting.getName().toLowerCase(), arg -> {
						if(arg != null) {
							if(arg.equals("none")) {
								((KeySetting) setting).setKey(0);
							} else {
								int keyCode = Keyboard.getKeyIndex(arg);
								if(keyCode != Keyboard.KEY_NONE) {
									((KeySetting) setting).setKey(keyCode);
								} else {
									Spark.sendInfo(""+CommandManager.ErrorColor+"Invalid argument! Key doesn't exist.");
								}
							}
							Spark.sendInfo(module.getName() + "'s " + setting.getName() + " setting has been set to "+CommandManager.COLOR2+"" + arg);
						} else {
							noArgInfo();
						}
					});
				}
			}
		}
	}

	@Override
	public String getName() {
		return module.getName().toLowerCase();
	}

}

package me.wallhacks.spark.systems.command;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.systems.command.Command.Option.OptionRunnable;

public abstract class Command {

	private final List<Option> OPTIONS = new ArrayList<>();
	
	public Command() {
		init();
	}
	
	public void init() {
		CommandManager.COMMANDSBYNAME.put(getName(), this);
	}
	
	public void addCommandByName(String name) {
		CommandManager.COMMANDSBYNAME.put(name, this);
	}
	
	public abstract String getName();
	public void run(String[] args) {
		handleArgs(args);
	}
	
	public Command addOption(String name, OptionRunnable runnable) {
		OPTIONS.add(new Option(name, new ArrayList<>(), runnable));
		return this;
	}
	
	public Command addOption(@Nullable String name, OptionRunnable runnable, String... usages) {
		OPTIONS.add(new Option(name, Lists.newArrayList(usages), runnable));
		return this;
	}
	
	public Command addOption(@Nullable String name, OptionRunnable runnable, List<String> usages) {
		OPTIONS.add(new Option(name, usages, runnable));
		return this;
	}
	
	public List<Option> getOptions() {
		return OPTIONS;
	}
	
	public void addUsage(String usage) {
		if(!CommandManager.COMMANDUSAGES.containsKey(getName())) {
			CommandManager.COMMANDUSAGES.put(getName(), new ArrayList<>());
		}
		CommandManager.COMMANDUSAGES.get(getName()).add(usage);
	}
	
	public void handleArgs(String[] args) {
		if(args.length >= 1) {
			Option defaultOption = null;
			for(Option option : OPTIONS) {
				if(args[0].equals(option.getName())) {
					if(args.length > 1) {
						option.run(args[1]);
						return;
					}
					option.run(null);
					return;
				}
				if(option.getName() == null) {
					defaultOption = option;
				}
			}
			if(defaultOption != null) {
				defaultOption.run(args[0]);
				return;
			}
		}
		noArgInfo();
	}
	
	public void noArgInfo() {
		Spark.sendError("No argument given.");
	}
	
	public static class Option {
		String name;
		List<String> usages;
		OptionRunnable runnable;
		
		public Option(String name, List<String> usages, OptionRunnable runnable) {
			this.name = name;
			this.usages = usages;
			this.runnable = runnable;
		}
		
		public String getName() {
			return name;
		}
		
		public void run(@Nullable String arg) {
			runnable.run(arg);
		}
		
		public static interface OptionRunnable {
			public void run(@Nullable String arg);
		}
		
		public List<String> getUsages() {
			return usages;
		}
	}
}

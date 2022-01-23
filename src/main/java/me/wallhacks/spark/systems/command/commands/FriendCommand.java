package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.systems.command.Command;

public class FriendCommand extends Command {

	public FriendCommand() {
		super();
		addOption("add", arg -> {
			if(arg != null){
				Spark.socialManager.addFriend(arg);
			}
		}, "<name>");

		addOption("remove", arg -> {
			if(arg != null){
				Spark.socialManager.removeFriend(arg);
			}
		}, "<name>");

		addOption("list", arg -> {

			if(Spark.socialManager.getFriends().size() == 0)
				Spark.sendInfo(""+ CommandManager.COLOR1+"You got no friends :(");
			else {
				Spark.sendInfo(""+CommandManager.COLOR1+"Your friends:");
				for (String f : Spark.socialManager.getFriendsNames()) {
					Spark.sendInfo(""+CommandManager.COLOR1+" - "+CommandManager.COLOR2+f);
				}
			}


		});

	}

	@Override
	public String getName() {
		return "friend";
	}

}

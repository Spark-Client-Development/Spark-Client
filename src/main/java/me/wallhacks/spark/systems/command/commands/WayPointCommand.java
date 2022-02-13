package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.manager.ConfigManager;
import me.wallhacks.spark.manager.WaypointManager;
import me.wallhacks.spark.systems.command.Command;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.Vec2d;
import me.wallhacks.spark.util.objects.Vec2i;

public class WayPointCommand extends Command implements MC {

	public WayPointCommand() {
		super();
		addOption("create", arg -> {
			if(arg != null){

				String[] args = arg.split(" ");




				if(Spark.waypointManager.createWayPoint(new Vec2i((int)mc.player.posX,(int)mc.player.posZ),mc.player.dimension,args[0]))
				{

					Spark.sendInfo(""+ CommandManager.COLOR1+"Waypoint "+CommandManager.COLOR2+arg+ ""+CommandManager.COLOR1+" has been created!");

				}
				else
					Spark.sendInfo(""+ CommandManager.ErrorColor+"Waypoint "+CommandManager.COLOR2+arg+CommandManager.ErrorColor+" already exists!");


			}
		}, "<configname>");


		addOption("delete", arg -> {
			if(arg != null){
				WaypointManager.Waypoint waypoint = Spark.waypointManager.getWayPoint(arg);

				if(waypoint != null)
				{
					Spark.waypointManager.getWayPoints().remove(waypoint);
					Spark.sendInfo(""+ CommandManager.COLOR1+"Waypoint "+CommandManager.COLOR2+arg+ ""+CommandManager.COLOR1+" has been deleted!");
				}
				else
					Spark.sendInfo(""+ CommandManager.ErrorColor+"Waypoint "+CommandManager.COLOR2+arg+ ""+CommandManager.ErrorColor+" can't be found!");

			}
		}, "<configname>");



	}

	@Override
	public String getName() {
		return "waypoint";
	}

}

package Minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Minigames.Games.RiverRace.RiverRaceCheckpoint;
import Minigames.Games.RiverRace.RiverRaceMap;

public class RRCheck implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED +"You are unable to add start points");
			return true;
		}
		if (!sender.hasPermission("Minigames.boatchecks"))
		{
			sender.sendMessage(ChatColor.RED +"You do not have permission to use this command");
			return true;
		}
		if (args.length == 0)
		{
			CheckHelp(sender);
		}
		
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("add"))
				sender.sendMessage(ChatColor.RED +"/rrcheck add [MapID] [Number]");
			
			else if (args[0].equalsIgnoreCase("delete"))
				sender.sendMessage(ChatColor.RED +"/rrcheck delete [CheckPointID]");
			
			else if (args[0].equalsIgnoreCase("list"))
				sender.sendMessage(ChatColor.RED +"/rrcheck list [MapID]");
			else
				CheckHelp(sender);
		}
		
		else if (args.length == 2)
		{
			if (args[0].equalsIgnoreCase("add"))
			{
				sender.sendMessage(ChatColor.RED +"/rrcheck add [MapID] [Number]");
			}
			//-----------------------------
			//-----Deleting Checkpoint-----
			//-----------------------------
			else if (args[0].equalsIgnoreCase("delete"))
			{
				if (args[1].matches("[0-9]+"))
				{
					if (RiverRaceCheckpoint.deleteCheckpoint(Integer.parseInt(args[2])))
						sender.sendMessage(ChatColor.GREEN +"Checkpoint was deleted");
					else
						sender.sendMessage(ChatColor.RED +"The checkpoint was unfortunetly not deleted from the DB. Please contact someone who can fix this.");
				}
				else
					sender.sendMessage(ChatColor.RED +"CheckPointID must be an integer");
			}
			//---------------------------------------
			//-----List of Checkpoint from a Map-----
			//---------------------------------------
			else if (args[0].equalsIgnoreCase("list"))
			{
				if (args[1].matches("[0-9]+"))
				{
					RiverRaceMap Map = new RiverRaceMap(Integer.parseInt(args[1]));
					Map.setMapFromMapID();
					
					RiverRaceCheckpoint[] Checkpoints = RiverRaceCheckpoint.getAllForMapID(Map.getMapID(), Map.getWorld());
					
					//Get details of each checkpoint
					for (int i = 0 ; i < Checkpoints.length ; i++)
					{
						sender.sendMessage(ChatColor.DARK_AQUA +"[Map Found]:");
						sender.sendMessage(ChatColor.AQUA +"CheckPointID: "+Checkpoints[i].getCheckpointID());
						sender.sendMessage(ChatColor.AQUA +"MapID: "+Map.getMapID());
						for (int j = 0 ; j < Checkpoints[i].getLocations().length ; j++)
						{
							sender.sendMessage(ChatColor.AQUA +"Pass Location: " +Map.getMapID() +"(" +Checkpoints[i].getLocations()[j].getBlockX() +Checkpoints[i].getLocations()[j].getBlockY() +Checkpoints[i].getLocations()[j].getBlockZ() +")");
						}
						sender.sendMessage("");
					}
					return true;
				}
				else
					sender.sendMessage(ChatColor.RED +"MapID must be an integer");
			}
			else
				CheckHelp(sender);
		}
		else if (args.length == 3)
		{
			if (args[0].equalsIgnoreCase("add"))
			{
				//sender.sendMessage(ChatColor.RED +"/rrcheck add [MapID] [Number]");
				if (!args[1].matches("[0-9]+"))
				{
					sender.sendMessage(ChatColor.RED +"MapID must be an integer");
					return true;
				}
				if (!args[2].matches("[0-9]+"))
				{
					sender.sendMessage(ChatColor.RED +"Number must be an integer");
					return true;
				}
				RiverRaceCheckpoint newCheckpoint = new RiverRaceCheckpoint(Integer.parseInt(args[1]), Integer.parseInt(a//rgs[2]));
				if (newStartGrid.addGrid())
				{
					sender.sendMessage(ChatColor.GREEN +"The grid has been added to the DB");
				}
				else
				{
					sender.sendMessage(ChatColor.RED +"The grid was unfortunetly not added to the DB. Please contact someone who can fix this.");
				}
			}
			else if (args[0].equalsIgnoreCase("delete"))
				sender.sendMessage(ChatColor.RED +"/rrcheck delete [CheckPointID]");
			
			else if (args[0].equalsIgnoreCase("list"))
				sender.sendMessage(ChatColor.RED +"/rrcheck list [MapID]");
			else
				CheckHelp(sender);
		}
		else
			CheckHelp(sender);
		return true;
	}
	
	private void CheckHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.DARK_AQUA +"---------------");
		sender.sendMessage(ChatColor.DARK_AQUA +"/rrcheck Help:");
		sender.sendMessage(ChatColor.AQUA +"/rrcheck add");
		sender.sendMessage(ChatColor.AQUA +"/rrcheck delete");
		sender.sendMessage(ChatColor.AQUA +"/rrcheck list");
	}
}

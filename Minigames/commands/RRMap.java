package Minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Minigames.Games.RiverRace.RiverRaceMap;

public class RRMap implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		//Check is command sender is a player
		if (!(sender instanceof Player))
		{
			sender.sendMessage("&cYou are unable to add a map!");
			return true;
		}
		
		Player player = (Player) sender;
		
		//Player doesnt have the correct permissions
		if (!sender.hasPermission("Minigames.RiverRaceMaps"))
		{
			sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
			return true;
		}
		
		if (args.length == 0)
		{
			maphelp(sender);
			return true;
		}
		else if (args.length == 2)
		{
			//Add help
			if (args[0].equalsIgnoreCase("add"))
			{
				if (!sender.hasPermission("Minigames.RiverRaceMaps.addmaps"))
				{
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
					return true;
				}
				//---------------------
				//----Add functions----
				//---------------------
				RiverRaceMap newMap = new RiverRaceMap(args[1], player.getWorld().getName());
				if (newMap.addMap())
					sender.sendMessage(ChatColor.GREEN +"The map has been added to the DB");
				else
					sender.sendMessage(ChatColor.RED +"The map was unfortunetly not added to the DB. Please contact someone who can fix this.");
				return true;
			}
			else if (args[0].equalsIgnoreCase("delete"))
			{
				if (!sender.hasPermission("Minigames.RiverRaceMaps.deletemaps"))
				{
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
					return true;
				}
				//--------------------
				//-----Delete Map-----
				//--------------------
				RiverRaceMap oldMap = new RiverRaceMap(args[1]);
				oldMap.setMapIDFromName();
				int iDeleted = oldMap.deleteMap();
				
				if (iDeleted == -1)
					sender.sendMessage(ChatColor.RED +"An error has occured. The DB command was not successful. Please contact someone who can fix this.");
				
				else if (iDeleted == 1)
					sender.sendMessage(ChatColor.GREEN +"1 map have been removed from the DB");
				
				else
					sender.sendMessage(ChatColor.GREEN +""+iDeleted +" maps have been removed from the DB");
				
				return true;
			}
			else
			{
				maphelp(sender);
				return true;
			}
		}
		
		//------------------
		//--List Functions--
		//------------------
		else if (args[0].equalsIgnoreCase("list"))
		{
			if (!sender.hasPermission("Minigames.RiverRaceMaps.list"))
			{
				sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
				return true;
			}
			//Get a list of mapIDs
			int[] mapIDs = RiverRaceMap.MapIDs();
			
			//Initiate Map array with length of the amount of MapIDs just found
			RiverRaceMap[] Maps = new RiverRaceMap[mapIDs.length];
			
			if (Maps.length == 0)
			{
				sender.sendMessage(ChatColor.GREEN +"No maps found");
				return true;
			}
			
			//Get details of each map
			for (int i = 0 ; i < mapIDs.length ; i++)
			{
				Maps[i] = new RiverRaceMap(mapIDs[i]);
				Maps[i].setMapFromMapID();
				player.sendMessage(ChatColor.DARK_AQUA +"[Map Found]:");
				player.sendMessage(ChatColor.AQUA +"MapID: "+Maps[i].getMapID());
				player.sendMessage(ChatColor.AQUA +"Name: "+Maps[i].getLocation());
				player.sendMessage(ChatColor.AQUA +"World: "+Maps[i].getWorld().getName());
			}
			return true;
		}
		
		else
		{
			if (args[0].equalsIgnoreCase("add"))
			{
				if (!sender.hasPermission("Minigames.RiverRaceMaps.addmaps"))
				{
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
					return true;
				}
				sender.sendMessage(ChatColor.RED +"/rrmap add [Location]");
				return true;
			}
			else if (args[0].equalsIgnoreCase("delete"))
			{
				//Delete help
				if (!sender.hasPermission("Minigames.RiverRaceMaps.deletemaps"))
				{
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
					return true;
				}
				sender.sendMessage(ChatColor.RED +"/rrmap delete [Location]");
				return true;
			}
			else
			{
				maphelp(sender);
				return true;
			}
		}
	}

	private void maphelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.DARK_AQUA +"---------------");
		sender.sendMessage(ChatColor.DARK_AQUA +"/rrmap Help:");
		sender.sendMessage(ChatColor.AQUA +"/rrmap delete");
		sender.sendMessage(ChatColor.AQUA +"/rrmap add");
		sender.sendMessage(ChatColor.AQUA +"/rrmap list");
	}
}

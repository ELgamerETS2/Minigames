package Minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Minigames.Games.RiverRace.RiverRaceStartGrid;

public class RRGrid implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		//Check is command sender is a player
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED +"You are unable to add start points");
			return true;
		}
		if (!sender.hasPermission("Minigames.boatgrids"))
		{
			sender.sendMessage(ChatColor.RED +"You do not have permission to use this command");
			return true;
		}
		
		if (args.length == 0)
		{
			GridHelp(sender);
		}
		
		else if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("add"))
				sender.sendMessage(ChatColor.RED +"/rrgrid add [MapID] [Direction (Right/Left)] [PositionFromCentre]");
			
			else if (args[0].equalsIgnoreCase("delete"))
				sender.sendMessage(ChatColor.RED +"/rrgrid delete [GridID]");
			
			else if (args[0].equalsIgnoreCase("list"))
				sender.sendMessage(ChatColor.RED +"/rrgrid list [MapID]");
			else
				GridHelp(sender);
		}
		
		else if (args.length == 2)
		{
			if (args[0].equalsIgnoreCase("add"))
			{
				sender.sendMessage(ChatColor.RED +"/rrgrid add [MapID] [Right/Left] [PositionFromCentre]");
			}
			else if (args[0].equalsIgnoreCase("delete"))
			{
				if (args[1].matches("[0-9]+"))
					RiverRaceStartGrid.delete(Integer.parseInt(args[1]));
				else
					sender.sendMessage(ChatColor.RED +"GridID must be an integer");
			}
			else if (args[0].equalsIgnoreCase("list"))
			{
				if (args[1].matches("[0-9]+"))
				{
					RiverRaceStartGrid[] Maps = RiverRaceStartGrid.allStartGrids(Integer.parseInt(args[1]));
					
					//Get details of each map
					for (int i = 0 ; i < Maps.length ; i++)
					{
						sender.sendMessage(ChatColor.DARK_AQUA +"[Start Grid Found]:");
						sender.sendMessage(ChatColor.AQUA +"MapID: "+Maps[i].getMapID());
						sender.sendMessage(ChatColor.AQUA +"GridID: "+Maps[i].getGridID());
						if (Maps[i].getLeft())
							sender.sendMessage(ChatColor.AQUA +"Direction: Left");
						else
							sender.sendMessage(ChatColor.AQUA +"Direction: Right");
						sender.sendMessage(ChatColor.AQUA +"Position from centre: "+Maps[i].getPositionFromCentre());
						sender.sendMessage(ChatColor.AQUA +"Coordinates: ("+Maps[i].getX()+", "+Maps[i].getY() +", "+Maps[i].getZ()+")");
					}
					return true;
				}
				else
					sender.sendMessage(ChatColor.RED +"MapID must be an integer");
			}
			else
				GridHelp(sender);
		}		
		else if (args.length == 4)
		{
			if (args[0].equalsIgnoreCase("add"))
			{
				if (!args[1].matches("[0-9]+"))
				{
					sender.sendMessage(ChatColor.RED +"MapID must be an integer");
					return true;
				}
				if (!args[3].matches("[0-9]+"))
				{
					sender.sendMessage(ChatColor.RED +"Position from centre must be an integer");
					return true;
				}
				if (args[2].equalsIgnoreCase("right"))
				{
					RiverRaceStartGrid newStartGrid = new RiverRaceStartGrid(Integer.parseInt(args[1]), false, Integer.parseInt(args[3]), ((Player) sender).getLocation());
					if (newStartGrid.addGrid())
					{
						sender.sendMessage(ChatColor.GREEN +"The grid has been added to the DB");
					}
					else
					{
						sender.sendMessage(ChatColor.RED +"The grid was unfortunetly not added to the DB. Please contact someone who can fix this.");
					}
					return true;
				}
				else if (args[2].equalsIgnoreCase("left"))
				{
					RiverRaceStartGrid newStartGrid = new RiverRaceStartGrid(Integer.parseInt(args[1]), true, Integer.parseInt(args[3]), ((Player) sender).getLocation());
					if (newStartGrid.addGrid())
					{
						sender.sendMessage(ChatColor.GREEN +"The grid has been added to the DB");
					}
					else
					{
						sender.sendMessage(ChatColor.RED +"The grid was unfortunetly not added to the DB. Please contact someone who can fix this.");
					}
				}
				else
				{
					sender.sendMessage(ChatColor.RED +"Direction must be either left or right");
				}

			}
			else if (args[0].equalsIgnoreCase("delete"))
				sender.sendMessage(ChatColor.RED +"/rrgrid delete [GridID]");
			
			else if (args[0].equalsIgnoreCase("list"))
				sender.sendMessage(ChatColor.RED +"/rrgrid list [MapID]");
			else
				GridHelp(sender);
		}
		else
			GridHelp(sender);
		return true;
	}
	
	private void GridHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.DARK_AQUA +"--------------");
		sender.sendMessage(ChatColor.DARK_AQUA +"/rrgrid Help:");
		sender.sendMessage(ChatColor.AQUA +"/rrgrid add");
		sender.sendMessage(ChatColor.AQUA +"/rrgrid delete");
		sender.sendMessage(ChatColor.AQUA +"/rrgrid list");
	}
}

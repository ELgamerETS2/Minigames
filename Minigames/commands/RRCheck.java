package Minigames.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Minigames.minigamesMain;
import Minigames.Games.RiverRace.RRSelection;
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
		
		else if (args.length == 1)
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
					if (RiverRaceCheckpoint.deleteCheckpoint(Integer.parseInt(args[1])))
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
						sender.sendMessage(ChatColor.DARK_AQUA +"[Checkpoint Found]:");
						sender.sendMessage(ChatColor.AQUA +"CheckPointID: "+Checkpoints[i].getCheckpointID());
						sender.sendMessage(ChatColor.AQUA +"MapID: "+Map.getMapID());
						sender.sendMessage(ChatColor.AQUA +"Number: "+Checkpoints[i].getNumber());
						for (int j = 0 ; j < Checkpoints[i].getLocations().length ; j++)
						{
							sender.sendMessage(ChatColor.AQUA +"Pass Location: " +"(" +Checkpoints[i].getLocations()[j].getBlockX()+", " +Checkpoints[i].getLocations()[j].getBlockY()+", " +Checkpoints[i].getLocations()[j].getBlockZ() +")");
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
				RiverRaceCheckpoint newCheckpoint = new RiverRaceCheckpoint(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				if (newCheckpoint.addCheckpoint())
				{
					sender.sendMessage(ChatColor.GREEN +"The checkpoint has been added to the DB");
					newCheckpoint.selectLastInsertID();
					
					//Look for the players selection points
					ArrayList<RRSelection> selections = minigamesMain.getInstance().selections;
					RRSelection blocksSelected = null;
					boolean bSuccess = true;
					
					for (int i = 0 ; i < selections.size() ; i++)
					{
						if (selections.get(i).getUUID().equals( ((Player) sender).getUniqueId() ))
						{
							blocksSelected = selections.get(i);
							ArrayList<Block> blocks = blocksSelected.getBlocks();
							for (int j = 0 ; j < blocks.size() ; j++)
							{
								bSuccess = newCheckpoint.addCheckpointBlocks(blocks.get(j).getX(), blocks.get(j).getY(), blocks.get(j).getZ());
							}
							if (bSuccess)
								sender.sendMessage(ChatColor.GREEN +"The checkpoint blocks have been added to the DB");
							else
								sender.sendMessage(ChatColor.RED +"The checkpoint blocks were unfortunetly not added to the DB. Please contact someone who can fix this.");
							minigamesMain.getInstance().selections.get(i).reset();
							break;
						}
					}
				}
				else
				{
					sender.sendMessage(ChatColor.RED +"The checkpoint was unfortunetly not added to the DB. Please contact someone who can fix this.");
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

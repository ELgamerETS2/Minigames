package Minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Minigames.MainLobby;

public class Lobby implements CommandExecutor
{
	private void help(CommandSender sender)
	{
		sender.sendMessage(ChatColor.DARK_AQUA +"---------------");
		sender.sendMessage(ChatColor.DARK_AQUA +"/lobby Help:");
		sender.sendMessage(ChatColor.AQUA +"/lobby delete");
		sender.sendMessage(ChatColor.AQUA +"/lobby add");
		sender.sendMessage(ChatColor.AQUA +"/lobby list");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage("Only players can manage lobbies");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("minigames.managelobbies"))
		{
			sender.sendMessage("You do not have the correct permissions");
			return true;
		}	
		
		switch (args.length)
		{
		case 0:
			help(sender);
			return true;
		case 1: //For listing
			switch (args[0].toLowerCase())
			{
			case "list":
				list(sender);
				return true;
			case "add":
				player.sendMessage(ChatColor.RED +"/lobby add [Name] [Version] [X] [Y] [Z] [HideLobbyX] [HideLobbyY] [HideLobbyZ] [RiverRaceX] [RiverRaceY] [RiverRaceZ]");
				return true;
			case "delete":
				player.sendMessage(ChatColor.RED +"/lobby delete [LobbyID]");
				return true;
			default:
				help(sender);
				return true;
			}
		case 2: //For deleting
			switch (args[0].toLowerCase())
			{
			case "list":
				list(sender);
				return true;
			case "add":
				player.sendMessage(ChatColor.RED +"/lobby add [Name] [Version] [X] [Y] [Z] [HideLobbyX] [HideLobbyY] [HideLobbyZ] [RiverRaceX] [RiverRaceY] [RiverRaceZ]");
				return true;
			case "delete":
				if (args[1].matches("[0-9]+"))
				{
					if (MainLobby.delete(Integer.parseInt(args[1])))
						player.sendMessage(ChatColor.GREEN +"The Lobby was successfully deleted");
					else
						player.sendMessage(ChatColor.RED +"The Lobby was not deleted. Please contact someone who can fix this");
				}
				else
					player.sendMessage(ChatColor.RED +"LobbyID must be an integer");
				return true;
			default:
				help(sender);
				return true;
			}
		case 12:
			switch (args[0].toLowerCase())
			{
			case "list":
				list(sender);
				return true;
			case "delete":
				player.sendMessage(ChatColor.RED +"/lobby delete [LobbyID]");
				return true;
			case "add":
				if (!args[2].matches("[0-9]+"))
				{
					player.sendMessage(ChatColor.RED +"Version must be an integer");
					return true;
				}
				if (!args[3].matches("[0-9]+"))
				{
					player.sendMessage(ChatColor.RED +"X Coord of Spawn must be an integer");
					return true;
				}
				if (!args[4].matches("[0-9]+"))
				{
					player.sendMessage(ChatColor.RED +"Y Coord of Spawn must be an integer");
					return true;
				}
				if (!args[5].matches("[0-9]+"))
				{
					player.sendMessage(ChatColor.RED +"Z Coord of Spawn must be an integer");
					return true;
				}
				if (!args[6].matches("[0-9]+"))
				{
					player.sendMessage(ChatColor.RED +"X Coord of Hide Lobby must be an integer");
					return true;
				}
				if (!args[7].matches("[0-9]+"))
				{
					player.sendMessage(ChatColor.RED +"Y Coord of Hide Lobby must be an integer");
					return true;
				}
				if (!args[8].matches("[0-9]+"))
				{
					player.sendMessage(ChatColor.RED +"Z Coord of Hide Lobby must be an integer");
					return true;
				}
				if (!args[9].matches("[0-9]+"))
				{
					player.sendMessage(ChatColor.RED +"X Coord of River Race must be an integer");
					return true;
				}
				if (!args[10].matches("[0-9]+"))
				{
					player.sendMessage(ChatColor.RED +"Y Coord of River Race must be an integer");
					return true;
				}
				if (!args[11].matches("[0-9]+"))
				{
					player.sendMessage(ChatColor.RED +"Z Coord of River Race must be an integer");
					return true;
				}
				MainLobby.add(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]),
						player.getWorld().getName(), Integer.parseInt(args[6]), Integer.parseInt(args[7]), Integer.parseInt(args[8]),
						Integer.parseInt(args[9]), Integer.parseInt(args[10]), Integer.parseInt(args[11]));
			default:
				help(sender);
				return true;
			}
		default:
			if (args[0].toLowerCase().equals("list"))
			{
				list(sender);
				return true;
			}
			help(sender);
			return true;
		}
	}
	
	private void list(CommandSender sender)
	{
		MainLobby[] lobbies = MainLobby.allActiveLobbies();
		if (lobbies.length == 0)
		{
			sender.sendMessage(ChatColor.RED +"No lobbies were found");
			return;
		}
		for (int i = 0 ; i < lobbies.length ; i++)
		{
			sender.sendMessage(ChatColor.DARK_AQUA +"[Lobby found]");
			sender.sendMessage(ChatColor.AQUA +"Name: " +lobbies[i].getSzName());
			if (lobbies[i].getActive())
				sender.sendMessage(ChatColor.AQUA +"Active: Yes");
			else
				sender.sendMessage(ChatColor.AQUA +"Active: No");
			sender.sendMessage(ChatColor.AQUA +"World: " +lobbies[i].getWorldName());
			sender.sendMessage(ChatColor.AQUA +"Spawn Coordinates: (" +lobbies[i].getX()+", " +lobbies[i].getY()+", " +lobbies[i].getZ()+")");
			sender.sendMessage(ChatColor.AQUA +"Hide n' Seek Lobby: (" +lobbies[i].getiHidePlatformX()+", " +lobbies[i].getiHidePlatformY()+", " +lobbies[i].getiHidePlatformZ()+")");
			sender.sendMessage(ChatColor.AQUA +"River Race Lobby: " +lobbies[i].getRiverRaceX()+", " +lobbies[i].getRiverRaceY()+", " +lobbies[i].getRiverRaceZ()+")");
			sender.sendMessage("");
		}
	}
}

package Minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Minigames.minigamesMain;

public class RiverRace implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		//Check is command sender is a player
		if (!(sender instanceof Player))
		{
			sender.sendMessage("&cYou cannot add a player to a region!");
			return true;
		}
		
		if (!sender.hasPermission("Minigames.RiverRace"))
		{
			sender.sendMessage(ChatColor.RED +"You do not have permission to use this command");
			return true;
		}
		
		Player player = (Player) sender;
		
		minigamesMain mgM = minigamesMain.getInstance();
		
		if (args.length == 0)
		{
			player.sendMessage(ChatColor.RED +"/rr [start|end]");
			return true;
		}
		else if (args.length > 1)
		{
			sender.sendMessage(ChatColor.RED +"Too many arguments");
			return true;
		}
		else if (args[0].equalsIgnoreCase("start"))
		{
			//Perm check now
			if (!sender.hasPermission("Minigames.RiverRace.start"))
			{
				sender.sendMessage(ChatColor.RED +"You do not have the correct minigame permissions");
				return false;
			}
			mgM.RRLobby.ManualStart(player);
			return true;
		}
		//Allows a mod to end a game
		else if (args[0].equalsIgnoreCase("end"))
		{
			//Perm check now
			if (!sender.hasPermission("Minigames.RiverRace.end"))
			{
				sender.sendMessage(ChatColor.RED +"You do not have the correct minigame permissions");
				return false;
			}
			mgM.RRLobby.ManualTerminate(player);
		}
		else
		{
			if (sender.hasPermission("Minigames.RiverRace.start") && sender.hasPermission("Minigames.RiverRace.end"))
				sender.sendMessage(ChatColor.RED +"/rr [start/end]");
			
			else if (sender.hasPermission("Minigames.RiverRace.start"))
				sender.sendMessage(ChatColor.RED +"/rr [start]");
			
			else if (sender.hasPermission("Minigames.RiverRace.end"))
				sender.sendMessage(ChatColor.RED +"/rr [end]");
			//Has no additional permissions but adds a parameter after /rr
			else
			{
				sender.sendMessage(ChatColor.RED +"You have /rr perms but are not allowed to do anything");
			}
			return true;
		}
		return true;
	}

}

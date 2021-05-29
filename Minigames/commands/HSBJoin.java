package Minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Minigames.minigamesMain;

public class HSBJoin implements CommandExecutor 
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		//Check is command sender is a player
		if (!(sender instanceof Player))
		{
			sender.sendMessage("&cYou cannot add a player to a region!");
			return true;
		}
		
		Player player = (Player) sender;
		
		minigamesMain mgM = minigamesMain.getInstance();
		
		if (args.length == 0)
		{
			if (!sender.hasPermission("Minigames.hide.join"))
			{
				sender.sendMessage(ChatColor.RED +"You do not have permission to join a hide and seek lobby");
				return true;
			}
			
			if (mgM.HSLobby.gameIsRunning)
			{
				mgM.HSLobby.HideGame.playerLeave(player);
			}
			mgM.HSLobby.playerJoinLobby(player);
			return true;
		}
		else if (args.length > 1)
		{
			sender.sendMessage(ChatColor.RED +"Too many arguments");
			return true;
		}
		else if (args[0].equalsIgnoreCase("leave"))
		{
			//Perm check now
			if (!sender.hasPermission("Minigames.hide.leave"))
			{
				sender.sendMessage(ChatColor.RED +"You do not have the correct minigame permissions");
				return false;
			}
			mgM.HSLobby.playerLeaveLobby(player);
		}
		else if (args[0].equalsIgnoreCase("start"))
		{
			//Perm check now
			if (!sender.hasPermission("Minigames.hide.start"))
			{
				sender.sendMessage(ChatColor.RED +"You do not have the correct minigame permissions");
				return false;
			}
			
			if (mgM.HSLobby.gameIsRunning)
			{
				sender.sendMessage(ChatColor.RED +"The hide and seek game is already running");
			}
			else
			{
				if (mgM.HSLobby.getPlayers() < 1)
				{
					sender.sendMessage(ChatColor.RED +"You need at least 2 players in the lobby to start manually");
				}
				else
				{
					sender.sendMessage(ChatColor.GREEN +"You have started the hide and seek game");
					mgM.HSLobby.lobbyRun();
				}
			}
			return true;
		}
		//Allows a mod to end a game
		else if (args[0].equalsIgnoreCase("end"))
		{
			//Perm check now
			if (!sender.hasPermission("Minigames.hide.end"))
			{
				sender.sendMessage(ChatColor.RED +"You do not have the correct minigame permissions");
				return false;
			}
			if (mgM.HSLobby.gameIsRunning)
			{
				mgM.HSLobby.HideGame.terminate();
			}
			else
			{
				sender.sendMessage(ChatColor.RED +"The hide and seek game is not running");
			}
		}
		else
		{
			if (sender.hasPermission("Minigames.hide.start") && sender.hasPermission("Minigames.hide.end"))
				sender.sendMessage(ChatColor.RED +"/hide [start/end]");
			
			else if (sender.hasPermission("Minigames.hide.start"))
				sender.sendMessage(ChatColor.RED +"/hide [start]");
			
			else if (sender.hasPermission("Minigames.hide.end"))
				sender.sendMessage(ChatColor.RED +"/hide [end]");
			//Has no additional permissions but adds a parameter after /hide
			else
			{
				//Have them join a lobby
				if (!sender.hasPermission("Minigames.hide.join"))
				{
					sender.sendMessage(ChatColor.RED +"You do not have permission to join a hide and seek lobby");
					return true;
				}
				
				if (mgM.HSLobby.gameIsRunning)
				{
					mgM.HSLobby.HideGame.playerLeave(player);
				}
				mgM.HSLobby.playerJoinLobby(player);
				return true;
			}
			return true;
		}
		return true;
	}
}
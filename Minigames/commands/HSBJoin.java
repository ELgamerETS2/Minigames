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
		
		minigamesMain mgM = minigamesMain.getInstance();
		
		if (args.length == 0)
		{
			Player player = (Player) sender;
			
			mgM.HSLobby.playerJoinLobby(player);
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
			sender.sendMessage("You have started the hide and seek game");
			mgM.HSLobby.lobbyRun();
			return true;
		}
		else
		{
			sender.sendMessage(ChatColor.RED +"/hide [start]");
		}
		return true;
	}
}

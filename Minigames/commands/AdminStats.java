package Minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Minigames.SQLInjection;
import Minigames.minigamesMain;

public class AdminStats implements CommandExecutor
{
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
		
		if (!sender.hasPermission("Minigames.stats.adminstats"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}
		
		if (args.length == 0)
		{
			player.sendMessage(ChatColor.RED + "State the query");
		}
		else
		{
			SQLInjection sql = new SQLInjection(args);
			sql.runQuery();
			
		}
		
		return true;
	}
}

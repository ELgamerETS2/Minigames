package Minigames.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Minigames.minigamesMain;
import Minigames.Games.HideAndSeek.HideAndSeekGame;

public class HSBTest implements CommandExecutor 
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
		
		//Convert sender to player
		Player player = (Player) sender;
		
		Player[] players = new Player[1];
		players[0] = player;
		
		HideAndSeekGame HSB = new HideAndSeekGame(players, 1, 0, mgM);
		
		HSB.highGameProcesses();
		
		return true;
	}
}

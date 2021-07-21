package Minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Minigames.statistics.HideStats;

public class Mystats implements CommandExecutor 
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		//Check is command sender is a player
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED +"You are not a player so cannot see your own stats!");
			return true;
		}
		
		//Check if the user has mystats permissions
		if (!sender.hasPermission("Minigames.mystats"))
		{
			sender.sendMessage(ChatColor.RED +"You do not have permission to view your stats");
			return true;
		}
		
		Player player = (Player) sender;
		
		//No parameters given
		if (args.length == 0)
		{
			HideStats playerstats = new HideStats(player.getUniqueId());
			playerstats.countTotalPoints();
			playerstats.countGames();
			playerstats.getTimePlayed();
			
			//Get seconds played from playerstats object
			int iSecondsRemaining = (int) playerstats.iSecondsPlayed;
			
			//Calculate time played in days, minutes, hours, seconds from total seconds
			int iDays = iSecondsRemaining / 86400;
			iSecondsRemaining = iSecondsRemaining % 86400;
			
			int iHours = iSecondsRemaining / 3600;
			iSecondsRemaining = iSecondsRemaining % 3600;
			
			int iMinutes = iSecondsRemaining / 60;
			iSecondsRemaining = iSecondsRemaining % 60;
			
			int iSeconds = iSecondsRemaining;
			
			//Compile output for time played
			String szOutput = "";
			if (iDays != 0)
			{
				szOutput = szOutput + iDays +" Days ";
			}
			if (iHours != 0)
			{
				szOutput = szOutput + iHours +" Hours ";
			}
			if (iMinutes != 0)
			{
				szOutput = szOutput + iMinutes +" Minutes ";
			}
			szOutput = szOutput + iSeconds +" Seconds";
			
			player.sendMessage("Total Points: "+playerstats.iTotalPoints);
			player.sendMessage("Total Games Played: "+playerstats.iTotalGames);
			player.sendMessage("Total Time Played: "+szOutput);
		}
		return true;
	}
}

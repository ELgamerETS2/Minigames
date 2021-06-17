package Minigames.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import Minigames.Games.Gametype;
import Minigames.statistics.PlayerStats;

public class HideStatsGUI
{
	private static Inventory inv;
	private static String inventory_name;
	private static int inv_rows = 3 * 9;
	
	public static String getInventoryName()
	{
		return inventory_name;
	}
	
	public static void initialize()
	{
		inventory_name = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Hide and Seek Stats";

		inv = Bukkit.createInventory(null, inv_rows);
	}
	
	public static Inventory GUI (Player player)
	{
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, getInventoryName());

		inv.clear();
		
		//Get stats for the player
		PlayerStats playerstats = new PlayerStats(player.getUniqueId(), Gametype.Hide_And_Seek);
		playerstats.countTotalPoints();
		playerstats.countGames();
		playerstats.getTimePlayed();
		
		int iGamesPlayed = playerstats.iTotalGames;
		
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
		
		String[] szOutputs;
		int iRows = 0;
		
		//Compile output for time played
		String szDays = "";
		String szHours = "";
		String szMinutes = "";
		String szSeconds = "";
		
		if (iDays != 0)
		{
			szDays = iDays +" Days ";
			iRows++;
		}
		if (iHours != 0)
		{
			szHours = szHours + iHours +" Hours ";
			iRows++;
		}
		if (iMinutes != 0)
		{
			szMinutes = szMinutes + iMinutes +" Minutes ";
			iRows++;
		}
		szSeconds = szSeconds + iSeconds +" Seconds";
		iRows++;
		
		//Initiate array
		szOutputs = new String[iRows];
		
		iRows = 0;
		
		//Add to the array
		if (iDays != 0)
		{
			szOutputs[iRows] = ChatColor.LIGHT_PURPLE +szDays;
			iRows++;
		}
		if (iHours != 0)
		{
			szOutputs[iRows] = ChatColor.LIGHT_PURPLE +szHours;
			iRows++;
		}
		if (iMinutes != 0)
		{
			szOutputs[iRows] = ChatColor.LIGHT_PURPLE +szMinutes;
			iRows++;
		}
		szOutputs[iRows] = ChatColor.LIGHT_PURPLE +szSeconds;
		
		//Create item for time played
		Utils.createItem(inv, Material.CLOCK, 1, 5, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Total time played", szOutputs);
		
		//Create item for games played
		Utils.createItem(inv, Material.LADDER, 1, 2, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Total games played", ChatColor.LIGHT_PURPLE +"" +iGamesPlayed);
		
		//Create item for points
		Utils.createItem(inv, Material.GUNPOWDER, 1, 8, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Your points", ""+playerstats.iTotalPoints);
		
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
}

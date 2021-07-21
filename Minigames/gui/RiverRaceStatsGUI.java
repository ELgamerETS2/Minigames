package Minigames.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import Minigames.statistics.RiverRaceStats;

public class RiverRaceStatsGUI
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
		inventory_name = ChatColor.BLUE + "" + ChatColor.BOLD + "River Race Stats";

		inv = Bukkit.createInventory(null, inv_rows);
	}
	
	public static Inventory GUI (Player player)
	{
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, getInventoryName());

		inv.clear();
		
		//Get stats for the player
		RiverRaceStats playerRRStats = new RiverRaceStats(player.getUniqueId());
		playerRRStats.getGames();
		playerRRStats.countGames();
		playerRRStats.getTimePlayed();
		playerRRStats.quickestTimes();
		
		int iGamesPlayed = playerRRStats.iTotalGames;
		
		//Get seconds played from playerstats object
		int iSecondsRemaining = (int) playerRRStats.iSecondsPlayed;
		
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
			szOutputs[iRows] = ChatColor.BLUE +szDays;
			iRows++;
		}
		if (iHours != 0)
		{
			szOutputs[iRows] = ChatColor.BLUE +szHours;
			iRows++;
		}
		if (iMinutes != 0)
		{
			szOutputs[iRows] = ChatColor.BLUE +szMinutes;
			iRows++;
		}
		szOutputs[iRows] = ChatColor.BLUE +szSeconds;
		
		//Create item for time played
		Utils.createItem(inv, Material.CLOCK, 1, 7, ChatColor.BLUE + "" + ChatColor.BOLD + "Total time played", szOutputs);
		
		//Create item for games played
		Utils.createItem(inv, Material.OAK_BOAT, 1, 3, ChatColor.BLUE + "" + ChatColor.BOLD + "Total games played", ChatColor.BLUE +"" +iGamesPlayed);
		
		
		//Create item for points
		if (playerRRStats.maps.length == 1)
		{
			Utils.createItem(inv, Material.GUNPOWDER, 1, 23, ChatColor.BLUE + "" + ChatColor.BOLD + "Fasted time", ChatColor.BLUE +""+playerRRStats.maps[0].getLocation(), ChatColor.BLUE +""+playerRRStats.quickestTimes[0]/1000 +"."+(playerRRStats.quickestTimes[0]%1000)/100 +" seconds");
		}
		else if (playerRRStats.maps.length == 2)
		{
			Utils.createItem(inv, Material.GUNPOWDER, 1, 21, ChatColor.BLUE + "" + ChatColor.BOLD + "Fasted time", ChatColor.BLUE +""+playerRRStats.maps[0].getLocation(), ChatColor.BLUE +""+playerRRStats.quickestTimes[0]/1000 +"."+(playerRRStats.quickestTimes[0]%1000)/100 +" seconds");
			Utils.createItem(inv, Material.GUNPOWDER, 1, 25, ChatColor.BLUE + "" + ChatColor.BOLD + "Fasted time", ChatColor.BLUE +""+playerRRStats.maps[1].getLocation(), ChatColor.BLUE +""+playerRRStats.quickestTimes[1]/1000 +"."+(playerRRStats.quickestTimes[1]%1000)/100 +" seconds");
		}
		else if (playerRRStats.maps.length == 3)
		{
			Utils.createItem(inv, Material.GUNPOWDER, 1, 21, ChatColor.BLUE + "" + ChatColor.BOLD + "Fasted time", ChatColor.BLUE +""+playerRRStats.maps[0].getLocation(), ChatColor.BLUE +""+playerRRStats.quickestTimes[0]/1000 +"."+(playerRRStats.quickestTimes[0]%1000)/100 +" seconds");
			Utils.createItem(inv, Material.GUNPOWDER, 1, 23, ChatColor.BLUE + "" + ChatColor.BOLD + "Fasted time", ChatColor.BLUE +""+playerRRStats.maps[1].getLocation(), ChatColor.BLUE +""+playerRRStats.quickestTimes[1]/1000 +"."+(playerRRStats.quickestTimes[1]%1000)/100 +" seconds");
			Utils.createItem(inv, Material.GUNPOWDER, 1, 25, ChatColor.BLUE + "" + ChatColor.BOLD + "Fasted time", ChatColor.BLUE +""+playerRRStats.maps[2].getLocation(), ChatColor.BLUE +""+playerRRStats.quickestTimes[2]/1000 +"."+(playerRRStats.quickestTimes[2]%1000)/100 +" seconds");
		}
		
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
}

package Minigames.statistics;

import java.util.ArrayList;

import org.bukkit.ChatColor;

import Minigames.minigamesMain;
import Minigames.Games.HideAndSeek.HideAndSeekFinder;

public class Statistics
{
	public minigamesMain CorePlugin;
	public HideRecord[] records;
	
	public Statistics(minigamesMain CorePlugin)
	{
		this.CorePlugin = CorePlugin;
	}
	
	public void storeHideAndSeekPoints(ArrayList<HideAndSeekFinder> finders, int GameID)
	{
		this.records = new HideRecord[finders.size()];
		int i;
		int iPoints;
		HideAndSeekFinder finder;
		
		//Copy player, gameID and points into a stat record, then store it in database
		for (i = 0 ; i < records.length ; i++)
		{
			finder = finders.get(i);
			iPoints = finder.iPoints + finder.iFound;
			records[i] = new HideRecord(finder.player.getUniqueId(), GameID, iPoints);
			finder.player.sendMessage(ChatColor.LIGHT_PURPLE +"You have received "+ChatColor.DARK_PURPLE +ChatColor.BOLD +iPoints +" points");
			records[i].storeRecord();
		}
	}
	
	//----------------------------------------------
	//------------Fetching from database------------
	//----------------------------------------------
	
/*	public StatRecord[] getAllRecordsForPlayer(String szUUID)
	{
		
	}
*/
}

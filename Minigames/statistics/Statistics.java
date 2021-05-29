package Minigames.statistics;

import java.util.ArrayList;

import Minigames.minigamesMain;
import Minigames.Games.HideAndSeek.HideAndSeekFinder;

public class Statistics
{
	public minigamesMain CorePlugin;
	public StatRecord[] records;
	
	public Statistics(minigamesMain CorePlugin)
	{
		this.CorePlugin = CorePlugin;
	}
	
	public void storeHideAndSeekPoints(ArrayList<HideAndSeekFinder> finders, int GameID)
	{
		this.records = new StatRecord[finders.size()];
		int i;
		
		HideAndSeekFinder finder;
		
		//Copy player, gameID and points into a stat record, then store it in database
		for (i = 0 ; i < records.length ; i++)
		{
			finder = finders.get(i);
			records[i] = new StatRecord(finder.player.getUniqueId(), GameID, finder.iPoints + finder.iFound);
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

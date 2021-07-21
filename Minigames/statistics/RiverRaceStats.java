package Minigames.statistics;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;

import Minigames.Games.Game;
import Minigames.Games.RiverRace.RiverRaceMap;

public class RiverRaceStats extends PlayerStats
{
	//Used to store river race values returned
	RiverRaceTime[] allRiverRaceRecords;
	public int[] quickestTimes;
	public RiverRaceMap[] maps;
	
	public RiverRaceStats(UUID uuid)
	{
		super(uuid);
	}
	
	public void reset()
	{
		super.reset();
	}
	
	public void getGames()
	{
		Bukkit.getConsoleSender().sendMessage("About to output line 25 of RRS");
		Bukkit.getConsoleSender().sendMessage(uuid.toString());
		allRiverRaceRecords = RiverRaceTime.getAllRecordsForPlayer(uuid);
	}

	public void countTotalPoints()
	{
		
	}

	public void countGames()
	{
		iTotalGames = allRiverRaceRecords.length;
	}

	public void getTimePlayed()
	{
		int i;
		iSecondsPlayed = 0;
		int iMilliSecondsPlayed = 0;
		
		for (i = 0 ; i < allRiverRaceRecords.length ; i++)
		{
			iMilliSecondsPlayed = iMilliSecondsPlayed + allRiverRaceRecords[i].getTime();
		}
		
		iSecondsPlayed = iMilliSecondsPlayed / 1000;
	}
	
	/**
	 * Use once and store in a local variable
	 */
	public void quickestTimes()
	{
		int iMaps = RiverRaceMap.count();
		int[] iMapIDs = RiverRaceMap.MapIDs();
		RiverRaceMap maps[] = new RiverRaceMap[iMaps];
		
		int[] quickestTimes = new int[iMaps];
		
		//Get all times into an arraylist
		ArrayList<RiverRaceTime> allTimes = new ArrayList<>();
		
		for (int i = 0 ; i < allRiverRaceRecords.length ; i++)
		{
			allTimes.add(allRiverRaceRecords[i]);
		}
		
		int iLeastMilliseconds;
		int iIndex = 0;
		Game game;
		boolean bMapPlayed = false;
		
		//For each map
		for (int i = 0 ; i < iMaps ; i++)
		{
			bMapPlayed = false; //Until a time is found
			iLeastMilliseconds = 2147483647;
			
			//Gets the details of each map
			maps[i] = new RiverRaceMap(iMapIDs[i]);
			maps[i].setMapFromMapID();
			
			//Goes through all times
			for (int j = 0 ; j < allTimes.size() ; j++)
			{
				game = new Game();
				game.setGameByID(allTimes.get(j).getGameID());
				
				//If the map which the current time was recorded on was that of the map we are currently on, update the lowest time 
				if (game.getMapID() == maps[i].getMapID() && allTimes.get(j).getTime() < iLeastMilliseconds)
				{
					bMapPlayed = true;
					iLeastMilliseconds = allTimes.get(j).getTime();
					iIndex = j;
				}
			}
			if (bMapPlayed)
				quickestTimes[i] = allTimes.get(iIndex).getTime();
			else
				quickestTimes[i] = 0;
		}
		this.quickestTimes = quickestTimes;
		this.maps = maps;
	}
}

package Minigames.statistics;

import java.sql.Date;
import java.util.UUID;

import Minigames.minigamesMain;
import Minigames.Games.Game;
import Minigames.Games.Gametype;

public class PlayerStats
{
	//Handles statistics database queries
	Statistics stats = new Statistics(minigamesMain.getInstance());
		
	//Member fields used a parameters for accessing data
	private boolean bAllTime;
	private Date dStart;
	private Date dEnd;
	
	protected UUID uuid;
	
	public int iTotalGames;
	public Game[] games;
	public long iSecondsPlayed;
	
	//Used when no game or time parameters are specified
	public PlayerStats(UUID uuid)
	{
		//Sets values to default values
		reset();
		this.uuid = uuid;
	//	allPlayerRecords = HideRecord.getAllRecordsForPlayer(uuid);
	}
	
	public PlayerStats(UUID uuid, Gametype gameType)
	{
		//Sets values to default values
		reset();
		
	//	allPlayerRecords = HideRecord.getAllRecordsForPlayerGame(uuid, gameType);
	}
	
	public PlayerStats(UUID uuid, PeriodType periodType, Gametype gameType)
	{
		//Sets values to default values
		reset();
		
		//Test period type and calculate dates
		if (periodType == PeriodType.AllTime)
		{
			bAllTime = true;
		}
	}
		
	public void reset()
	{
		bAllTime = false;
	}
}

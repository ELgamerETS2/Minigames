package Minigames.statistics;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
	
	//Used to store values returned
	HideRecord[] allPlayerRecords;
	
	//Used to store values returned
	RiverRaceTime[] allRiverRaceRecord;
	
	public int iTotalPoints;
	public int iTotalGames;
	public Game[] games;
	public long iSecondsPlayed;
	
	//Used when no game or time parameters are specified
	public PlayerStats(UUID uuid)
	{
		//Sets values to default values
		reset();
		
		allPlayerRecords = HideRecord.getAllRecordsForPlayer(uuid);
	}
	
	public PlayerStats(UUID uuid, Gametype gameType)
	{
		//Sets values to default values
		reset();
		
		allPlayerRecords = HideRecord.getAllRecordsForPlayerGame(uuid, gameType);
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
		iTotalPoints = 0;
	}
	
	public void countTotalPoints()
	{
		int i;
		for (i = 0 ; i < allPlayerRecords.length ; i++)
		{
			iTotalPoints = iTotalPoints + allPlayerRecords[i].getPoints();
		}
	}
	
	public void countGames()
	{
		iTotalGames = allPlayerRecords.length;
	}
	
	public void getTimePlayed()
	{
		int i;
		iSecondsPlayed = 0;
		Game game;
		Timestamp gameStart;
		Timestamp gameEnd;
		
		for (i = 0 ; i < allPlayerRecords.length ; i++)
		{
			//Get game
			game = allPlayerRecords[i].getGame();
			//Store start and end locally
			gameStart = game.getTimeStart();
			gameEnd = game.getTimeEnd();
			
			//Convert to local date time
			LocalDateTime NewGameStart = gameStart.toLocalDateTime();
			LocalDateTime NewGameEnd = gameEnd.toLocalDateTime();
			
			iSecondsPlayed = iSecondsPlayed + (NewGameEnd.toEpochSecond(ZoneOffset.UTC) - NewGameStart.toEpochSecond(ZoneOffset.UTC));
		}	
	}
}

package Minigames.statistics;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import Minigames.Games.Game;

public class HideStats extends PlayerStats
{
	//Used to store values returned
	HideRecord[] allPlayerRecords;
	
	public int iTotalPoints;
	
	
	public HideStats(UUID uniqueId)
	{
		super(uniqueId);
	}
	
	public void reset()
	{
		super.reset();
		iTotalPoints = 0;
	}
	
	public void getGames()
	{
		allPlayerRecords = HideRecord.getAllRecordsForPlayer(uuid);
	}
	
	public void countGames()
	{
		iTotalGames = allPlayerRecords.length;
	}
	
	public void countTotalPoints()
	{
		int i;
		for (i = 0 ; i < allPlayerRecords.length ; i++)
		{
			iTotalPoints = iTotalPoints + allPlayerRecords[i].getPoints();
		}
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

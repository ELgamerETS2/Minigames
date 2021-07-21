package Minigames.statistics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import Minigames.minigamesMain;
import Minigames.Games.Game;

public class RiverRaceTime
{
//	private int ScoreID;
	private Game Game;
	private int GameID;
	private int iTime; //In milliseconds
	private UUID uUUID;
	
	public RiverRaceTime(UUID uuid, int iGameID, long lTimeStart, long lTimeEnd)
	{
		this.uUUID = uuid;
		this.GameID = iGameID;
		this.iTime = (int) (lTimeEnd - lTimeStart);
		
		storeTime();
	}
	
	public RiverRaceTime(int i)
	{
		this.iTime = i;
	}
	
	public RiverRaceTime()
	{
		// TODO Auto-generated constructor stub
	}
	
	public int getTime()
	{
		return iTime;
	}
	
	public int getGameID()
	{
		return Game.getGameID();
	}
	
	private boolean storeTime()
	{
		boolean bSuccess = false;
		int iCount = -1;
		String sql;
		
		Statement SQL = null;
		
		try
		{
			//Compiles the command to add the new user
			sql = "INSERT INTO RiverRaceTimes (PlayerUUID, GameID, Time) "
					+ " VALUES("
					+ "\"" + this.uUUID +"\", "
					+ "" + this.GameID +", "
					+ "" + this.iTime + ");";
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] [RiverRaceTimes] "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			
			//Executes the update and returns the amount of records updated
			iCount = SQL.executeUpdate(sql);
			
			//Checks whether only 1 record was updated
			if (iCount == 1)
			{
				//If so, bSuccess is set to true
				bSuccess = true;
			}
		}
		catch(SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] [RiverRaceTimes] SQL Error adding Stats record in");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] [RiverRaceTimes] Non-SQL Error adding Stats record in");
			e.printStackTrace();
		}
		return bSuccess;
	}
	
	public static RiverRaceTime[] getAllRecordsForPlayer(UUID UUID)
	{
		String szUUID = UUID.toString();
		
		int iCount = countAllForPlayer(szUUID);
		RiverRaceTime[] allRecords = new RiverRaceTime[iCount];
		
		String sql;
		Statement SQL = null;
		ResultSet resultSet = null;
		int i;
		
		try
		{
			//Compiles the command to add the new user
			sql = "Select * FROM RiverRaceTimes WHERE PlayerUUID = \""+szUUID+"\"";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceTimes] "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			
			//Executes the update and returns the amount of records updated
			resultSet = SQL.executeQuery(sql);
			
			i = 0;
			while (resultSet.next())
			{
				allRecords[i] = new RiverRaceTime();
				allRecords[i].Game = new Game();
				allRecords[i].Game.setGameByID(resultSet.getInt("GameID"));
				allRecords[i].iTime = resultSet.getInt("Time");
				allRecords[i].uUUID = UUID;
				i++;
			}
		}
		catch(SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] [RiverRaceTimes] SQL Error gathering all stats records for one player");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return allRecords;
	}
	
	//Counts the amount of records for a player
	private static int countAllForPlayer(String szUUID)
	{
		boolean bSuccess = false;
		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		int iCount = 0;
		
		try
		{
			//Collects all maps
			sql = "Select * FROM RiverRaceTimes WHERE PlayerUUID = \""+szUUID+"\"";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceTimes] "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no records are found, program will notify thing
			if (bSuccess == false)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceTimes] No RiverRaceTime found");
				iCount = 0;
			}
			else
			{
				iCount++;
			}
			
			//Checks that there is only 1 record returned
			while (resultSet.next() != false)
			{
				iCount++;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return iCount;
	}
}

package Minigames.statistics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import Minigames.minigamesMain;
import Minigames.Games.Game;
import Minigames.Games.Gametype;

public class StatRecord
{
	private UUID uUuid;
	private Game game;
	private int iPoints;
	
	//--------------------------------------------
	//----------------Constructors----------------
	//--------------------------------------------
	public StatRecord(UUID uUuid, int gameID, int iPoints)
	{
		this.uUuid = uUuid;
		game = new Game();
		game.setGameID(gameID);
		this.iPoints = iPoints;
	}
	
	public StatRecord()
	{
	}

	//-------------------------------------------
	//------------------Getters------------------
	//-------------------------------------------
	public int getPoints()
	{
		return iPoints;
	}
	
	public Game getGame()
	{
		return game;
	}
	
	public boolean storeRecord()
	{
		boolean bSuccess = false;
		int iCount = -1;
		String sql;
		
		Statement SQL = null;
		
		try
		{
			//Compiles the command to add the new user
			sql = "INSERT INTO "+minigamesMain.getInstance().STATS +" (UUID, GameID, Points) "
					+ " VALUES("
					+ "\"" + this.uUuid +"\", "
					+ "" + this.game.getGameID() +", "
					+ "" + this.getPoints()	+ ");";
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] [Hide] "+sql);
			
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
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] [Hide] SQL Error adding Stats record in");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bSuccess;
	}
	
	public static StatRecord[] getAllRecordsForPlayer(UUID UUID)
	{
		String szUUID = UUID.toString();
		
		int iCount = countAllForPlayer(szUUID);
		StatRecord[] allRecords = new StatRecord[iCount];
		
		String sql;
		Statement SQL = null;
		ResultSet resultSet = null;
		int i;
		
		try
		{
			//Compiles the command to add the new user
			sql = "Select * FROM Stats WHERE UUID = \""+szUUID+"\"";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Stats] "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			
			//Executes the update and returns the amount of records updated
			resultSet = SQL.executeQuery(sql);
			
			i = 0;
			while (resultSet.next())
			{
				allRecords[i] = new StatRecord();
				allRecords[i].game = new Game();
				allRecords[i].game.setGameByID(resultSet.getInt("GameID"));
				allRecords[i].iPoints = resultSet.getInt("Points");
				allRecords[i].uUuid = UUID;
				i++;
			}
		}
		catch(SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] [Stats] SQL Error gathering all stats records for one player");
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
			sql = "Select * FROM Stats WHERE UUID = \""+szUUID+"\"";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Stats] "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no records are found, program will notify thing
			if (bSuccess == false)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Stats] No stats found");
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
	
	public static StatRecord[] getAllRecordsForPlayerGame(UUID UUID, Gametype gametype)
	{
		String szUUID = UUID.toString();
		
		int iCount = countAllForPlayerGame(szUUID, gametype);
		StatRecord[] allRecords = new StatRecord[iCount];
		
		String sql;
		Statement SQL = null;
		ResultSet resultSet = null;
		int i;
		
		try
		{
			//Compiles the command to add the new user
			sql = "Select * FROM Stats, Games WHERE Stats.UUID = \""+szUUID+"\" AND Games.GameID = Stats.GameID AND Games.GameType = '"+gametype.toString() +"';";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Stats] "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			
			//Executes the update and returns the amount of records updated
			resultSet = SQL.executeQuery(sql);
			
			i = 0;
			while (resultSet.next())
			{
				allRecords[i] = new StatRecord();
				allRecords[i].game = new Game();
				allRecords[i].game.setGameByID(resultSet.getInt("GameID"));
				allRecords[i].iPoints = resultSet.getInt("Points");
				allRecords[i].uUuid = UUID;
				i++;
			}
		}
		catch(SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] [Stats] SQL Error gathering all stats records for one player");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return allRecords;
	}
	
	//Counts the amount of records for a player on a particular game
	private static int countAllForPlayerGame(String szUUID, Gametype gametype)
	{
		boolean bSuccess = false;
		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		int iCount = 0;
		
		try
		{
			//Collects all maps
			sql = "Select * FROM Stats, Games WHERE Stats.UUID = \""+szUUID+"\" AND Games.GameID = Stats.GameID AND Games.GameType = '"+gametype.toString() +"';";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Stats,Games] "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no records are found, program will notify thing
			if (bSuccess == false)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Stats,Games] No stats found");
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

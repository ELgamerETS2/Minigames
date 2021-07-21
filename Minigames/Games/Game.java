package Minigames.Games;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import Minigames.minigamesMain;

public class Game
{
	private int GameID;
	private Gametype gameType;
	private int iMapID; // Not really decided yet
	private Timestamp TimeStart;
	private Timestamp TimeEnd;
	
	//Setters
	public void setGameID(int gameID)
	{
		this.GameID = gameID;
	}
	public void setMapID(int iMapID)
	{
		this.iMapID = iMapID;
	}
	public void setTimeStart()
	{
		this.TimeStart = Timestamp.valueOf(LocalDateTime.now());
	}
	public void setTimeEnd()
	{
		this.TimeEnd = Timestamp.valueOf(LocalDateTime.now());
	}
	
	//Getters
	public int getGameID()
	{
		return this.GameID;
	}
	public Timestamp getTimeStart()
	{
		return this.TimeStart;
	}
	public Timestamp getTimeEnd()
	{
		return this.TimeEnd;
	}
	public int getMapID()
	{
		return this.iMapID;
	}

	//Constructors
	public Game()
	{
		
	}
	
	public Game(Gametype gameType)
	{
		this.gameType = gameType;
	}
	
	public void setGameByID(int GameID)
	{		
		String sql;
		Statement SQL = null;
		ResultSet resultSet = null;
		
		try
		{
			//Compiles the command to add the new user
			sql = "Select * FROM Games WHERE GameID = "+GameID;
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Games] "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			
			//Executes the update and returns the amount of records updated
			resultSet = SQL.executeQuery(sql);
			resultSet.next();
			this.GameID = GameID;
			gameType = Gametype.valueOf(resultSet.getString("GameType"));
			iMapID = resultSet.getInt("MapID");
			TimeStart = resultSet.getTimestamp("TimeStart");
			TimeEnd = resultSet.getTimestamp("TimeEnd");
		}
		catch(SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] [Stats] SQL Error fetching game by GameID");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean storeGameInDatabase()
	{
		boolean bSuccess = false;
		int iCount = -1;
		String sql;
		
		Statement SQL = null;
		
		try
		{
			//Compiles the command to add the new user
			sql = "INSERT INTO "+minigamesMain.getInstance().GAMES +" (GameType, MapID, TimeStart)"
					+ "VALUES("
					+ "\"" + this.gameType +"\" ,"
					+ "\"" + this.iMapID +"\" ,"
					+ "\"" + this.TimeStart +"\""
							+ ");";
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
			se.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bSuccess;
	}
	
	public int selectLastInsertID()
	{
		int iGameID = -1;
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet;
		
		try
		{
			//Compiles the command to add the new user
			sql = "Select LAST_INSERT_ID()";
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			
			//Executes the update and returns the amount of records updated
			resultSet = SQL.executeQuery(sql);
			
			resultSet.next();
			
			iGameID = resultSet.getInt(1);
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		this.GameID = iGameID;
		return iGameID;
	}
	
	public boolean storeGameEndInDatabase()
	{
		boolean bSuccess = false;
		int iCount = -1;
		String sql;
		
		Statement SQL = null;
		
		try
		{
			sql = "UPDATE "+minigamesMain.getInstance().GAMES +" SET TimeEnd = \""+ TimeEnd +"\" Where GameID = "+this.GameID+";";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Games] " +sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			
			//Executes the update and returns the amount of records updated
			iCount = SQL.executeUpdate(sql);
			
			//Checks whether only 1 record was updated
			if (iCount == 1)
			{
				//If so, bSuccess is set to true
				bSuccess = true;
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Minigames] [Games] ["+gameType.toString() +"] TimeEnd stored to game");
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] [Games]  ["+gameType.toString() +"] TimeEnd unsuccesfully added");
			}
		}
		catch (SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] [Games]  ["+gameType.toString() +"] SQL Error adding TimeEnd");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] [Games]  ["+gameType.toString() +"] Non-SQL Error adding TimeEnd");
			e.printStackTrace();
		}
		return bSuccess;
	}
}

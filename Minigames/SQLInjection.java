package Minigames;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import Minigames.Games.Game;
import Minigames.statistics.StatRecord;

public class SQLInjection
{
	String sql;
	
	public SQLInjection(String[] args)
	{
		sql = "";
		for (String string : args)
		{
			sql = sql + string +" ";
		}
	}
	public void runQuery()
	{
		boolean bSuccess = false;
		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		int iCount = 0;
		int iColumns;
		
		try
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [AdminStats-Count] "+sql);

			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			iColumns = resultSet ;
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no records are found, program will notify thing
			if (bSuccess == false)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames] [AdminStats-Count] No stats found");
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
		
		int i;
		
		String[][] results = 
		
		try
		{
			//Compiles the command to add the new user
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
}

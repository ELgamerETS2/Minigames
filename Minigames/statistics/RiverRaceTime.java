package Minigames.statistics;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import Minigames.minigamesMain;

public class RiverRaceTime
{
	private int ScoreID;
	private int GameID;
	private int iTime; //In milliseconds
	private UUID UUID;
	private boolean bFinished;
	
	public RiverRaceTime(UUID uuid, int iGameID, long lTimeStart, long lTimeEnd)
	{
		this.UUID = uuid;
		this.GameID = iGameID;
		this.iTime = (int) (lTimeEnd - lTimeStart);
		
		storeTime();
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
					+ "\"" + this.UUID +"\", "
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
	
}

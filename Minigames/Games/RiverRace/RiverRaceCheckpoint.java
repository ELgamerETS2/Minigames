package Minigames.Games.RiverRace;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import Minigames.minigamesMain;

public class RiverRaceCheckpoint
{
	private int iMapID;
	private int iCheckPointID;
	private int iNumber;
	
	private Location[] locations;
	
	private World world;
	
	public RiverRaceCheckpoint (int iMap, int iNumber)
	{
		this.iMapID = iMap;
		this.iNumber = iNumber;
		
	}
	
	public Location[] getLocations()
	{
		return locations;
	}
	
	public int getCheckpointID()
	{
		return iCheckPointID;
	}
	
	public static RiverRaceCheckpoint[] getAllForMapID(int mapID, World world)
	{
		RiverRaceCheckpoint[] Checkpoints;
		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		int i;
		
		//Counts total amount of maps first
		int iCount = count();
		
		//Initiates the int array for storing MapIDs with length of the amount of maps just found
		Checkpoints = new RiverRaceCheckpoint[iCount];
		
		//If no maps were found, skip collecting the list
		if (iCount == 0)
		{
			return Checkpoints;
		}
		
		try
		{
			//Collects all maps
			sql = "SELECT * FROM RiverRaceCheckPoints Where MapID = "+mapID +"ORDER BY Number";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Select * From RiverRaceCheckPoints]: "+sql);
			
			//Executes the query
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			
			//Moves the curser to the next line
			resultSet.next();
						
			//Checks that there is only 1 record returned
			for (i = 0 ; i < iCount ; i++)
			{
				Checkpoints[i].iCheckPointID = resultSet.getInt("CheckPointID");
				Checkpoints[i].iMapID = resultSet.getInt("MapID");
				Checkpoints[i].iNumber = resultSet.getInt("Number");
				//Then get the locations
				Checkpoints[i].GetLocations(world);
				
				resultSet.next();
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return Checkpoints;
	}
	
	//Counts the amount of checkpoints
	public static int count()
	{
		boolean bSuccess = false;
		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		int iCount = 0;
		
		try
		{
			//Collects all maps
			sql = "SELECT * FROM RiverRaceCheckPoints";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Count of RiverRaceCheckPoints]: "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no map is found, program will notify thing
			if (bSuccess == false)
			{
				System.out.println("No checkpoints found");
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
	
	public void GetLocations(World world)
	{		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		int i;
		
		//Counts total amount of locations for this checkpoint first
		int iCount = countLocations(this.iCheckPointID);
		
		//Initiates the int array for storing MapIDs with length of the amount of maps just found
		this.locations = new Location[iCount];
		
		//If no locations were found, skip collecting the list
		if (iCount == 0)
		{
			return;
		}
		
		try
		{
			//Collects all locations
			sql = "SELECT * FROM RiverRaceCheckpointBlocks Where CheckPointID = "+this.iCheckPointID;
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Select * From RiverRaceCheckpointBlocks]: "+sql);
			
			//Executes the query
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			
			//Moves the curser to the next line
			resultSet.next();
						
			//Checks that there is only 1 record returned
			for (i = 0 ; i < iCount ; i++)
			{
				this.locations[i] = new Location(world, resultSet.getDouble("X"), resultSet.getDouble("Y"), resultSet.getDouble("Z"));
				resultSet.next();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}	
	}
	
	//Counts the amount of checkpoints
	public static int countLocations(int iCheckPointID)
	{
		boolean bSuccess = false;
		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		int iCount = 0;
		
		try
		{
			//Collects all maps
			sql = "SELECT * FROM RiverRaceCheckpointBlocks WHERE CheckPointID = "+iCheckPointID;
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Count of RiverRaceCheckPoints]: "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no map is found, program will notify thing
			if (bSuccess == false)
			{
				System.out.println("No checkpoints found");
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
	
	public static boolean deleteCheckpoint(int iCheckPointID)
	{
		int iCount = -1;
		int iCount2 = -1;
		String sql;
		Statement SQL = null;
		
		try
		{
			//Delete all the checkpoint pass locations
			sql = "Delete FROM RiverRaceCheckpointBlocks WHERE CheckPointID = \""+iCheckPointID +"\"";
			
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Delete from RiverRaceStartGrids]: "+sql);
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			iCount = SQL.executeUpdate(sql);
			
			//Delete the checkpoint
			sql = "Delete FROM RiverRaceCheckPoints WHERE CheckPointID = \""+iCheckPointID +"\"";
			
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Delete from RiverRaceStartGrids]: "+sql);
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			iCount2 = SQL.executeUpdate(sql);
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return (iCount2 == 1 && iCount >=0);
	}
}

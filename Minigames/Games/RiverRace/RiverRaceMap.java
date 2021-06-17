package Minigames.Games.RiverRace;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.World;

import Minigames.minigamesMain;

public class RiverRaceMap
{
	private int iMapID;
	private String szLocation;
	private World mapWorld;
	private String szMapWorld;
	
	public RiverRaceMap()
	{
		
	}
	
	public RiverRaceMap(String szLocation, String szMapWorld)
	{
		this.szLocation = szLocation;
		this.szMapWorld = szMapWorld;
	}
	
	public RiverRaceMap(String szLocation)
	{
		this.szLocation = szLocation;
	}
	
	public RiverRaceMap(int iMapID)
	{
		this.iMapID = iMapID;
	}
	
	public int getMapID()
	{
		return iMapID;
	}
	
	public String getLocation()
	{
		return szLocation;
	}
	
	public World getWorld()
	{
		return mapWorld;
	}
	
	//SQL setters
	public void setMapFromMapID()
	{
		boolean bSuccess = false;
		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		try
		{
			//Collects all fields for the specified EID
			sql = "SELECT * FROM "+minigamesMain.getInstance().RiverRaceMaps +" WHERE MapID = \""+this.iMapID +"\"";
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no user is found, program will notify thing
			if (bSuccess == false)
			{
				System.out.println("[Minigames] [RiverRaceMaps] [DB] Setting preferences from MapID: No map found with MapID "+this.iMapID);
			}
			//Checks that there is only 1 record returned
			else if (resultSet.next() == false)
			{
				//Runs if there is no second record
				//Colects results again
				resultSet = SQL.executeQuery(sql);
				resultSet.next();
				
				//Stores results into the object
				this.szLocation = resultSet.getString("Location");
				this.mapWorld = Bukkit.getWorld(resultSet.getString("MapWorld"));
			}
			else
			{
				System.out.println("Setting preferences from MapID: More than one map found");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setMapIDFromName()
	{
		boolean bSuccess = false;
		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		try
		{
			//Collects all fields for the specified EID
			sql = "SELECT * FROM "+minigamesMain.getInstance().RiverRaceMaps +" WHERE Location = \""+this.szLocation +"\"";
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no user is found, program will notify thing
			if (bSuccess == false)
			{
				System.out.println("[Minigames] [RiverRaceMaps] [DB] Setting MapID from Location: No map found with Location "+this.szLocation);
			}
			//Checks that there is only 1 record returned
			else if (resultSet.next() == false)
			{
				//Runs if there is no second record
				//Colects results again
				resultSet = SQL.executeQuery(sql);
				resultSet.next();
				
				//Stores results into the object
				this.iMapID = resultSet.getInt("MapID");
			}
			else
			{
				System.out.println("Setting MapID from Location: More than one map found");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//Collects list of River Race mapIDs
	public static int[] MapIDs()
	{
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		int i;
		
		//Counts total amount of maps first
		int iCount = count();
		
		//Initiates the int array for storing MapIDs with length of the amount of maps just found
		int[] iMapIDs = new int[iCount];
		
		//If no maps were found, skip collecting the list
		if (iCount == 0)
		{
			return iMapIDs;
		}
		
		try
		{
			//Collects all maps
			sql = "SELECT * FROM "+minigamesMain.getInstance().RiverRaceMaps;
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Select * From RiverRaceMaps]: "+sql);
			
			//Executes the query
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			
			//Moves the curser to the next line
			resultSet.next();
						
			//Checks that there is only 1 record returned
			for (i = 0 ; i < iCount ; i++)
			{
				iMapIDs[i] = resultSet.getInt("MapID");
				resultSet.next();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return iMapIDs;
	}
	
	//Counts the amount of maps
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
			sql = "SELECT * FROM "+minigamesMain.getInstance().RiverRaceMaps;
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Count of RiverRaceMaps]: "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no map is found, program will notify thing
			if (bSuccess == false)
			{
				System.out.println("No maps found");
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
	
	//Insert new map
	public boolean addMap()
	{
		boolean bSuccess = false;
		int iCount = -1;
		String sql;
		
		Statement SQL = null;
		
		try
		{
			//Compiles the command to add the new user
			sql = "INSERT INTO `"+minigamesMain.getInstance().RiverRaceMaps
					+"` (`Location`, `MapWorld`)"
					+ " VALUES("
					+ "\""+szLocation+"\", "
					+ "\""+szMapWorld+"\");";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Add to RiverRaceMaps]: "+sql);
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
	
	//Delete map
	public int deleteMap()
	{
		int iCount = -1;
		String sql;
		
		Statement SQL = null;
		
		//Deletes all the start grids for the map
		RiverRaceStartGrid.deleteAllForMap(this.iMapID);
		this.setMapFromMapID();
		
		//Delete the checkpoints
		RiverRaceCheckpoint[] CheckPoints = RiverRaceCheckpoint.getAllForMapID(this.iMapID, this.getWorld());
		for (int i = 0 ; i < CheckPoints.length ; i++)
		{
			RiverRaceCheckpoint.deleteCheckpoint(CheckPoints[i].getCheckpointID());
		}
		
		try
		{
			//Compiles the command to add the new user
			sql = "DELETE FROM `"+minigamesMain.getInstance().RiverRaceMaps +"` Where Location = \""+szLocation+"\"";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Deleting a River Race map]: "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			
			//Executes the update and returns the amount of records updated
			iCount = SQL.executeUpdate(sql);
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return iCount;
	}
}

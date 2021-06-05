package Minigames.Games.RiverRace;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import Minigames.minigamesMain;

public class RiverRaceStartGrid
{
	private int GridID;
	private int MapID;
	private boolean left;
	private int positionFromCentre;
	private Double X;
	private Double Y;
	private Double Z;
	
	public RiverRaceStartGrid(int iMapID)
	{
		this.MapID = iMapID;
	}
	
	public RiverRaceStartGrid(int iMapID, boolean bLeft, int PositionFromCentre, Location location)
	{
		this.MapID = iMapID;
		this.left = bLeft;
		this.positionFromCentre = PositionFromCentre;
		this.X = location.getX();
		this.Y = location.getY();
		this.Z = location.getZ();
	}
	
	//Getters
	public int getGridID()
	{
		return this.GridID;
	}
	public int getMapID()
	{
		return this.MapID;
	}
	public boolean getLeft()
	{
		return this.left;
	}
	public int getPositionFromCentre()
	{
		return this.positionFromCentre;
	}
	public Double getX()
	{
		return X;
	}
	public Double getY()
	{
		return Y;
	}
	public Double getZ()
	{
		return Z;
	}
	
	//SQL fetches
	public static RiverRaceStartGrid[] allStartGrids(int iMapID)
	{
		RiverRaceStartGrid[] StartGrids;
		
		int iCount = 0;
		
		boolean bSuccess = false;
		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		try
		{
			//Collects all fields for the specified EID
			sql = "SELECT * FROM RiverRaceStartGrids WHERE MapID = \""+iMapID +"\"";
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no user is found, program will notify thing
			if (bSuccess == false)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceStartGrids] Fetching start grids from MapID: No map found with MapID "+iMapID);
				StartGrids = new RiverRaceStartGrid[0];
				return StartGrids;
			}
			else
			{
				iCount++;
			}
			
			//Counts the amount of StartGrids
			while (resultSet.next() != false)
			{
				iCount++;
			}
			
			StartGrids = new RiverRaceStartGrid[iCount];

			resultSet = SQL.executeQuery(sql);
			
			//Moves the cursor to the first row
			resultSet.next();
			
			//Checks that there is only 1 record returned
			for (int i = 0 ; i < iCount ; i++)
			{
				StartGrids[i] = new RiverRaceStartGrid(iMapID);
				StartGrids[i].GridID = resultSet.getInt("GridID");
				StartGrids[i].left = resultSet.getBoolean("Left");
				StartGrids[i].positionFromCentre = resultSet.getInt("PositionFromCentre");
				StartGrids[i].X = resultSet.getDouble("PositionX");
				StartGrids[i].Y = resultSet.getDouble("PositionY");
				StartGrids[i].Z = resultSet.getDouble("PositionZ");
				//Moves the cursor on
				resultSet.next();
			}
		}
		catch (SQLException e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceStartGrids] Fetching start grids from MapID: SQL Error occured"+iMapID);
			e.printStackTrace();
			StartGrids = new RiverRaceStartGrid[0];
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceStartGrids] Fetching start grids from MapID: SQL Error occured"+iMapID);
			e.printStackTrace();
			StartGrids = new RiverRaceStartGrid[0];
		}
		return StartGrids;
	}
	
	//Insert new grid
	public boolean addGrid()
	{
		boolean bSuccess = false;
		int iCount = -1;
		String sql;
		
		Statement SQL = null;
		
		int left;
		if (this.left)
			left = 1;
		else
			left = 0;
		
		try
		{
			//Compiles the command to add the new user
			sql = "INSERT INTO `RiverRaceStartGrids`"
					+" (`MapID`, `Left`, `PositionFromCentre`, `PositionX`, `PositionY`, `PositionZ`)"
					+ " VALUES("
					+ "\""+MapID+"\", "
					+ "\""+left+"\", "
					+ "\""+positionFromCentre+"\", "
					+ "\""+X+"\", "
					+ "\""+Y+"\", "
					+ "\""+Z+"\""
							+ ");";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceStartGrids] [Add to RiverRaceStartGrids]: "+sql);
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
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceStartGrids] Add to RiverRaceStartGrids - SQL Error occured");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceStartGrids] Add to RiverRaceStartGrids - Error occured");
			e.printStackTrace();
		}
		return bSuccess;
	}
	
	public static int deleteAllForMap(int iMapID)
	{		
		int iCount = -1;
		String sql;
		Statement SQL = null;
		
		try
		{
			//Collects all fields for the specified EID
			sql = "Delete FROM RiverRaceStartGrids WHERE MapID = \""+iMapID +"\"";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceStartGrids] Delete all from RiverRaceStartGrids: "+sql);
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			iCount = SQL.executeUpdate(sql);
									
		}
		catch (SQLException e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceStartGrids] Delete all from RiverRaceStartGrids - SQL Error occured");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceStartGrids] Delete all from RiverRaceStartGrids - Error occured");
			e.printStackTrace();
		}
		return iCount;
	}
	
	public static int delete(int iGridID)
	{
		int iCount = -1;
		String sql;
		Statement SQL = null;
		
		try
		{
			//Collects all fields for the specified EID
			sql = "Delete FROM RiverRaceStartGrids WHERE GridID = \""+iGridID +"\"";
			
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceStartGrids] Delete from RiverRaceStartGrids]: "+sql);
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			iCount = SQL.executeUpdate(sql);
		}
		catch (SQLException e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceStartGrids] Delete from RiverRaceStartGrids - SQL Error occured");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RiverRaceStartGrids] Delete from RiverRaceStartGrids - Error occured");
			e.printStackTrace();
		}
		return iCount;
	}
}

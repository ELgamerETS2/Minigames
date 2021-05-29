package Minigames;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

public class MainLobby
{	
	private String szName;
	private int iVersion;
	private boolean bActive;
	private int X;
	private int Y;
	private int Z;
	private String szWorldName;
	
	private int iHidePlatformX;
	private int iHidePlatformY;
	private int iHidePlatformZ;
	
	private int RiverRaceX;
	private int RiverRaceY;
	private int RiverRaceZ;
	
	public MainLobby()
	{
		
	}
	
	public MainLobby(minigamesMain CorePlugin)
	{		
		MainLobby[] ActiveLobbies = allActiveLobbies();
		int iTotalLobbies = ActiveLobbies.length;
		
		Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL Lobbies] Active Lobbies Found = " +iTotalLobbies);
		
		if (iTotalLobbies == 0)
		{
			this.setSzName(CorePlugin.getConfig().getString("EmergencyLobbyName"));
			this.iVersion = CorePlugin.getConfig().getInt("EmergencyLobbyVersion");
			this.bActive = true;
			this.setX(CorePlugin.getConfig().getInt("EmergencyLobbyX"));
			this.setY(CorePlugin.getConfig().getInt("EmergencyLobbyY"));
			this.setZ(CorePlugin.getConfig().getInt("EmergencyLobbyZ"));
			this.setWorldName(CorePlugin.getConfig().getString("EmergencyLobbyWorldName"));
			this.iHidePlatformX = CorePlugin.getConfig().getInt("HideX");
			this.iHidePlatformY = CorePlugin.getConfig().getInt("HideY");
			this.iHidePlatformZ = CorePlugin.getConfig().getInt("HideZ");
			this.RiverRaceX = CorePlugin.getConfig().getInt("RiverRaceX");
			this.RiverRaceY = CorePlugin.getConfig().getInt("RiverRaceY");
			this.RiverRaceZ = CorePlugin.getConfig().getInt("RiverRaceZ");
		}
		else
		{
			int iRandom = ( (int) (Math.random() * iTotalLobbies) );
						
			this.setSzName(ActiveLobbies[iRandom].getSzName());
			this.iVersion = ActiveLobbies[iRandom].iVersion;
			this.bActive = ActiveLobbies[iRandom].bActive;
			this.setX(ActiveLobbies[iRandom].getX());
			this.setY(ActiveLobbies[iRandom].getY());
			this.setZ(ActiveLobbies[iRandom].getZ());
			this.setWorldName(ActiveLobbies[iRandom].getWorldName());
			this.iHidePlatformX = ActiveLobbies[iRandom].getiHidePlatformX();
			this.iHidePlatformY = ActiveLobbies[iRandom].getiHidePlatformY();
			this.iHidePlatformZ = ActiveLobbies[iRandom].getiHidePlatformZ();
			this.RiverRaceX = ActiveLobbies[iRandom].getRiverRaceX();
			this.RiverRaceY = ActiveLobbies[iRandom].getRiverRaceY();
			this.RiverRaceZ = ActiveLobbies[iRandom].getRiverRaceZ();
		}
		
		Bukkit.getConsoleSender().sendMessage("[Minigames] [Lobby] Lobby set as "+szName +" version "+iVersion);
	}
	
	//Setters
	public void setSzName(String szName)
	{
		this.szName = szName;
	}
	
	public void setX(int x)
	{
		X = x;
	}
	
	public void setY(int y)
	{
		Y = y;
	}
	
	public void setZ(int z)
	{
		Z = z;
	}
	public void setWorldName(String szWorldName)
	{
		this.szWorldName = szWorldName;
	}
	
	//Getters
	public int getX()
	{
		return X;
	}
	public int getY()
	{
		return Y;
	}
	public int getZ()
	{
		return Z;
	}
	
	public String getWorldName()
	{
		return szWorldName;
	}
	public String getSzName()
	{
		return szName;
	}
	
	public int getiHidePlatformX()
	{
		return iHidePlatformX;
	}
	public int getiHidePlatformY()
	{
		return iHidePlatformY;
	}
	public int getiHidePlatformZ()
	{
		return iHidePlatformZ;
	}
	
	public int getRiverRaceX()
	{
		return RiverRaceX;
	}
	public int getRiverRaceY()
	{
		return RiverRaceY;
	}
	public int getRiverRaceZ()
	{
		return RiverRaceZ;
	}
	
	//Collects list of all active lobbies
	public static MainLobby[] allActiveLobbies()
	{
		MainLobby[] activeLobbies;
		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		int i;
		
		//Counts total amount of hdie and seek maps first
		int iCount = count();
		
		//Initiates the MainLobby array for storing MainLobbies with length of the amount of lobbies just found
		activeLobbies = new MainLobby[iCount];
		
		//If no lobbies were found, skip collecting the list
		if (iCount == 0)
		{
			return activeLobbies;
		}
		
		try
		{
			//Collects all active lobbies
			sql = "SELECT * FROM "+minigamesMain.getInstance().MainLobbies +" WHERE Active = 1";
			
			//Executes the query
			SQL = minigamesMain.getInstance().connection.createStatement();
			resultSet = SQL.executeQuery(sql);
			
			//Moves the curser to the next line
			resultSet.next();
			
			//Collects details of each lobby
			for (i = 0 ; i < iCount ; i++)
			{
				activeLobbies[i] = new MainLobby();
				activeLobbies[i].setSzName(resultSet.getString("Name"));
				activeLobbies[i].iVersion = resultSet.getInt("Version");
				activeLobbies[i].bActive = resultSet.getBoolean("Active");
				activeLobbies[i].setX(resultSet.getInt("LocationX"));
				activeLobbies[i].setY(resultSet.getInt("LocationY"));
				activeLobbies[i].setZ(resultSet.getInt("LocationZ"));
				activeLobbies[i].setWorldName(resultSet.getString("WorldName"));
				activeLobbies[i].iHidePlatformX = resultSet.getInt("HidePlatformX");
				activeLobbies[i].iHidePlatformY = resultSet.getInt("HidePlatformY");
				activeLobbies[i].iHidePlatformZ = resultSet.getInt("HidePlatformZ");
				activeLobbies[i].RiverRaceX = resultSet.getInt("RiverRaceX");
				activeLobbies[i].RiverRaceY = resultSet.getInt("RiverRaceY");
				activeLobbies[i].RiverRaceZ = resultSet.getInt("RiverRaceZ");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return activeLobbies;
	}
	
	//Counts the amount of active lobbies
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
			sql = "SELECT * FROM "+minigamesMain.getInstance().MainLobbies + " WHERE Active = 1";
			
			SQL = minigamesMain.getInstance().connection.createStatement();
			resultSet = SQL.executeQuery(sql);
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no map is found, program will notify thing
			if (bSuccess == false)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL Lobbies] No lobbies found");
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
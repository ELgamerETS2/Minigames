package Minigames;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import Minigames.Games.Gametype;
import Minigames.Games.HideAndSeek.HideAndSeekLobby;
import Minigames.Games.RiverRace.RiverRaceLobby;
import Minigames.commands.HSBJoin;
import Minigames.commands.HSMap;
import Minigames.commands.Mystats;
import Minigames.commands.RRGrid;
import Minigames.commands.RRMap;
import Minigames.commands.RiverRace;
import Minigames.gui.LobbyGUI;
import Minigames.listeners.InventoryClicked;
import Minigames.listeners.JoinEvent;
import Minigames.listeners.PlayerInteract;
import Minigames.statistics.UpdateCall;

public class minigamesMain extends JavaPlugin
{
	String sql;
	
	Statement SQL = null; 
	ResultSet resultSet = null;
	
	static minigamesMain instance;
	static FileConfiguration config;
	
	private String HOST;
	private int PORT;
	public String Database;
	private String USER;
	private String PASSWORD;
	
	public String HideAndSeekMaps;
	public String RiverRaceMaps;
	public String CastleDefenceMaps;
	public String GAMES;
	public String STATS;
	
	public String MainLobbies;
	
	public MainLobby MainLobby;
	public HideAndSeekLobby HSLobby;
	public RiverRaceLobby RRLobby;
	
	public Plugin plugin;
	
	public Connection connection = null;
    
	boolean bIsConnected;
	
	private String DB_CON;
	
	public ItemStack slot9;
	public static ItemStack gui;
	
	@Override
	public void onEnable()
	{
		//Config Setup
		minigamesMain.instance = this;
		minigamesMain.config = this.getConfig();
		saveDefaultConfig();
		
		//MySQL
		boolean bSuccess;
		bIsConnected = false;
		
		//Attempt set up from config and connect
		mySQLSetup();
		bSuccess = connect();
		
		//Test whether database connected
		if (bSuccess)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Minigames] MySQL Connected");
			bIsConnected = true;
			
			//Only creates tables if database connected properly
			
			//Creates the MySQL table if not already exists
			// - Order is important
			
			//Hide and seek Maps
			createHideAndSeekMapsTable();
			
			//Castle Defence Maps
		//	createHideAndSeekMapsTable();
			
			//Games
			createGamesTable();
			
			//Stats
			createStatsTable();
			
			//Lobbies
			createLobbiesTable();
		}
		
		//--------------------------------------
		//------------Create lobbies------------
		//--------------------------------------
		
		MainLobby = new MainLobby(this);
		//Attempts to load world
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv import " +MainLobby.getWorldName() +" normal");
		
		HSLobby = new HideAndSeekLobby(this);
		RRLobby = new RiverRaceLobby(this);
		
		//---------------------------------------
		//--------------Create GUIs--------------
		//---------------------------------------
		
		LobbyGUI.initialize();
		
		//Create gui item				
		gui = new ItemStack(Material.ARROW);
		ItemMeta meta = gui.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Minigames Menu");
		gui.setItemMeta(meta);
		
		//1 second timer - updates slot
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {

				for (Player p : Bukkit.getOnlinePlayers())
				{
					slot9 = p.getInventory().getItem(8);
					if (slot9 == null)
					{
						p.getInventory().setItem(8, gui);
					}
					else if (!slot9.equals(gui))
					{
						p.getInventory().setItem(8, gui);
					}
				}
			}
		}, 0L, 20L);
		
		//---------------------------------------
		//---------------Listeners---------------
		//---------------------------------------
		
		//Handles welcome message and gamemode
		new JoinEvent(this);
		new PlayerInteract(this);
		new InventoryClicked(this);
		
		//--------------------------------------
		//---------------Commands---------------
		//--------------------------------------
		
		//Handles viewing own stats
		getCommand("mystats").setExecutor(new Mystats());
		
		//Handles joining hide and seek lobby
		getCommand("hide").setExecutor(new HSBJoin());
				
		//Allows Map-Makers to manage Hide and Seek maps
		getCommand("hsmap").setExecutor(new HSMap());
		
		//Allows Game-Makers to manually start and end River Race games
		getCommand("rr").setExecutor(new RiverRace());
		
		//Allows Map-Makers to manage River Race maps
		getCommand("rrmap").setExecutor(new RRMap());
		
		//Allows Map-Makers to manage River Race start points
		getCommand("rrgrid").setExecutor(new RRGrid());
		
		
		//--------------------------------------
		//-------------Stats Update-------------
		//--------------------------------------
		
		long lMinute = 1200L;
		
		//Schedule the stats to change
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			public void run()
			{
				UpdateCall up = new UpdateCall(instance);
				up.run();
			}
		}, 0L, lMinute * config.getInt("StatisticsSignUpdates"));
	}
	
	@Override
	public void onDisable()
	{
		//Where stats archive occures
		
		disconnect();
	}
	
	public void mySQLSetup()
	{
		this.HOST = config.getString("MySQL_host");
		this.PORT = config.getInt("MySQL_port");
		this.Database = config.getString("MySQL_database");
		this.USER = config.getString("MySQL_username");
		this.PASSWORD = config.getString("MySQL_password");
		
		this.HideAndSeekMaps = config.getString("MySQL_HideAndSeekMaps");
		this.RiverRaceMaps = config.getString("MySQL_RiverRaceMaps");
		this.CastleDefenceMaps = config.getString("MySQL_CastleDefenceMaps");
		
		this.GAMES = config.getString("MySQL_games");
		this.STATS = config.getString("MySQL_stats");
		this.MainLobbies = config.getString("MySQL_lobbies");
		this.DB_CON = "jdbc:mysql://" + this.HOST + ":" 
				+ this.PORT + "/" + this.Database;
	}
	
	public boolean connect() 
	{
		try
		{
		//	System.out.println(this.getClass().getName() +" : Connecting la la la");
			DriverManager.getDriver(DB_CON);
			connection = DriverManager.getConnection(DB_CON, USER, PASSWORD);
			if (this.connection != null)
				return true;
			else
				return false;
		}
		catch (SQLException e)
		{
			if (e.toString().contains("Access denied"))
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] - [minigamesMain] - Access denied");			
				System.out.println("Access denied");
			}
			else if (e.toString().contains("Communications link failure"))
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] - [minigamesMain] - Communications link failure");			
				System.out.println("Communications link failure");
			}
			else
			{
				e.printStackTrace();
				System.out.println(e.toString());
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] - [minigamesMain] - Other SQLException - "+e.getMessage());			
			}
			return false;
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] - [minigamesMain] - Other Exception whilst connecting to database- "+e.getMessage());			
			return false;
		}
	}
	
	public void disconnect()
	{
		try
		{
			if (bIsConnected)
			{
				this.connection.close() ;
				this.bIsConnected = false ;
			}
		//	System.err.println( this.getClass().getName() + ":: disconnected." ) ;
		}
		catch ( SQLException se )
		{
			System.err.println( this.getClass().getName() + ":: SQL error " + se ) ;
			se.printStackTrace() ;
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] - [minigamesMain] - SQLException - closing connection");			
		}
		catch ( Exception e )
		{
			System.err.println( this.getClass().getName() + ":: error " + e ) ;
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] - [minigamesMain] - Exception - closing connection");			
			e.printStackTrace() ;
		}
	}
	
	public boolean createHideAndSeekMapsTable()
	{
		boolean bSuccess = false;
		int iCount = -1;

		try
		{
			//Adds a weather pref table
			sql = "CREATE TABLE IF NOT EXISTS `"+this.Database+"`.`"+this.HideAndSeekMaps+"` (\n" + 
					"  `MapID` INT NOT NULL AUTO_INCREMENT,\n" + 
					"  `Location` VARCHAR(45) NOT NULL,\n" + 
					"  `Creator` VARCHAR(45) NOT NULL,\n" + 
					"  `MapWorld` VARCHAR(45) NOT NULL,\n" + 
					"  `StartX` INT NOT NULL,\n" + 
					"  `StartY` INT NOT NULL,\n" + 
					"  `StartZ` INT NOT NULL,\n" + 
					"  `Wait` INT NOT NULL,\n" + 
					"   PRIMARY KEY (`MapID`));";
			SQL = connection.createStatement();

			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created Hide and Seek Maps table");			
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
		return (bSuccess);
	}
	
	public boolean createRiverRaceMapsTable()
	{
		boolean bSuccess = false;
		int iCount = -1;

		try
		{
			//Adds a weather pref table
			sql = "CREATE TABLE IF NOT EXISTS `"+this.Database+"`.`"+this.RiverRaceMaps+"` (\n" + 
					"  `MapID` INT NOT NULL AUTO_INCREMENT,\n" + 
					"  `Location` VARCHAR(45) NOT NULL,\n" + 
					"  `MapWorld` VARCHAR(45) NOT NULL,\n" + 
					"   PRIMARY KEY (`MapID`));";
			SQL = connection.createStatement();

			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created Hide and Seek Maps table");			
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
		return (bSuccess);
	}
	
	public boolean createRiverRaceStartGridsTable()
	{
		boolean bSuccess = false;
		int iCount = -1;

		try
		{
			//Adds a weather pref table
			sql = "CREATE TABLE IF NOT EXISTS `"+this.Database+"`.`RiverRaceStartGrids` (\n" + 
					"  `GridID` INT NOT NULL AUTO_INCREMENT,\n" + 
					"  `MapID` VARCHAR(45) NOT NULL,\n" + 
					"  `MapWorld` VARCHAR(45) NOT NULL,\n" + 
					"   PRIMARY KEY (`MapID`));";
			SQL = connection.createStatement();

			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created Hide and Seek Maps table");			
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
		return (bSuccess);
	}
	
	//Creates games table in database
	public boolean createGamesTable()
	{
		boolean bSuccess = false;
		int iCount = -1;

		try
		{
			//Adds a weather pref table
			sql = "CREATE TABLE IF NOT EXISTS `"+this.Database+"`.`"+this.GAMES+"` (\n" + 
					"  `GameID` INT NOT NULL AUTO_INCREMENT,\n" + 
					"  `GameType` ENUM('"+Gametype.Hide_And_Seek+"', '"+Gametype.Castle_Defence +"') NOT NULL,\n" + 
					"  `MapID` INT NOT NULL,\n" + 
					"  `TimeStart` TIMESTAMP NOT NULL,\n" + 
					"  `TimeEnd` TIMESTAMP NULL DEFAULT NULL,\n" + 
					"   PRIMARY KEY (`GameID`))"
				+	";";

			SQL = connection.createStatement();

			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created minigames Games table");			
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
		return (bSuccess);
	}
	
	//Creates stats table in database
	public boolean createStatsTable()
	{
		boolean bSuccess = false;
		int iCount = -1;

		try
		{
			//Adds a weather pref table
			sql = "CREATE TABLE IF NOT EXISTS `"+this.Database+"`.`"+this.STATS+"` (\n" + 
					"  `UUID` VARCHAR(36) NOT NULL,\n" + 
					"  `GameID` INT NOT NULL,\n" + 
					"  `Points` Int NOT NULL,\n" + 
					"   PRIMARY KEY (`UUID`,`GameID`));";
			SQL = connection.createStatement();

			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created minigames Stats table");			
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
		return (bSuccess);
	}
	
	//Creates stats table in database
	public boolean createLobbiesTable()
	{
		boolean bSuccess = false;
		int iCount = -1;

		try
		{
			//Adds a weather pref table
			sql = "CREATE TABLE IF NOT EXISTS `"+this.Database+"`.`Lobbies` (\n" +
					"`Name` varchar(36) NOT NULL,\n" + 
					"  `Version` int NOT NULL,\n" + 
					"  `Active` tinyint NOT NULL,\n" + 
					"  `LocationX` int NOT NULL,\n" + 
					"  `LocationY` int NOT NULL,\n" + 
					"  `LocationZ` int NOT NULL,\n" + 
					"  `WorldName` varchar(36) NOT NULL,\n" + 
					"  `HidePlatformX` int NOT NULL,\n" + 
					"  `HidePlatformY` int NOT NULL,\n" + 
					"  `HidePlatformZ` int NOT NULL,\n" + 
					"  PRIMARY KEY (`Name`,`Version`));";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] " +ChatColor.AQUA + "");			
			SQL = connection.createStatement();
			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created minigames Lobbies table");			
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
		return (bSuccess);
	}
	
	public static minigamesMain getInstance()
	{
		return instance;
	}
	
	public Connection getConnection()
	{
		try
		{
			if(connection.isClosed() || connection == null)
			{
				connect();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return connection;
	}
}

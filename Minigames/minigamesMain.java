package Minigames;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
import Minigames.Games.RiverRace.RRSelection;
import Minigames.Games.RiverRace.RiverRaceLobby;
import Minigames.commands.HSBJoin;
import Minigames.commands.HSMap;
import Minigames.commands.Lobby;
import Minigames.commands.Mystats;
import Minigames.commands.RRCheck;
import Minigames.commands.RRGrid;
import Minigames.commands.RRMap;
import Minigames.commands.RiverRace;
import Minigames.commands.Wand;
import Minigames.gui.HideStatsGUI;
import Minigames.gui.MenuGUI;
import Minigames.gui.RiverRaceStatsGUI;
import Minigames.gui.StatsGUI;
import Minigames.gui.Utils;
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
	
	private Connection connection = null;
    
	boolean bIsConnected;
	
	private String DB_CON;
	
	public ItemStack slot9;
	public static ItemStack menu;
	
	public ItemStack slot8;
	public static ItemStack stats;
	
	public ArrayList<RRSelection> selections;
	
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
			
			//River Race Maps
			createRiverRaceMapsTable();
			
			//River Race Checkpoints
			createCheckpointsTable();
			
			//Rive Race Checkpoint blocks
			createCheckpointBlocksTable();
			
			//River Race Start Grids
			createRiverRaceStartGridsTable();
			
			//Games
			createGamesTable();
			
			//Stats
			createStatsTable();
			
			//River Race Times
			createRiverRaceTimesTable();
			
			//Lobbies
			createLobbiesTable();
		}
		
		//--------------------------------------
		//------------Create lobbies------------
		//--------------------------------------
		
		MainLobby = new MainLobby(this);
		//Attempts to load world
	//	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv import " +MainLobby.getWorldName() +" normal");
		
		HSLobby = new HideAndSeekLobby(this);
		RRLobby = new RiverRaceLobby(this);
		
		//---------------------------------------
		//--------------Create GUIs--------------
		//---------------------------------------
		
		MenuGUI.initialize();
		StatsGUI.initialize();
		HideStatsGUI.initialize();
		RiverRaceStatsGUI.initialize();
		
		//Create menu item				
		menu = new ItemStack(Material.EMERALD);
		ItemMeta meta = menu.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Minigames Menu");
		menu.setItemMeta(meta);
		
		//1 second timer - updates slot
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run()
			{
				for (Player p : Bukkit.getOnlinePlayers())
				{
					//Menu
					slot9 = p.getInventory().getItem(8);
					if (slot9 == null)
					{
						p.getInventory().setItem(8, menu);
					}
					else if (!slot9.equals(menu))
					{
						p.getInventory().setItem(8, menu);
					}
					
					//Create stats item
					stats = Utils.createPlayerSkull(p, 1, 7, ChatColor.GREEN + "" + ChatColor.BOLD + "Stats");
					ItemMeta meta = stats.getItemMeta();
					meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Stats");
					stats.setItemMeta(meta);
					
					slot8 = p.getInventory().getItem(7);
					if (slot8 == null)
					{
						p.getInventory().setItem(7, stats);
					}
					else if (!slot8.equals(stats))
					{
						p.getInventory().setItem(7, stats);
					}
				}
			}
		}, 0L, 20L);
		
		//---------------------------------------
		//---------------Listeners---------------
		//---------------------------------------
		
		//Create selection tool list
		selections = new ArrayList<RRSelection>();
		
		//Handles welcome message and gamemode
		new JoinEvent(this);
		new PlayerInteract(this);
		new InventoryClicked(this);
		new Wand(this);
		
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
		
		//Allows Map-Makers to manage River Race checkpoints
		getCommand("rrcheck").setExecutor(new RRCheck());
		
		//Allows devs to manage lobbies
		getCommand("lobbys").setExecutor(new Lobby());
		
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
			}
			else if (e.toString().contains("Communications link failure"))
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] - [minigamesMain] - Communications link failure");			
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] - [minigamesMain] - Other SQLException - "+e.getMessage());
				e.printStackTrace();
			}
			return false;
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] - [minigamesMain] - Other Exception whilst connecting to database- "+e.getMessage());
			e.printStackTrace();
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
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Minigames] - [minigamesMain] - SQLException - closing connection");			
			se.printStackTrace() ;
		}
		catch ( Exception e )
		{
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
			
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL]: " + sql);
			
			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created HideAndSeekMaps table");			
				bSuccess = true;
			}
		}
		catch (SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] SQL Error adding HideAndSeekMaps Table");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] Error adding HideAndSeekMaps Table");
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
			
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL]: " + sql);
			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created RiverRaceMaps table");			
				bSuccess = true;
			}
		}
		catch (SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] SQL Error adding RiverRaceMaps Table");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] Error adding RiverRaceMaps Table");
			e.printStackTrace();
		}
		return (bSuccess);
	}
	
	public boolean createCheckpointsTable()
	{
		boolean bSuccess = false;
		int iCount = -1;

		try
		{
			//Adds a weather pref table
			sql = "CREATE TABLE IF NOT EXISTS `"+this.Database+"`.`RiverRaceCheckPoints` (\n" + 
					"  `CheckPointID` INT NOT NULL AUTO_INCREMENT,\n" + 
					"  `MapID` INT NOT NULL,\n" + 
					"  `Number` INT NOT NULL,\n" + 
					"   PRIMARY KEY (`CheckPointID`));";
			SQL = connection.createStatement();
			
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL]: " + sql);
			
			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created RiverRaceCheckPoints table");			
				bSuccess = true;
			}
		}
		catch (SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] SQL Error adding RiverRaceCheckPoints Table");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] Error adding RiverRaceCheckPoints Table");
			e.printStackTrace();
		}
		return (bSuccess);
	}
	
	public boolean createCheckpointBlocksTable()
	{
		boolean bSuccess = false;
		int iCount = -1;

		try
		{
			//Adds a weather pref table
			sql = "CREATE TABLE IF NOT EXISTS `"+this.Database+"`.`RiverRaceCheckpointBlocks` (\n" + 
					"  `CheckPointID` INT NOT NULL,\n" + 
					"  `X` DOUBLE NOT NULL,\n" +
					"  `Y` DOUBLE NOT NULL,\n" +
					"  `Z` DOUBLE NOT NULL,\n" +
					"  KEY `MapIDRef2_idx` (`CheckPointID`),\n" + 
					"  CONSTRAINT `MapIDRef2` FOREIGN KEY (`CheckPointID`) REFERENCES `RiverRaceCheckPoints` (`CheckPointID`)\n" + 
					");";
			SQL = connection.createStatement();
			
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL]: " + sql);
			
			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created RiverRaceCheckpointBlocks table");			
				bSuccess = true;
			}
		}
		catch (SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] SQL Error adding RiverRaceCheckpointBlocks Table");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] Error adding RiverRaceCheckpointBlocks Table");
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
					"  `MapID` INT NOT NULL,\n" + 
					"  `Left` TINYINT NOT NULL,\n" + 
					"  `PositionFromCentre` INT NOT NULL,\n" +
					"  `PositionX` DOUBLE NOT NULL,\n" +
					"  `PositionY` DOUBLE NOT NULL,\n" +
					"  `PositionZ` DOUBLE NOT NULL,\n" +
					"   PRIMARY KEY (`GridID`));";
			SQL = connection.createStatement();
			
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL]: " + sql);
			
			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created RiverRaceStartGrids table");			
				bSuccess = true;
			}
		}
		catch (SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] SQL Error adding RiverRaceStartGrids Table");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] Error adding RiverRaceStartGrids Table");
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
			
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL]: " + sql);
			
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
		catch (SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] SQL Error adding Games Table");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] Error adding Games Table");
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
			
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL]: " + sql);
			
			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created minigames Stats table");			
				bSuccess = true;
			}
		}
		catch (SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] SQL Error adding Stats Table");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] Error adding Stats Table");
			e.printStackTrace();
		}
		return (bSuccess);
	}
	
	public boolean createRiverRaceTimesTable()
	{

		boolean bSuccess = false;
		int iCount = -1;

		try
		{
			//Adds a weather pref table
			sql = "CREATE TABLE IF NOT EXISTS `MinigameStats`.`RiverRaceTimes` (\n" + 
					"				  `ScoreID` INT NOT NULL AUTO_INCREMENT,\n" + 
					"				  `GameID` INT NOT NULL,\n" + 
					"				  `Time` INT NOT NULL,\n" + 
					"				  `PlayerUUID` VARCHAR(36) NOT NULL ,\n" + 
					"				  PRIMARY KEY (`ScoreID`),\n" + 
					"				  INDEX `Game_idx` (`GameID` ASC) VISIBLE,\n" + 
					"				  CONSTRAINT `Game`\n" + 
					"				    FOREIGN KEY (`GameID`)\n" + 
					"				    REFERENCES `MinigameStats`.`Games` (`GameID`)\n" + 
					"				    ON DELETE NO ACTION\n" + 
					"				    ON UPDATE NO ACTION);";
			
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL]: " + sql);
			SQL = connection.createStatement();
			//Executes the update and returns how many rows were changed
			iCount = SQL.executeUpdate(sql);

			//If only 1 record was changed, success is set to true
			if (iCount == 1)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.AQUA + "Created RiverRaceTimes table");			
				bSuccess = true;
			}
		}
		catch (SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] SQL Error adding RiverRaceTimes Table");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] Error adding RiverRaceTimes Table");
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
					"  `LobbyID` INT NOT NULL AUTO_INCREMENT,\n" + 
					"  `Name` varchar(36) NOT NULL,\n" + 
					"  `Version` int NOT NULL,\n" + 
					"  `Active` tinyint NOT NULL,\n" + 
					"  `LocationX` int NOT NULL,\n" + 
					"  `LocationY` int NOT NULL,\n" + 
					"  `LocationZ` int NOT NULL,\n" + 
					"  `WorldName` varchar(36) NOT NULL,\n" + 
					"  `HidePlatformX` int NOT NULL,\n" + 
					"  `HidePlatformY` int NOT NULL,\n" + 
					"  `HidePlatformZ` int NOT NULL,\n" + 
					"  `RiverRaceX` int NOT NULL,\n" + 
					"  `RiverRaceY` int NOT NULL,\n" + 
					"  `RiverRaceZ` int NOT NULL,\n" + 
					"  PRIMARY KEY (`LobbyID`));";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL]: " + sql);
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
		catch (SQLException se)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] SQL Error adding Lobbies Table");
			se.printStackTrace();
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [SQL] Error adding Lobbies Table");
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

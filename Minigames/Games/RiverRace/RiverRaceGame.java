package Minigames.Games.RiverRace;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import Minigames.Announce;
import Minigames.minigamesMain;
import Minigames.Games.Game;
import Minigames.Games.Gametype;

public class RiverRaceGame
{
	//Stores the details of the game as in the Games table of the database
	public final Game game;

	//Used for access to the lobby and main plugin
	private final minigamesMain plugin;
	protected RiverRaceLobby RRL;

	//Stores the lists of players and racers
	private Player[] players;
	private ArrayList<RiverRaceRacer> racers = new ArrayList<RiverRaceRacer>();
	public ArrayList<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
	
	//Stores the map details
	private RiverRaceMap map;
	private RiverRaceCheckpoint[] DBCheckPoints;
	
	//Holds whether the game has been terminated
	public boolean bTerminate;
	public boolean bGamePlayStarted;
	
	//Scoreboard
	private ScoreboardManager SBM;
	private Scoreboard SB;
	private Objective objCheckpoints;
	
	//Listeners
	ArrayList<Checkpoint> ListenerCheckpoints;
	
	public RiverRaceGame(minigamesMain plugin, RiverRaceLobby RRL, Player[] players)
	{
		//Sets up the Game class
		this.game = new Game(Gametype.River_Race);

		this.plugin = plugin;
		this.RRL = RRL;
		
		this.players = players;
		
		bGamePlayStarted = false;
		bTerminate = false;
		
		ListenerCheckpoints = new ArrayList<Checkpoint>();
		
		//Scoreboard
		SBM = Bukkit.getScoreboardManager();
		SB = SBM.getNewScoreboard();
		objCheckpoints = SB.registerNewObjective("Checkpoints", "dummy");
		objCheckpoints.setDisplayName(ChatColor.BLUE +"Leaderboard");
		objCheckpoints.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void highGameProcesses()
	{
		int i;
		
		//Selects map
		int[] MapIDs = RiverRaceMap.MapIDs();
		map = new RiverRaceMap(MapIDs[(int) ( Math.random() * MapIDs.length)]);
		map.setMapFromMapID();
		
		DBCheckPoints = RiverRaceCheckpoint.getAllForMapID(map.getMapID(), map.getWorld());
		
		//Set up start grids
		RiverRaceStartGrid[] startGridsArray = RiverRaceStartGrid.allStartGrids(map.getMapID());
		ArrayList<RiverRaceStartGrid> startGrids = new ArrayList<RiverRaceStartGrid>();
		for (i = 0 ; i < startGridsArray.length ; i++)
		{
			startGrids.add(startGridsArray[i]);
		}
		
		//Declare variables
		int iRandom;
		RiverRaceStartGrid newGrid;
		Score newScore;
		
		//Set up racers
		for (i = 0 ; i < players.length ; i++)
		{
			//Get a random grid from the list
			iRandom = (int) (Math.random() * startGrids.size());
			newGrid = startGrids.get(iRandom);
			Bukkit.getConsoleSender().sendMessage(map.getWorld().getName());
			//Set up the location of this grid
			Location location = new Location(map.getWorld(), newGrid.getX(), newGrid.getY(), newGrid.getZ());

			//Create new racer. Pass the location into the newBoat method.
			RiverRaceRacer newRacer = new RiverRaceRacer(players[i], newBoat(location));
			racers.add(newRacer);
			
			//Add them to scoreboard
			newScore = objCheckpoints.getScore(players[i].getName());
			newScore.setScore(0);
		}

		//5 Second count down
		Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable()
		{
			int time = 5;

			@Override
			public void run()
			{
				//Terminates the process if the game has been terminated
				if (bTerminate)
				{
					return;
				}
				if (this.time == 0)
				{
					return;
				}
				Announce.announce(players, (ChatColor.GOLD +""+ ChatColor.BOLD +time));
				//	Announce.playNote(players, Sound.GLASS);		 
				this.time--;
			}
		}, 0L, 20L);
		
		//Starts gameplay
		Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				if (bTerminate)
				{
					return;
				}
				//Actual gameplay
				bGamePlayStarted = true;
				game();
			}
		}, 5 * 20L);

		
		if (bTerminate)
		{
			return;
		}
	}
	
	private void game()
	{
		int i;
		
		//Set speed of boats to 2D
		for (i = 0 ; i < racers.size() ; i++)
		{
			racers.get(i).increaseBoatSpeed(2D);
		}
		game.setTimeStart();
		game.storeGameInDatabase();
		game.getGameID();
		
		Checkpoint newCheck;
		
		for (i = 0 ; i < DBCheckPoints.length - 1 ; i++)
		{
			newCheck = new Checkpoint(plugin, this, racers, (i+1), DBCheckPoints[i].getLocations());
			ListenerCheckpoints.add(newCheck);
		}
		newCheck = new Checkpoint(plugin, this, racers, (i+1), DBCheckPoints[DBCheckPoints.length - 1].getLocations());
		ListenerCheckpoints.add(newCheck);
		
	}

	private Boat newBoat(Location location)
	{
		Boat boat = (Boat) location.getWorld().spawnEntity(location, EntityType.BOAT);
		boat.setMaxSpeed(0.0001D);
		return boat;
	}
	
	//----------------------------------------
	//-------Deals with players leaving-------
	//----------------------------------------
	public void playerLeave(Player player)
	{
		int i;
		for (i = 0 ; i < racers.size() ; i++)
		{
			if (racers.get(i).getUUID().equals(player.getUniqueId()))
				racers.remove(i);
		}
		
		if (racers.size() == 0)
		{
			terminate(TerminateType.Last_Player_Left);
		}
	}
	
	public void terminate(TerminateType Reason)
	{
		
	}
}
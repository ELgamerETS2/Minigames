package Minigames.Games.HideAndSeek;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import Minigames.Announce;
import Minigames.MainLobby;
import Minigames.minigamesMain;
import Minigames.statistics.Statistics;

public class HideAndSeekLobby
{
	//Essentials
	private minigamesMain CorePlugin;
	private final Location lobbyLocation;
	private ArrayList<Player> players;
		
	//Stores amount of finders wanted
	private int iFinders;
	
	private HideAndSeekMap Map;
	public HideAndSeekGame HideGame;
	
	public boolean gameIsRunning;
	private boolean gameIsStarting;
	int iTimer;
	boolean bTerminate;
	BukkitTask Task;
	
	public int getPlayers()
	{
		return players.size();
	}
	
	public HideAndSeekLobby(minigamesMain CorePlugin)
	{
		//Store plugin locally
		this.CorePlugin = CorePlugin;
		
		//Get lobby into a local variable
		MainLobby lobby = CorePlugin.MainLobby;
		
		System.out.println("name 1: " +lobby.getWorldName());
		
		World world = Bukkit.getWorld(lobby.getWorldName());
		
		if (world == null)
			System.out.println("World temp is null");
		else
			System.out.println("World temp is not null");
		
		String szCurrent = Bukkit.getWorlds().get(0).getName();
		
		System.out.println("Current world: "+szCurrent);
		
		//Set up location for hide lobby
		this.lobbyLocation = new Location(world, lobby.getiHidePlatformX()+0.5, lobby.getiHidePlatformY()+0.5, lobby.getiHidePlatformZ()+0.5);
		
		if (lobbyLocation.getWorld() == null)
			System.out.println("Lobby location world is null");
		else
			System.out.println("Lobby location world is not null");
		
		System.out.println("name 2: " +Bukkit.getWorld(lobby.getWorldName()).getName());
		//Create a list of players
		players = new ArrayList<Player>();
				
		//Announce
		Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide]" +ChatColor.GREEN +" Hide and seek lobby created");
		Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide]" +ChatColor.GREEN +" In world: "+lobbyLocation.getWorld().getName() +", X: "+lobbyLocation.getBlockX() + ", Y: "+lobbyLocation.getBlockY() + ", Z: "+lobbyLocation.getBlockZ());
		
		reset();
				
		Bukkit.getScheduler().runTaskTimer(this.CorePlugin, new Runnable()
		{
			@Override
			public void run()
			{
				if (players.size() >= CorePlugin.getConfig().getInt("HideMininumPlayers") && !gameIsRunning && !gameIsStarting)
				{
					gameStartCountdown();
				}
			}
		}, 0, 100L);
	}
	
	private void reset()
	{
		//Reset finders to 0
		this.iFinders = 0;
		
		//Create the map
		this.Map = new HideAndSeekMap();
		
		//Reset the booleans
		gameIsRunning = false;
		gameIsStarting = false;
		
		iTimer = 60;
	}
	
	//Handles player attmepting to join the hide and seek lobby
	public void playerJoinLobby(Player player)
	{
		//Kicks them out of River Race lobby
		CorePlugin.RRLobby.playerLeaveLobby(player);
		
		if (gameIsRunning)
		{
			HideGame.playerLeave(player);
		}
		
		//Tests whether player is already in the lobby
		if (players.contains(player))
		{
			playerLeaveLobby(player);
		}
		else
		{
			//Teleports player to location
			player.teleport(lobbyLocation);
			
			//Adds player to the list
			players.add(player);
			
			//Sends welcome message
			player.sendMessage(ChatColor.LIGHT_PURPLE +"Welcome to the Hide and Seek lobby");
			
			//Announces to all players that a player has joined the loobby
			Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.DARK_PURPLE +player.getDisplayName() +ChatColor.LIGHT_PURPLE +" has joined the Hide and Seek lobby"));
			
			//Announces the amount of players now in the lobby
			if (players.size() == 1)
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" player is now in the lobby"));
			else
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" players are now in the lobby"));
			
			//Resets players speeds
			player.setWalkSpeed((float) 0.2);
			player.setFlySpeed((float) 0.2);
		}
	}
	
	//Deals with players leaving the lobby
	public void playerLeaveLobby(Player player)
	{
		//Checks that they are in the lobby
		if (players.contains(player))
		{
			//Removes them
			players.remove(player);
			
			//Announces that they have left
			Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.DARK_PURPLE +player.getDisplayName() +ChatColor.LIGHT_PURPLE +" has left the Hide and Seek lobby"));
			
			//Announces the amount of players now in the lobby
			if (players.size() == 1)
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" player in lobby"));
			else
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" players in lobby"));
			
			player.sendMessage(ChatColor.LIGHT_PURPLE +"Leaving Hide and Seek lobby");
		}
		
		//If the game is running, calls the method in the game class to deal with them leaving
		if (gameIsRunning)
		{
			HideGame.playerLeave(player);
		}
		
		//Reset flight to stop vunerability of people leaving during the flight section
		player.setAllowFlight(false);
	}
	
	public void gameStartCountdown()
	{
		gameIsStarting = true;
		iTimer = 60;
		System.out.println("Game is starting, timer is 60");
		
		Task = Bukkit.getScheduler().runTaskTimer(this.CorePlugin, new Runnable()
		{
			@Override
			public void run()
			{
				if (gameIsRunning)
				{
					gameIsStarting = false;
					Bukkit.getScheduler().cancelTask(Task.getTaskId());
					return;
				}
				
				if (players.size() < CorePlugin.getConfig().getInt("HideMininumPlayers"))
				{
					gameIsStarting = false;
					Bukkit.getScheduler().cancelTask(Task.getTaskId());
					return;
				}
				
				if (iTimer == 0)
				{
					Announce.announce(players, ChatColor.LIGHT_PURPLE +"Game starting now");
					iTimer = 60;
					lobbyRun();
					Bukkit.getScheduler().cancelTask(Task.getTaskId());
					return;
				}
				else if (iTimer % 10 == 0)
				{
					Announce.announce(players, ChatColor.LIGHT_PURPLE +"Game starting in "+iTimer +" seconds");
				}
				else if (iTimer < 10)
				{
					Announce.announce(players, ChatColor.LIGHT_PURPLE +"Game starting in "+iTimer);
				}
				
				iTimer--;
			}
		}, 0, 20L);
	}
	
	//Triggers the game to start
	public void lobbyRun()
	{
		gameIsRunning = true;
		
		//Sets finders to 1
		iFinders = 1;
		
		//Creates a new game and returns the gameID
		HideGame = new HideAndSeekGame((Player[]) players.toArray(new Player[players.size()]), iFinders, Map.iMapID, this.CorePlugin, this);
		
		//Resets player list
		players = new ArrayList<Player>();
		
		gameIsStarting = false;
		
		//Starts the game running
		HideGame.highGameProcesses();
	}
	
	//Deals with the game finishing
	protected void gameFinished(Player[] exitingPlayers, ArrayList<HideAndSeekFinder> finders, boolean gameRan)
	{
		//Adds players leaving game back into lobby players
		//Resets their scoreboard
		for (Player player : exitingPlayers)
		{
			//Adds player to lobby
			playerJoinLobby(player);
			//Resets their scoreboard
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
		
		//Sets game as not running
		//-Shouldn't be needed but it there for security
		gameIsRunning = false;
		
		//Tests whether the game successfully started
		if (gameRan)
		{
			//Store game statistics
			Statistics Statistics = new Statistics(CorePlugin);
			Statistics.storeHideAndSeekPoints(finders, HideGame.game.getGameID());
		}
		
		//Reset hide and seek game
		HideGame.reset();
	}
}
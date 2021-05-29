package Minigames.Games.HideAndSeek;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;

import Minigames.Announce;
import Minigames.MainLobby;
import Minigames.minigamesMain;
import Minigames.statistics.Statistics;

public class HideAndSeekLobby
{
	//Essentials
	private minigamesMain CorePlugin;
	private final Location lobbyLocation;
	private List<Player> players;
	
	//Scoreboard
	protected ScoreboardManager SBM;
	protected Scoreboard SB;
	protected Team TeamH;
	protected Team TeamS;
	protected Objective Found;
	protected Objective Hiders;
	protected Score Score;
	
	//Stores amount of finders wanted
	private int iFinders;
	
	private HideAndSeekMap Map;
	public HideAndSeekGame HideGame;
	
	public boolean gameIsRunning;
	private boolean gameIsStarting;
	
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
		
		//Set up location for hide lobby
		this.lobbyLocation = new Location(Bukkit.getWorld(lobby.getWorldName()), lobby.getiHidePlatformX(), lobby.getiHidePlatformY(), lobby.getiHidePlatformZ());
		
		//Create a list of players
		players = new ArrayList<Player>();
		
		//Get scoreboard
		SBM = Bukkit.getScoreboardManager();
		SB = SBM.getNewScoreboard();
		
		//Registers the teams
		TeamH = SB.registerNewTeam("Hiders");
		TeamS = SB.registerNewTeam("Seekers");
		Found = SB.registerNewObjective("Players found", "dummy");
		Hiders = SB.registerNewObjective("Hiders", "dummy");
		
		//Announce
		Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.GREEN +" Hide and seek lobby created");
		Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.GREEN +" In world: "+lobbyLocation.getWorld().getName() +", X: "+lobbyLocation.getBlockX() + ", Y: "+lobbyLocation.getBlockY() + ", Z: "+lobbyLocation.getBlockZ());
		
		reset();
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
		
		//Unregisters the teams
		TeamH.unregister();
		TeamS.unregister();
		Found.unregister();
		Hiders.unregister();
		
		//Registers the teams
		TeamH = SB.registerNewTeam("Hiders");
		TeamS = SB.registerNewTeam("Seekers");
		Found = SB.registerNewObjective("Players found", "dummy");
		Hiders = SB.registerNewObjective("Hiders", "dummy");

		//Set the teams
		TeamH.setDisplayName("Hiders");
		TeamS.setDisplayName("Seekers");
		
		TeamH.setAllowFriendlyFire(false);
		TeamS.setAllowFriendlyFire(false);
		
		Found.setDisplayName("Seekers");
		Found.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		Hiders.setDisplayName("Hiders");
		Hiders.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	//Handles player attmepting to join the hide and seek lobby
	public void playerJoinLobby(Player player)
	{
		//Kicks them out of River Race lobby
		CorePlugin.RRLobby.playerLeaveLobby(player);
		
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
			Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.BLUE +player.getDisplayName() +ChatColor.DARK_PURPLE +" has joined the Hide and Seek lobby"));
			
			//Announces the amount of players now in the lobby
			if (players.size() == 1)
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" player is now in the lobby"));
			else
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" players are now in the lobby"));
			
			//Resets players speeds
			player.setWalkSpeed((float) 0.2);
			player.setFlySpeed((float) 0.2);
			
			//Selection to start the game here:
			if (players.size() >= CorePlugin.getConfig().getInt("HideMininumPlayers") && !gameIsStarting && !gameIsRunning)
			{
				//Sets game is starting to true
				gameIsStarting = true;
				
		        Bukkit.getScheduler().runTaskLater(this.CorePlugin, new Runnable()
		        {
		            @Override
		            public void run()
		            {
		            //	Announce.announce((Player[]) players.toArray(new Player[players.size()]), ChatColor.DARK_PURPLE +"Moving to map location in "+CorePlugin.getConfig().getInt("HideTimer")+ " seconds!");
		            	if (players.size() >= CorePlugin.getConfig().getInt("HideMininumPlayers") && !gameIsRunning)
		            	{
							lobbyRun();
		            	}
		            	else
		            	{
		            		gameIsStarting = false;
		            	}
		            }
		        }, CorePlugin.getConfig().getInt("HideTimer") * 20L);
			}
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
			Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.BLUE +player.getDisplayName() +ChatColor.DARK_PURPLE +" has left the Hide and Seek lobby"));
			
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
	}
	
	//Triggers the game to start
	public void lobbyRun()
	{
	/*	while (players.size() < 4)
		{
			
		}
	*/
		//Sets finders to 1
		iFinders = 1;
		
		//Creates a new game and returns the gameID		
		HideGame = new HideAndSeekGame((Player[]) players.toArray(new Player[players.size()]), iFinders, Map.iMapID, this.CorePlugin, this);
		gameIsRunning = true;
		
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
			player.setScoreboard(SBM.getMainScoreboard());
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
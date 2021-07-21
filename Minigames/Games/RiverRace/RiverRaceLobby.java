package Minigames.Games.RiverRace;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import Minigames.Announce;
import Minigames.MainLobby;
import Minigames.minigamesMain;

public class RiverRaceLobby
{
	//Essentials
	private minigamesMain CorePlugin;
	private final Location lobbyLocation;
	private ArrayList<Player> players;
	private RiverRaceGame RRGame;
	
	private boolean bGameIsRunning;
	private boolean gameIsStarting;
	private int iTimer;
	
	private final int minPlayers;
	private BukkitTask Task;
	
	public RiverRaceLobby(minigamesMain CorePlugin)
	{
		minPlayers = 1;
		
		//Store plugin locally
		this.CorePlugin = CorePlugin;
		
		//Get lobby into a local variable
		MainLobby lobby = this.CorePlugin.MainLobby;
		
		//Set up location for hide lobby
		this.lobbyLocation = new Location(Bukkit.getWorld(lobby.getWorldName()), lobby.getRiverRaceX()+0.5, lobby.getRiverRaceY()+0.5, lobby.getRiverRaceZ()+0.5);
		
		//Create a list of players
		players = new ArrayList<Player>();
		
		bGameIsRunning = false;
		
		//Get scoreboard
	/*	SBM = Bukkit.getScoreboardManager();
		SB = SBM.getNewScoreboard();
		
		//Registers the teams
		TeamH = SB.registerNewTeam("Hiders");
		TeamS = SB.registerNewTeam("Seekers");
		Found = SB.registerNewObjective("Players found", "dummy");
		Hiders = SB.registerNewObjective("Hiders", "dummy");
		
	*/	//Announce
		Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.GREEN +" River Race lobby created");
		Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.GREEN +" In world: "+lobbyLocation.getWorld().getName() +", X: "+lobbyLocation.getBlockX() + ", Y: "+lobbyLocation.getBlockY() + ", Z: "+lobbyLocation.getBlockZ());
		
		iTimer = 60;
		
		Bukkit.getScheduler().runTaskTimer(this.CorePlugin, new Runnable()
		{
			@Override
			public void run()
			{
				if (players.size() >= minPlayers && !bGameIsRunning && !gameIsStarting)
				{
					gameStartCountdown();
				}
			}
		}, 0, 100L);
		
	//	reset();
	}
	
	//Handles player attmepting to join the hide and seek lobby
	public void playerJoinLobby(Player player)
	{
		//Kicks them out of hide and seek lobby
		CorePlugin.HSLobby.playerLeaveLobby(player);
		
		//Chunks them out of the game
		if (bGameIsRunning)
		{
			RRGame.playerLeave(player);
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
			player.sendMessage(ChatColor.BLUE +"Welcome to the River Race lobby");
			
			//Announces to all players that a player has joined the loobby
			Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.DARK_BLUE +player.getDisplayName() +ChatColor.BLUE +" has joined the River Race lobby"));
			
			//Announces the amount of players now in the lobby
			if (players.size() == 1)
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.BLUE +"1 player is now in the lobby"));
			else
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.BLUE +""+players.size() +" players are now in the lobby"));
			
			//Resets players speeds
			player.setWalkSpeed((float) 0.2);
			player.setFlySpeed((float) 0.2);
						
		/*	//Selection to start the game here:
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
		*/
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
			Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.DARK_BLUE +player.getDisplayName() +ChatColor.BLUE +" has left the River Race lobby"));
			
			//Announces the amount of players now in the lobby
			if (players.size() == 1)
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.BLUE +"1 player is now in the lobby"));
			else
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.BLUE +""+players.size() +" players are now in the lobby"));
			
			player.sendMessage(ChatColor.BLUE +"Leaving River Race lobby");
		}
		
		//If the game is running, calls the method in the game class to deal with them leaving
		if (bGameIsRunning)
		{
			RRGame.playerLeave(player);
		}
	}
	
	public void gameStartCountdown()
	{
		gameIsStarting = true;
		iTimer = 60;
		
		Task = Bukkit.getScheduler().runTaskTimer(this.CorePlugin, new Runnable()
		{
			@Override
			public void run()
			{
				if (bGameIsRunning)
				{
					gameIsStarting = false;
					Bukkit.getScheduler().cancelTask(Task.getTaskId());
					return;
				}
				
				if (players.size() < minPlayers)
				{
					gameIsStarting = false;
					Bukkit.getScheduler().cancelTask(Task.getTaskId());
					return;
				}
				
				if (iTimer == 0)
				{
					Announce.announce(players, ChatColor.BLUE +"Game starting now");
					lobbyRun();
					Bukkit.getScheduler().cancelTask(Task.getTaskId());
					return;
				}
				else if (iTimer % 10 == 0)
				{
					Announce.announce(players, ChatColor.BLUE +"Game starting in "+iTimer +" seconds");
				}
				else if (iTimer < 10)
				{
					Announce.announce(players, ChatColor.BLUE +"Game starting in "+iTimer);
				}
				
				iTimer--;
			}
		}, 0, 20L);
	}
	
	private void lobbyRun()
	{
		bGameIsRunning = true;
				
		RRGame = new RiverRaceGame(CorePlugin, this, players);
		
		players = new ArrayList<Player>();
		
		gameIsStarting = false;
		
		RRGame.highGameProcesses();
	}
	
	public void ManualStart(Player sender)
	{
		if (bGameIsRunning)
			sender.sendMessage(ChatColor.RED +"The River Race game is already running");
		else if (players.size() < 1)
		{
			sender.sendMessage(ChatColor.RED +"You need at least 1 players in the lobby to start manually");
		}
		else
		{
			sender.sendMessage(ChatColor.GREEN +"You have started the River Race game");
			lobbyRun();
		}
	}

	public void ManualTerminate(Player sender)
	{
		if (!bGameIsRunning)
		{
			sender.sendMessage(ChatColor.RED +"The River Race game is not running");
		}
		else
		{
			sender.sendMessage(ChatColor.GREEN +"Terminating the River Race game");
			RRGame.terminate(TerminateType.Manual);
		}
	}
	
	//For if game play started
	public void gameFinished(ArrayList<RiverRaceRacer> racers)
	{
		bGameIsRunning = false;
		
		//Unkit players and add them back to the lobby
		for (RiverRaceRacer racer : racers)
		{
			//Removes them from boat and deletes boat
			racer.unBoard();
			
			//Resets the player's scoreboard
			racer.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			
			//Adds the player to this lobby
			playerJoinLobby(racer.getPlayer());
		}
	}
	
	//For if gameplay did not start
	public void gameFinishedEarly(ArrayList<Player> players)
	{
		bGameIsRunning = false;
		
		///Add players back to the lobby
		for (Player player : players)
		{
			//Resets the player's scoreboard
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			
			//Adds the player to this lobby
			playerJoinLobby(player);
		}
	}
}

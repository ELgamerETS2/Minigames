package Minigames.Games.RiverRace;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import Minigames.Announce;
import Minigames.MainLobby;
import Minigames.minigamesMain;

public class RiverRaceLobby
{
	//Essentials
	private minigamesMain CorePlugin;
	private final Location lobbyLocation;
	private List<Player> players;
	private RiverRaceGame RRGame;
	
	private boolean bGameIsRunning;
	
	public RiverRaceLobby(minigamesMain CorePlugin)
	{
		//Store plugin locally
		this.CorePlugin = CorePlugin;
		
		//Get lobby into a local variable
		MainLobby lobby = this.CorePlugin.MainLobby;
		
		//Set up location for hide lobby
		this.lobbyLocation = new Location(Bukkit.getWorld(lobby.getWorldName()), lobby.getRiverRaceX(), lobby.getRiverRaceY(), lobby.getRiverRaceZ());
		
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
		
	//	reset();
	}
	
	//Handles player attmepting to join the hide and seek lobby
	public void playerJoinLobby(Player player)
	{
		//Kicks them out of hide and seek lobby
		CorePlugin.HSLobby.playerLeaveLobby(player);
		
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
	//	if (gameIsRunning)
	//	{
	//		HideGame.playerLeave(player);
	//	}
	}
	
	private void lobbyRun()
	{
		bGameIsRunning = true;
		
		RRGame = new RiverRaceGame(CorePlugin, this, (Player[]) players.toArray(new Player[players.size()]));
		
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

}

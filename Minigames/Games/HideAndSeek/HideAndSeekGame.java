package Minigames.Games.HideAndSeek;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import Minigames.Announce;
import Minigames.minigamesMain;

public class HideAndSeekGame
{
	public List<HideAndSeekHider> hiders;
	public List<HideAndSeekFinder> finders;
	public Player[] players;
	public int iFinders;
	public HideAndSeekMap Map;
	
	private final minigamesMain plugin;
	
	public HideAndSeekGame(Player[] players, int iFinders, int iMap, minigamesMain plugin) //Input to be from lobby for whatever preferences are decided
	{
		this.plugin = plugin;
		this.players = players;
		this.iFinders = iFinders;
		Map = new HideAndSeekMap(iMap);
		finders = new ArrayList<HideAndSeekFinder>();
		hiders = new ArrayList<HideAndSeekHider>();
	}
	
	public void highGameProcesses()
	{
		//Sets up the game - decides on a seeker
		createTeams();
		
		//Chooses map
	//	chooseMap();
		
		//Countdown
		for (int iNumber = 10 ; iNumber > 0 ; iNumber--)
		{
			this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
			{
				public void run()
				{
				}
			}, 20L);
			Announce.announce(players, (ChatColor.DARK_PURPLE +""+iNumber +" Seconds until start"));
		}
		
		//Teleport players and get them fitted
	//	teleportPlayers();
		
		//Actual gameplay
		game();
	}
	
	public void createTeams()
	{
		int i;
		int iRandom = -1;
		HideAndSeekFinder newFinder = null;
		HideAndSeekHider newHider;
		
		//Find a seeker
		for (i = 0 ; i < iFinders ; i++) 
		{
			iRandom = ((int) Math.random() * players.length);
			newFinder = new HideAndSeekFinder(players[iRandom]);
			finders.add(newFinder);
		}
		
		//Add the hiders
		for (i = 0 ; i < iFinders ; i++)
		{
			if (i != iRandom)
			{
				newHider = new HideAndSeekHider(players[i]);
				hiders.add(newHider);
			}
		}
		
		Announce.announce(players, (ChatColor.RED +newFinder.player.getDisplayName() +ChatColor.DARK_PURPLE +" is the seeker"));
	}
	
/*	public void chooseMap()
	{
		if (Map.iMapID == 0)
		{
			//Random number innit + choose
			int iTotalMaps = HideAndSeekMap.count();
			int iRandom = 1 + ( (int) Math.random()*iTotalMaps );
			Map.iMapID = iRandom;
		}
		//Collect details of map
		Map.setMapFromMapID();
		
		//Announce choice of map
		Announce.announce(players, (ChatColor.DARK_PURPLE +"The map choosen is "+ChatColor.BLUE +Map.szName +ChatColor.DARK_PURPLE+" by "+ChatColor.BLUE+Map.szCreator));
	}
	
	public void teleportPlayers()
	{
		int i;
		Location location = new Location(Map.mapWorld, Map.spawnCoordinates[0], Map.spawnCoordinates[1], Map.spawnCoordinates[2]);
		for (i = 0 ; i < players.length ; i++)
		{
			players[i].teleport(location);
		}
	}
	*/
	public void game()
	{
		int i, j;
		
		Announce.announce(players, (ChatColor.GREEN +"There are 30 seconds for hiders to hide"));
		
		//For 30 seconds let hiders hide and restrain the seekers
		for (i = 0 ; i < this.finders.size() ; i++)
		{
			finders.get(i).player.sendMessage("You are restricted for 30 seconds");
			finders.get(i).player.setWalkSpeed(0);
			for (j = 0 ; j < hiders.size() ; j++)
			{
				finders.get(i).player.hidePlayer(hiders.get(j).player);
			}
		}
		
		//30 second countdown
		this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
		{
			public void run()
			{
			}
		}, 540L);
				
		for (i = 3 ; i > 0 ; i++)
		{
			Announce.announce(players, (ChatColor.GOLD +""+i));
			Announce.playNote(players, Sound.ANVIL_LAND);
			
			this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
			{
				public void run()
				{
				}
			}, 20L);
		}
				
		Announce.announce(players, (ChatColor.GREEN +"GO!"));
		Announce.playNote(players, Sound.FIREWORK_LARGE_BLAST2);
		
		//Then allow hiders to be found
		for (i = 0 ; i < this.finders.size() ; i++)
		{
			for (j = 0 ; j < hiders.size() ; j++)
			{
				finders.get(i).player.showPlayer(hiders.get(j).player);
			}
		}
		
		finders.get(i).player.setWalkSpeed(1);
		
		Announce.announce(players, (ChatColor.RED +"The seekers have been release"));
		
		//Listeners
		new DamageDone(this, plugin);
		
		
	}
}

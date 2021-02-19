package Minigames.Games.HideAndSeek;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import Minigames.Announce;
import Minigames.minigamesMain;

public class HideAndSeekGame extends HideAndSeekLobby
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
	}
	
	public void highGameProcesses()
	{
		//Sets up the game - decides on a seeker
		createTeams();
		
		//Chooses map
		chooseMap();
		
		int minute = (int) 1200L;
		
		int iNumber = 10;
		
		//Schedulers
		this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			public void run()
			{
				Announce.announce(players, "");
				iNumber--;
			}
		}, 0L, minute * config.getInt("timerInterval"));
		
		//Teleport players and get them fitted
		teleportPlayers();
		
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
		
		Announce.announce(players, (ChatColor.RED +newFinder.player.getDisplayName() +ChatColor.DARK_PURPLE +"is the seeker"));
	}
	
	public void chooseMap()
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
		Announce.announce(players, (ChatColor.DARK_PURPLE +"The map choosen is "+ChatColor.BLUE +Map.szName +ChatColor.DARK_PURPLE+"by "+ChatColor.BLUE+Map.szCreator));
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
	
	public void game()
	{
		int i, j;
		
		Announce.announce(players, (ChatColor.GREEN +"There are 30 seconds for hiders to hide"));
		
		//For 30 seconds let hiders hide and restrain the seekers
		for (i = 0 ; i < this.finders.size() ; i++)
		{
			finders.get(i).player.sendMessage("You are restricted for 30 seconds");
			for (j = 0 ; j < hiders.size() ; j++)
			{
				finders.get(i).player.hidePlayer(hiders.get(j).player);
			}
		}
		
		//Then allow hiders to be found
		for (i = 0 ; i < this.finders.size() ; i++)
		{
			finders.get(i).player.sendMessage("It is time to begin");
			for (j = 0 ; j < hiders.size() ; j++)
			{
				finders.get(i).player.showPlayer(hiders.get(j).player);
			}
		}
		
		Announce.announce(players, (ChatColor.RED +"The seekers have been release"));
		
		//Listeners
		new DamageDone(this, plugin);
		
		
	}
}

package Minigames.Games.HideAndSeek;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
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
	
    public HideAndSeekGame HSG;
    
	private final minigamesMain plugin;
	
	public boolean bTerminate;
	
	public HideAndSeekGame(Player[] players, int iFinders, int iMap, minigamesMain plugin) //Input to be from lobby for whatever preferences are decided
	{
		this.plugin = plugin;
		this.players = players;
		this.iFinders = iFinders;
		Map = new HideAndSeekMap(iMap);
		finders = new ArrayList<HideAndSeekFinder>();
		hiders = new ArrayList<HideAndSeekHider>();
		bTerminate = false;
	}
	
	public void highGameProcesses()
	{
		//Sets up the game - decides on a seeker
		createTeams();
		
		//Chooses map
	//	chooseMap();
		
		//Starts 10 second countdown into game
        Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable()
        {
            int time = 10; //or any other number you want to start countdown from
            @Override
            public void run()
            {
                if (this.time == 0)
                {
                    return;
                }
    			Announce.announce(players, (ChatColor.DARK_PURPLE +""+time +" Seconds until start"));
                this.time--;
            }
        }, 0L, 20L);
        
		//Teleport players and get them fitted
	//	teleportPlayers();
		
        //Run game after 10 seconds
        Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable()
        {
            @Override
            public void run()
            {
        		//Actual gameplay
        		game();
            }
        }, 200L);
	}
	
	private void createTeams()
	{
		int i, j;
		int iRandom = -1;
		HideAndSeekFinder newFinder = null;
		HideAndSeekHider newHider;
		boolean bUnique;
		
		//Find a seeker
		for (i = 0 ; i < iFinders ; i++) 
		{
			bUnique = true;
			
			iRandom = ((int) (Math.random() * players.length));
			newFinder = new HideAndSeekFinder(players[iRandom]);
			for (j = 0 ; j < finders.size() ; j++) 
			{
				//Catches whether the finder selected is already in the list
				if (finders.get(j).player.equals(players[iRandom]))
				{
					bUnique = false;
					break;
				}
			}
			
			//Tests whether they weren't found in the list already
			if (bUnique)
				finders.add(newFinder);
			else
				i--;
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
		
		//Announce finders
		for (i = 0 ; i < iFinders ; i++)
		{
			newFinder = new HideAndSeekFinder(players[iRandom]);
			Announce.announce(players, (ChatColor.RED +newFinder.player.getDisplayName() +ChatColor.DARK_PURPLE +" is a seeker"));
		}
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
	private void game()
	{
		int i, j;
		Announce.announce(players, (ChatColor.GREEN +"The game has begun"));
		Announce.announce(players, (ChatColor.GREEN +"There are 30 seconds for hiders to hide"));
		
		//For 30 seconds let hiders hide and restrain the seekers
		for (i = 0 ; i < this.finders.size() ; i++)
		{
			finders.get(i).player.sendMessage("You are restricted for 30 seconds");
			finders.get(i).player.setWalkSpeed(0);
			finders.get(i).player.setFlySpeed(0);
			for (j = 0 ; j < hiders.size() ; j++)
			{
				finders.get(i).player.hidePlayer(hiders.get(j).player);
			}
		}
		
        Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable()
        {
            int time = 3; //or any other number you want to start countdown from

            @Override
            public void run()
            {
                if (this.time == 0)
                {
                    return;
                }
			    Announce.announce(players, (ChatColor.GOLD +""+time));
			//	Announce.playNote(players, Sound.GLASS);             
                this.time--;
            }
        }, 27*20L, 20L);
        
        HSG = this;
        
        Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable()
        {
            @Override
            public void run()
            {
            	int i, j;
        		Announce.announce(players, (ChatColor.GREEN +"GO!"));
        	//	Announce.playNote(players, Sound.FIREWORK_BLAST);
        		
        		//Then allow hiders to be found
        		for (i = 0 ; i < finders.size() ; i++)
        		{
        			finders.get(i).player.setWalkSpeed(0.1F);
        			finders.get(i).player.setFlySpeed(0.1F);
        			for (j = 0 ; j < hiders.size() ; j++)
        			{
        				finders.get(i).player.showPlayer(hiders.get(j).player);
        			}
        		}
        		
        		Announce.announce(players, (ChatColor.RED +"The seekers have been release"));
        		
            }
        }, 600L);
		
        if (bTerminate)
        {
        	Announce.announce(players, "Game terminated");
       // 	return true;
        }
        
        //Listeners
		new DamageDone(HSG, plugin);
	}
	
	private void terminate()
	{
		
	}
}

package Minigames.Games.HideAndSeek;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

import Minigames.Announce;
import Minigames.minigamesMain;
import Minigames.Games.Game;
import Minigames.Games.Gametype;

public class HideAndSeekGame
{
	public final Game game;
	public ArrayList<HideAndSeekHider> hiders;
	public ArrayList<HideAndSeekFinder> finders;
	public ArrayList<HideAndSeekPlayer> allOriginalPlayers;
	private Player[] players;
	public int iFinders;
	public int iStartingHiders;
	public HideAndSeekMap Map;
	    
    protected HideAndSeekLobby HSL;
    
	private final minigamesMain plugin;
	
	private DamageDone DD;
	private SeekerMove SM;
	
	private boolean registered;
	
	protected boolean bTerminate;
	private boolean bGamePlayStarted;
	
	private Score score;
	
	public HideAndSeekGame(Player[] players, int iFinders, int iMap, minigamesMain plugin, HideAndSeekLobby HSL) //Input to be from lobby for whatever preferences are decided
	{
		//Sets up the Game class
		this.game = new Game(Gametype.Hide_And_Seek);
		
		this.plugin = plugin;
		this.HSL = HSL;
		
		this.players = players;
		
		this.iFinders = iFinders;
		Map = new HideAndSeekMap(iMap);
		finders = new ArrayList<HideAndSeekFinder>();
		hiders = new ArrayList<HideAndSeekHider>();
		bTerminate = false;
		registered = false;
		
		bGamePlayStarted = false;
	}
	
	public Player[] getPlayers()
	{
		return players;
	}
	
	public void reset()
	{
		this.players = new Player[0];
		this.iFinders = 0;
		Map = new HideAndSeekMap();
		finders = new ArrayList<HideAndSeekFinder>();
		hiders = new ArrayList<HideAndSeekHider>();
		registered = false;
		bGamePlayStarted = false;
	}
	
	public void highGameProcesses()
	{
		//Sets up the game - decides on a seeker
		createTeams();
		
		//Chooses map
		chooseMap();
		
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
                //Terminates the process if the game has been terminated
                if (bTerminate)
                {
                	return;
                }
                if (time == 1)
        			Announce.announce(getPlayers(), (ChatColor.DARK_PURPLE +""+time +" Second until start"));
                else
        			Announce.announce(getPlayers(), (ChatColor.DARK_PURPLE +""+time +" Seconds until start"));
                this.time--;
            }
        }, 0L, 20L);
        
        if (bTerminate)
        {
        	return;
        }
        
		//Teleport players and get them fitted
		teleportPlayers();
		allowFlight();
		
        if (bTerminate)
        {
        	return;
        }
        
        int i, j;
        
        //Ensures all players can see all other players
        for (i = 0 ; i < players.length ; i++)
        {
        	for (j = 0 ; j < players.length ; j++)
        	{
        		players[i].showPlayer(plugin, players[j]);
        	}
        }
        
        //Run game after 10 seconds
        Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable()
        {
            @Override
            public void run()
            {
                if (bTerminate)
                {
                	return;
                }
                
                disableFlight();
                teleportPlayers();
                
            	game.setTimeStart();
            	game.storeGameInDatabase();
            	game.selectLastInsertID();
            	
        		//Actual gameplay
            	bGamePlayStarted = true;
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
			
			iRandom = ((int) (Math.random() * getPlayers().length));
			newFinder = new HideAndSeekFinder(getPlayers()[iRandom]);
			
			//Tests whether the finder selected is already in the list
			for (j = 0 ; j < finders.size() ; j++) 
			{
				//Catches whether the finder selected is already in the list
				if (finders.get(j).player.equals(getPlayers()[iRandom]))
				{
					bUnique = false;
					break;
				}
			}
			
			//Tests whether they weren't found in the list already
			if (bUnique)
			{
				finders.add(newFinder);
				HSL.TeamS.addEntry(getPlayers()[iRandom].getDisplayName());
				score = HSL.Found.getScore(getPlayers()[i].getDisplayName());
				score.setScore(0);
				(getPlayers()[iRandom]).setScoreboard(HSL.SB);
			//	allOriginalPlayers.add(newPlayer);
			}
			else //Go back and find another seeker
				i--;
		}
		
		//Add the hiders
		for (i = 0 ; i < getPlayers().length ; i++)
		{
			if (i != iRandom)
			{
				newHider = new HideAndSeekHider(getPlayers()[i]);
				hiders.add(newHider);
				HSL.TeamH.addEntry(getPlayers()[i].getDisplayName());
				score = HSL.Found.getScore(getPlayers()[i].getDisplayName());
				(getPlayers()[i]).setScoreboard(HSL.SB);
			}
		}
		
		//Announce finders
		for (i = 0 ; i < finders.size() ; i++)
		{
			newFinder = finders.get(i);
			Announce.announce(getPlayers(), (ChatColor.DARK_PURPLE +newFinder.player.getDisplayName() +ChatColor.LIGHT_PURPLE +" is a seeker"));
		}
	}
	
	//-------------------------------------
	//-----------Chooses the map-----------
	//-------------------------------------
	public void chooseMap()
	{
		//Collects a list of Maps
		int[] iMapIDs = HideAndSeekMap.hideAndSeekMapIDs();
		
		//If the MapID is 0 (Not already set), change the MapID to a random hide and seek map
		if (Map.iMapID == 0)
		{
			int iTotalMaps = iMapIDs.length;
			System.out.println("[Minigames] [SQL Maps] Maps Found = " +iTotalMaps);
			int iRandom = 1 + ( (int) (Math.random() * iTotalMaps) );
			Map.iMapID = iRandom;
		}
		
		//Collect details of map
		Map.setMapFromMapID();
		
		//Set the MapID in game
		game.setMapID(Map.iMapID);
		
		//Announce choice of map
		Announce.announce(getPlayers(), (ChatColor.LIGHT_PURPLE +"The map choosen is "+ChatColor.DARK_PURPLE +Map.szLocation +ChatColor.LIGHT_PURPLE+" by "+ChatColor.DARK_PURPLE+Map.szCreator));
	}
	
	//-------------------------------------
	//-----------Teleports players---------
	//-------------------------------------
	public void teleportPlayers()
	{
		int i;
		Location location = new Location(Map.mapWorld, Map.spawnCoordinates[0], Map.spawnCoordinates[1], Map.spawnCoordinates[2]);
		for (i = 0 ; i < getPlayers().length ; i++)
		{
			getPlayers()[i].teleport(location);
		}
	}
	
	private void allowFlight()
	{
		for (HideAndSeekFinder finder : finders)
		{
			finder.player.setAllowFlight(true);
			finder.player.setFlying(true);
			finder.player.setFlySpeed(0.3F);
		}
		
		for (HideAndSeekHider hider : hiders)
		{
			hider.player.setAllowFlight(true);
			hider.player.setFlying(true);
			hider.player.setFlySpeed(0.3F);
		}
	}
	
	public void disableFlight()
	{
		for (HideAndSeekFinder finder : finders)
		{
			finder.player.setFlySpeed(0.2F);
			finder.player.setFlying(false);
			finder.player.setAllowFlight(false);
		}
		
		for (HideAndSeekHider hider : hiders)
		{
			hider.player.setFlySpeed(0.2F);
			hider.player.setFlying(false);
			hider.player.setAllowFlight(false);
		}
	}
	
	//-------------------------------------
	//-----------Actual gameplay-----------
	//-------------------------------------
	private void game()
	{
		int i, j;
		Announce.announce(getPlayers(), (ChatColor.LIGHT_PURPLE +"The game has begun"));
		Announce.announce(getPlayers(), (ChatColor.LIGHT_PURPLE +"There are " +Map.iWait +" seconds for hiders to hide"));
		
		for (i = 0 ; i < this.finders.size() ; i++)
		{
			finders.get(i).player.sendMessage(ChatColor.DARK_PURPLE +"You are restricted for " +Map.iWait +" seconds");
			finders.get(i).player.setWalkSpeed(0);
			finders.get(i).player.setFlySpeed(0);
			
			for (j = 0 ; j < hiders.size() ; j++)
			{
				finders.get(i).player.hidePlayer(plugin, hiders.get(j).player);
			}
		}
		
		//For 30 seconds let hiders hide and restrain the seekers
		SM = new SeekerMove(this, plugin);
		
        if (bTerminate)
        {
        	return;
        }
        
		//Plays last 3 seconds
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
			    Announce.announce(getPlayers(), (ChatColor.DARK_PURPLE +""+time));
			//	Announce.playNote(players, Sound.GLASS);             
                this.time--;
            }
        }, (Map.iWait - 3) * 20L, 20L);
                
        if (bTerminate)
        {
        	return;
        }
        
        //Releases seekers
        Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable()
        {
            @Override
            public void run()
            {
            	int i, j;
        	//	Announce.playNote(players, Sound.FIREWORK_BLAST);
        		
    			SM.unRegister();
    			
        		//Then allow hiders to be found
        		for (i = 0 ; i < finders.size() ; i++)
        		{
        			finders.get(i).player.setWalkSpeed((float) plugin.getConfig().getDouble("SeekerSpeed"));
        			finders.get(i).player.setFlySpeed((float) 0.2);
        			for (j = 0 ; j < hiders.size() ; j++)
        			{
        				finders.get(i).player.showPlayer(plugin, hiders.get(j).player);
        			}
        		}
        		
        		Announce.announce(getPlayers(), (ChatColor.DARK_PURPLE +"" +ChatColor.BOLD +"The seekers have been release"));
            }
        }, Map.iWait * 20L);
        
        if (bTerminate)
        {
        	return;
        }
        
		registered = true;
		
        //Creates the hit listener
        DD = new DamageDone(this, plugin);
   	}
	
	//----------------------------------------
	//-------Deals with players leaving-------
	//----------------------------------------
	public void playerLeave(Player player)
	{
		int i;
		boolean bRemovedFromHiders = false;
		
		//Checks whether they are in the hiders list and removes them
		for (i = 0 ; i < hiders.size() ; i++)
		{
			if (hiders.get(i).player.equals(player))
			{
				hiders.remove(i);
				bRemovedFromHiders = true;
				break;
			}
		}
		
		//If not removed from hiders, checks whether they are in the finders list and removes them
		if (!bRemovedFromHiders)
		{
			//Goes through seekers
			for (i = 0 ; i < finders.size() ; i++)
			{
				//If the player is found within the seekers list,
				if (finders.get(i).player.equals(player))
				{
					//Remove them from it
					finders.remove(i);
					break;
				}
			}
		}
		
		//Update the players array - Used for announcements
		//Update size
		players = new Player[finders.size() + hiders.size()];
		//Go through finders and add those
		for (i = 0 ; i < finders.size() ; i++)
		{
			getPlayers()[i] = finders.get(i).player;
		}
		//Go through hiders and add those
		for (i = finders.size() ; i < players.length ; i++)
		{
			getPlayers()[i] = hiders.get(i).player;
		}
		
		//Checks the size of each list to check if the game needs to end
		if (bRemovedFromHiders)
		{
			if (hiders.size() == 0)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] Final hider left");
				finalHiderLeft();
			}
		}
		else if (finders.size() == 0)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] Final seeker left");
			finalFinderLeft();
		}
	}
	
	//Deals with final hider leaving
	private void finalHiderLeft()
	{
		Announce.announce(getPlayers(), (ChatColor.GREEN +"The final hider left, the seekers have won!"));
		terminate();
	}
	
	//Deals with final seeker leaving
	private void finalFinderLeft()
	{
		Announce.announce(getPlayers(), (ChatColor.GREEN +"The final seeker left, the hiders have won!"));
		terminate();
	}
	
	//Terminates the game
	public void terminate()
	{
		try
		{
			//Records game termination in the log
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] The game ended");

			//Sets game as not running in the lobby
			//This is very important as if someone now leaves, it will prevent the game from attempting the remove them
			//...from the game which could then trigger another termination on top of the current one.
			plugin.HSLobby.gameIsRunning = false;

			//Unregisters damage listener
			if (registered)
			{
				DD.unRegister();
				registered = false;
			}

			//Announce termination and set terminate to true
			this.bTerminate = true;
			Announce.announce(getPlayers(), (ChatColor.GREEN +"The game has ended!"));

			//Records whether game play started in the log
			if (!bGamePlayStarted)
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] The game play did not start");
			else
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] The game play did start");

			//Checks whether game play started (i.e game added to DB)
			if (bGamePlayStarted)
			{
				//Record end of game in database
				game.setTimeEnd();
				game.storeGameEndInDatabase();
			}

			//Wait 6 seconds before sending players back to the lobby
			Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable()
			{
				@Override
				public void run()
				{            	
					//Notifies lobby of game ending
					plugin.HSLobby.gameFinished(getPlayers(), finders, bGamePlayStarted);
				}
			}, 120L);
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] terminate() - Error teriminating game");
			e.printStackTrace();
		}
	}
}

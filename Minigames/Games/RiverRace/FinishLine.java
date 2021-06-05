package Minigames.Games.RiverRace;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import Minigames.Announce;
import Minigames.minigamesMain;

public class FinishLine implements Listener
{
	private final RiverRaceGame RRGame;
	private final int iNumber;
	
	private final Location[] locations;
	
	private ArrayList<RiverRaceRacer> YetToFinish;
	protected ArrayList<RiverRaceRacer> Finished;
	
	public FinishLine(minigamesMain CorePlugin, RiverRaceGame RRGame, ArrayList<RiverRaceRacer> racers, int iNumber, Location[] locations)
	{
		//Set up checkpoint
		this.RRGame = RRGame;
		this.iNumber = iNumber;
		
		YetToFinish = new ArrayList<RiverRaceRacer>();
		
		for (int i = 0 ; i < racers.size() ; i++)
		{
			this.YetToFinish.add(racers.get(i));
		}
		
		this.Finished = new ArrayList<RiverRaceRacer>();
		this.locations = locations;
		
		Bukkit.getServer().getPluginManager().registerEvents(this, CorePlugin);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Minigames] [River Race] Finishline ("+this.iNumber +") loaded");
	}
	
	@EventHandler
	private void Finished(PlayerMoveEvent event)
	{
		Location location = event.getTo();
		if (locationIsInCheckpoint(location))
		{
			registerPlayer(event);
		}
	}
	
	private boolean locationIsInCheckpoint(Location location)
	{
		//Goes through all checkpoint blocks
		for (int i = 0 ; i < locations.length ; i++)
		{
			//If the location is in one of these blocks, return true
			if (location.getBlockX() == locations[i].getBlockX() && location.getBlockY() == locations[i].getBlockY() && location.getBlockZ() == locations[i].getBlockZ())
			{
				return true;
			}
		}
		//If the location was in none of the checkpoint blocks, return false
		return false;
	}
	
	private void registerPlayer(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		int i;
		
		//Goes through yet to pass and moves the correct racer onto Passed
		for (i = 0 ; i < YetToFinish.size() ; i++)
		{
			if (YetToFinish.get(i).getUUID().equals(player.getUniqueId()))
			{
				if (playerPastPrevious(YetToFinish.get(i).getUUID()))
				{
					YetToFinish.get(i).updateScore(iNumber);
					Finished.add(YetToFinish.get(i));
					String szName = YetToFinish.get(i).getPlayer().getName();
					YetToFinish.remove(i);
					dealWithFinish(szName);
					break;
				}
				else if (locationIsInCheckpoint(event.getFrom()))
				{
					//If they are just moving along the finish line
				}
				else
				{
					player.sendMessage(ChatColor.DARK_BLUE +"You have missed a checkpoint");
				}
			}
		}
	}
	
	private boolean playerPastPrevious(UUID uuid)
	{
		if (this.iNumber == 1)
			return true;
		else
			return RRGame.ListenerCheckpoints.get(iNumber-2).playerHasPassed(uuid);
	}
	
	private void dealWithFinish(String name)
	{
		Announce.announce(RRGame.getPlayers(), ChatColor.DARK_BLUE +name+ChatColor.BLUE+" Has finished the race!");
		
		if (YetToFinish.size() == 0)
		{
			RRGame.terminate(TerminateType.All_Players_Finished);
		}
	}
	
	public void playerLeft(Player player)
	{
		int i;
		//Checked passed list
		for (i = 0 ; i < Finished.size() ; i++)
		{
			if (Finished.get(i).getUUID().equals(player.getUniqueId()))
			{
				Finished.remove(i);
			}
		}
		
		//Checked yet to pass list
		for (i = 0 ; i < YetToFinish.size() ; i++)
		{
			if (YetToFinish.get(i).getUUID().equals(player.getUniqueId()))
			{
				YetToFinish.remove(i);
			}
		}
	}
	
	public void unRegister()
	{
		HandlerList.unregisterAll(this);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Minigames] [River Race] Finishline unregistered");
	}
}

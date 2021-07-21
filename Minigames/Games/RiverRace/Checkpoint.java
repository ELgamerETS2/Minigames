package Minigames.Games.RiverRace;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
//import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
//import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
//import org.bukkit.event.block.Action;
//import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import Minigames.minigamesMain;

public class Checkpoint implements Listener
{
	private final RiverRaceGame RRGame;
	private final int iNumber;
	
	private final Location[] locations;
	
	private ArrayList<RiverRaceRacer> YetToPass;
	private ArrayList<RiverRaceRacer> Passed;
	
	public Checkpoint(minigamesMain CorePlugin, RiverRaceGame RRGame, ArrayList<RiverRaceRacer> racers, int iNumber, Location[] locations)
	{
		//Set up checkpoint
		this.RRGame = RRGame;
		this.iNumber = iNumber;
		
		YetToPass = new ArrayList<RiverRaceRacer>();
		
		for (int i = 0 ; i < racers.size() ; i++)
		{
			this.YetToPass.add(racers.get(i));
		}
		
		this.Passed = new ArrayList<RiverRaceRacer>();
		this.locations = locations;
		
		Bukkit.getServer().getPluginManager().registerEvents(this, CorePlugin);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Minigames] [River Race] Checkpoint "+this.iNumber +" loaded");
	}
	
/*	@EventHandler
	//New event to try to reduce lag on the minigames server
	public void Pass2(PlayerInteractEvent event)
	{
		if (event.getAction() != Action.PHYSICAL)
			return;
		if (event.getMaterial() != Material.TRIPWIRE)
			return;
		Location location = event.getClickedBlock().getLocation();
		
		//Check whether the location they have moved to is in the checkpoint
		if (locationIsInCheckpoint(location))
		{
			//Registers that the player has moved through the checkponiny
			registerPlayer(event);
		}
	}
*/	
	@EventHandler
	public void Pass(PlayerMoveEvent event)
	{
		Location location = event.getTo();
		if (locationIsInCheckpoint(location))
		{
			registerPlayer(event);
		}
	}
		
	private void registerPlayer(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		int i;
		
		//Goes through yet to pass and moves the target racer into Passed
		for (i = 0 ; i < YetToPass.size() ; i++)
		{
			if (YetToPass.get(i).getUUID().equals(player.getUniqueId()))
			{
				if (playerPastPrevious(YetToPass.get(i).getUUID()))
				{
					//Make sure these are in the correct order
					YetToPass.get(i).updateScore(iNumber);
					Passed.add(YetToPass.get(i));
					YetToPass.remove(i);
					player.sendMessage(ChatColor.BLUE +"You've passed checkpoint "+iNumber);
					break;
				}
				//Checks whether the location that they have come from was another point on the checkpoint
				else if (locationIsInCheckpoint(event.getFrom()))
				{
					//If they are just moving along a checkpoint, don't display message
				}
				else
				{
					player.sendMessage(ChatColor.DARK_BLUE +"You have missed a checkpoint");
				}
			}
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
	
	private boolean playerPastPrevious(UUID uuid)
	{
		//Used for if this is the first checkpoint
		if (this.iNumber == 1)
			return true;
		else
			return RRGame.ListenerCheckpoints.get(iNumber-2).playerHasPassed(uuid);
	}
	
	public boolean playerHasPassed(UUID uuid)
	{
		int i;
		//Go through list of racers who have passed
		for (i = 0 ; i < Passed.size() ; i++)
		{
			//If the target racer is found, return true
			if (Passed.get(i).getUUID().equals(uuid))
			{
				return true;
			}
		}
		//If the target racer was not found, return false
		return false;
	}
	
	public void playerLeft(Player player)
	{
		int i;
		//Checked passed list
		for (i = 0 ; i < Passed.size() ; i++)
		{
			if (Passed.get(i).getUUID().equals(player.getUniqueId()))
			{
				Passed.remove(i);
			}
		}
		
		//Checked yet to pass list
		for (i = 0 ; i < YetToPass.size() ; i++)
		{
			if (YetToPass.get(i).getUUID().equals(player.getUniqueId()))
			{
				YetToPass.remove(i);
			}
		}
	}
	
	public void unRegister()
	{
		HandlerList.unregisterAll(this);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Minigames] [River Race] Checkpoint "+iNumber +" unregistered");
	}
}

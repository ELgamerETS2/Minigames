package Minigames.Games.RiverRace;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import Minigames.minigamesMain;

public class Checkpoint implements Listener
{
	private final minigamesMain CorePlugin;
	private final RiverRaceGame RRGame;
	private final int iNumber;
	
	private final Location[] locations;
	
	private ArrayList<RiverRaceRacer> YetToPass;
	private ArrayList<RiverRaceRacer> Passed;
	
	public Checkpoint(minigamesMain CorePlugin, RiverRaceGame RRGame, ArrayList<RiverRaceRacer> racers, int iNumber, Location[] locations)
	{
		//Set up checkpoint
		this.CorePlugin = CorePlugin;
		this.RRGame = RRGame;
		this.iNumber = iNumber;
		this.YetToPass = racers;
		this.locations = locations;
		
		Bukkit.getServer().getPluginManager().registerEvents(this, CorePlugin);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Minigames] [RiverRace] Checkpoint "+this.iNumber +" loaded");
	}
	
	@EventHandler
	public void Pass(PlayerMoveEvent event)
	{
		Location location = event.getTo();
		for (int i = 0 ; i < locations.length ; i++)
		{
			if (location.getBlockX() == locations[i].getBlockX() && location.getBlockY() == locations[i].getBlockY() && location.getBlockZ() == locations[i].getBlockZ())
			{
				getPlayer(event.getPlayer());
			}
		}
	}
	
	private void getPlayer(Player player)
	{
		int i;
		RiverRaceRacer racerPassed;
		
		//Goes through yet to pass and moves the correct racer onto Passed
		for (i = 0 ; i < YetToPass.size() ; i++)
		{
			if (YetToPass.get(i).getUUID().equals(player.getUniqueId()))
			{
				if (playerPastPrevious(YetToPass.get(i).getUUID()))
				{
					racerPassed = YetToPass.get(i);
					YetToPass.remove(i);
					racerPassed.updateScore(iNumber);
					Passed.add(racerPassed);
					break;
				}
				else
				{
					player.sendMessage("You have missed a checkpoint");
				}
			}
		}
	}
	
	private boolean playerPastPrevious(UUID uuid)
	{
		if (this.iNumber == 1)
			return true;
		else
			return RRGame.checkpoints.get(iNumber-2).playerHasPassed(uuid);
	}
	
	public boolean playerHasPassed(UUID uuid)
	{
		int i;
		for (i = 0 ; i < Passed.size() ; i++)
		{
			if (Passed.get(i).getUUID().equals(uuid))
			{
				return true;
			}
		}
		return false;
	}
}

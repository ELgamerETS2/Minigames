package Minigames.Games.HideAndSeek;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import Minigames.minigamesMain;

public class SeekerMove implements Listener
{
//	private final minigamesMain mainPlugin;
	private final HideAndSeekGame HideAndSeekGame;
	
	public SeekerMove(HideAndSeekGame game, minigamesMain mainPlugin)
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Minigames] [Hide] SeekerMove loaded");
		this.HideAndSeekGame = game;
		Bukkit.getServer().getPluginManager().registerEvents(this, mainPlugin);
	}
		
	@EventHandler
	public void Move(PlayerMoveEvent event)
	{
		int i;
		
		//Get the player involved into a local variable
		Player player = event.getPlayer();
		
		//Goes through the list of finders
		for (i = 0 ; i < HideAndSeekGame.finders.size() ; i++)
		{
			if (HideAndSeekGame.finders.get(i).player.getUniqueId().equals(player.getUniqueId()))
			{
				event.setCancelled(true);
			}
		}
	}
	
	public void unRegister()
	{
		HandlerList.unregisterAll(this);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Minigames] [Hide] SeekerMove unregistered");
	}

}

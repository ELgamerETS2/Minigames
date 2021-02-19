
/**
 * @author 14walkerg
 * @date 4 Jan 2021
 * @time 17:55:24
 */
package Minigames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import Minigames.minigamesMain;

public class JoinEvent implements Listener
{
	private final minigamesMain plugin;
	
	public JoinEvent(minigamesMain plugin)
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "JoinEvent loaded");
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent event)
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Player joined minigames server, setting to spawn");
		Location location = new Location(World, plugin.getConfig().getDouble("XofSpawn"), plugin.getConfig().getDouble("YofSpawn"), plugin.getConfig().getDouble("ZofSpawn"));
		
		event.getPlayer().teleport(location);
	}
} //End Class

//Created by Bluecarpet in London
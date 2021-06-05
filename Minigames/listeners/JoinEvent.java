
/**
 * @author 14walkerg
 * @date 4 Jan 2021
 * @time 17:55:24
 */
package Minigames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import Minigames.MainLobby;
import Minigames.minigamesMain;
import Minigames.Games.RiverRace.RRSelection;

public class JoinEvent implements Listener
{
	private final minigamesMain plugin;
	
	public JoinEvent(minigamesMain plugin)
	{
		Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.GREEN + " JoinEvent loaded");
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent event)
	{
		//Get lobby into a local variable
		MainLobby lobby = plugin.MainLobby;
		
		//Send message to console
		Bukkit.getConsoleSender().sendMessage("[Minigames] " +ChatColor.GREEN + "Player joined minigames server, setting to spawn of "+plugin.MainLobby.getSzName() + " lobby");
		
		//Set up location for spawn point
		Location location = new Location(Bukkit.getWorld(lobby.getWorldName()), lobby.getX(), lobby.getY(), lobby.getZ());
						
		//Teleport player to spawn
		event.getPlayer().teleport(location);
		
		//Set gamemode
		event.getPlayer().setGameMode(GameMode.ADVENTURE);
		
		//Display welcome messgae
        Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable()
        {
            @Override
            public void run()
            {
        		event.getPlayer().sendMessage(ChatColor.GREEN + "Welcome to BTE-UK Minigames!");
            }
        }, 20L);
		
		if (event.getPlayer().hasPermission("Minigames.boatchecks"))
		{
			RRSelection newMapMaker = new RRSelection(event.getPlayer());
			plugin.selections.add(newMapMaker);
		}
	}
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent event)
	{
		Bukkit.getConsoleSender().sendMessage("[Minigames] Player left, removing from game lobbies");
		plugin.HSLobby.playerLeaveLobby(event.getPlayer());
		plugin.RRLobby.playerLeaveLobby(event.getPlayer());
	}
	
} //End Class

//Created by Bluecarpet in London
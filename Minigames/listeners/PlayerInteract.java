package Minigames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

import Minigames.minigamesMain;
import Minigames.gui.LobbyGUI;

public class PlayerInteract implements Listener
{
	public PlayerInteract(minigamesMain plugin)
	{
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.GREEN + " PlayerInteract loaded");
	}

	@EventHandler
	public void interactEvent(PlayerInteractEvent e)
	{		
		if (e.getPlayer().getOpenInventory().getType() != InventoryType.CRAFTING && e.getPlayer().getOpenInventory().getType() != InventoryType.CREATIVE)
		{
		    return;
		}
		
		if (e.getPlayer().getInventory().getItemInHand().equals(minigamesMain.gui))
		{
			e.setCancelled(true);
			e.getPlayer().openInventory(LobbyGUI.GUI(e.getPlayer()));
		}
	}
}

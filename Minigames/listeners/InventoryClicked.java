package Minigames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import Minigames.minigamesMain;
import Minigames.gui.LobbyGUI;

public class InventoryClicked implements Listener
{
	public InventoryClicked(minigamesMain plugin)
	{
		Bukkit.getPluginManager().registerEvents(this, plugin);
		Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.GREEN + " InventoryClicked loaded");
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		if (e.getCurrentItem() == null)
		{
			return;
		}

		if (e.getCurrentItem().hasItemMeta() == false)
		{
			return;
		}

		String title = e.getView().getTitle();

		if (title.equals(LobbyGUI.getInventoryName()))
		{
			e.setCancelled(true);
			if (e.getCurrentItem() == null)
			{
				return;
			}
			LobbyGUI.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
		}
	}
}
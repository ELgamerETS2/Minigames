package Minigames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import Minigames.minigamesMain;
import Minigames.gui.HideStatsGUI;
import Minigames.gui.MenuGUI;
import Minigames.gui.RiverRaceStatsGUI;
import Minigames.gui.StatsGUI;

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

		//If in the Menu GUI
		if (title.equals(MenuGUI.getInventoryName()))
		{
			e.setCancelled(true);
			if (e.getCurrentItem() == null)
			{
				return;
			}
			MenuGUI.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
		}
		
		//If in the Stats GUI
		else if (title.equals(StatsGUI.getInventoryName()))
		{
			e.setCancelled(true);
			if (e.getCurrentItem() == null)
			{
				return;
			}
			StatsGUI.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
		}
		
		//If in the HideStats GUI
		else if (title.equals(HideStatsGUI.getInventoryName()))
		{
			e.setCancelled(true);
			return;
		}
		
		//If in the RiverRaceStats GUI
		else if (title.equals(RiverRaceStatsGUI.getInventoryName()))
		{
			e.setCancelled(true);
			return;
		}
	}
}
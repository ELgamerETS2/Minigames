package Minigames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.SkullMeta;

import Minigames.minigamesMain;
import Minigames.gui.MenuGUI;
import Minigames.gui.StatsGUI;

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
		SkullMeta skullMeta;
		Player player;
		
		player = e.getPlayer();
		
		if (player.getOpenInventory().getType() != InventoryType.CRAFTING && e.getPlayer().getOpenInventory().getType() != InventoryType.CREATIVE)
		{
		    return;
		}
		
		if (player.getInventory().getItemInMainHand().equals(minigamesMain.menu))
		{
			e.setCancelled(true);
			player.openInventory(MenuGUI.GUI(e.getPlayer()));
		}
		
		else if (player.getInventory().getItemInMainHand().hasItemMeta())
		{
			if (player.getInventory().getItemInMainHand().getItemMeta() instanceof SkullMeta)
			{
				skullMeta = (SkullMeta) (player.getInventory().getItemInMainHand().getItemMeta());
				if (skullMeta.getOwningPlayer().getUniqueId().equals(player.getUniqueId()))
				{
					e.setCancelled(true);
					player.openInventory(StatsGUI.GUI(player));
				}
			}
		}
	}
}

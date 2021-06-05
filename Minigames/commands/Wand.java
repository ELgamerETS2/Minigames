package Minigames.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import Minigames.minigamesMain;

public class Wand implements Listener
{
	private ItemStack IS;
	private minigamesMain plugin;
	
	public Wand(minigamesMain plugin)
	{
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.GREEN + " Wand loaded");
		IS = new ItemStack(Material.DIAMOND_AXE);
	}

	@EventHandler
	public void interactEvent(PlayerInteractEvent e)
	{
		if (!e.getPlayer().hasPermission("Minigames.boatchecks"))
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RRWand] Player doesn't have perms so interact ignored");
			return;
		}
		if (e.getAction() != Action.LEFT_CLICK_BLOCK)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [RRWand] Do left click so interact ignored");
			return;
		}
		if (e.getPlayer().getInventory().getItemInHand().equals(IS))
		{
			e.setCancelled(true);
			for (int i = 0 ; i < plugin.selections.size() ; i++)
			{
				if (e.getPlayer().getUniqueId().equals(plugin.selections.get(i).getUUID()))
				{
					plugin.selections.get(i).addBlock(e.getClickedBlock());
					e.getPlayer().sendMessage(ChatColor.BLUE +"Block added");
				}
			}
		}
	}

}

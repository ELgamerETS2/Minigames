package Minigames.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Minigames.minigamesMain;

public class MenuGUI
{
	private static Inventory inv;
	private static String inventory_name;
	private static int inv_rows = 3 * 9;
	
	//Code for the method was taken from ElgamerYT
	public static String getInventoryName()
	{
		return inventory_name;
	}
	
	//Code for the method was taken from ElgamerYT
	public static void initialize()
	{
		inventory_name = ChatColor.GREEN + "" + ChatColor.BOLD + "Minigames Menu";

		inv = Bukkit.createInventory(null, inv_rows);
	}
	
	//Some code for this method was taken from ElgamerYT
	public static Inventory GUI (Player p)
	{
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, getInventoryName());

		inv.clear();

		createItem(inv, Material.LADDER, 1, 12, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Hide and Seek");

		createItem(inv, Material.OAK_BOAT, 1, 16, ChatColor.BLUE + "" + ChatColor.BOLD + "River Race");

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(Player player, int slot, ItemStack clicked, Inventory inv)
	{
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Hide and Seek"))
		{
			//Takes player out of current game
			if (minigamesMain.getInstance().HSLobby.gameIsRunning)
			{
				minigamesMain.getInstance().HSLobby.HideGame.playerLeave(player);
			}
			//Puts player in hide and seek
			minigamesMain.getInstance().HSLobby.playerJoinLobby(player);
			player.closeInventory();
		}
		else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "" + ChatColor.BOLD + "River Race"))
		{
			minigamesMain.getInstance().RRLobby.playerJoinLobby(player);
			player.closeInventory();
		}
	}
	
	//Code for the method was taken from ElgamerYT
	public static ItemStack createItem(Inventory inv, Material material, int amount, int invSlot, String displayName, String... loreString)
	{
		ItemStack item;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<String> lore = new ArrayList();
		
		item = new ItemStack(material, amount);
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		for (String s : loreString) {
			lore.add(s);
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		inv.setItem(invSlot - 1,  item);
		
		return item;
	}
}

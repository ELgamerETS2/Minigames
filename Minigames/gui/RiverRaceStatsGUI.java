package Minigames.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class RiverRaceStatsGUI
{
	private static Inventory inv;
	private static String inventory_name;
	private static int inv_rows = 3 * 9;
	
	public static String getInventoryName()
	{
		return inventory_name;
	}
	
	public static void initialize()
	{
		inventory_name = ChatColor.BLUE + "" + ChatColor.BOLD + "River Race Stats";

		inv = Bukkit.createInventory(null, inv_rows);
	}
	
	public static Inventory GUI (Player p)
	{
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, getInventoryName());

		inv.clear();

		Utils.createItem(inv, Material.LADDER, 1, 12, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Hide and Seek");
				
		Utils.createItem(inv, Material.OAK_BOAT, 1, 16, ChatColor.BLUE + "" + ChatColor.BOLD + "River Race");

		toReturn.setContents(inv.getContents());
		return toReturn;
	}
}

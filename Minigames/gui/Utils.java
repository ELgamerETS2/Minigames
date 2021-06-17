package Minigames.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Utils
{
	//Code for the method was taken from ElgamerYT
	public static ItemStack createPlayerSkull(Inventory inv, Player p, int amount, int invSlot, String displayName, String... loreString) {

		ItemStack item;

		List<String> lore = new ArrayList<String>();

		item = new ItemStack(Material.PLAYER_HEAD);

		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName(displayName);
		for (String s : loreString) {
			lore.add(s);
		}
		meta.setLore(lore);
		meta.setOwningPlayer(p);
		item.setItemMeta(meta);

		inv.setItem(invSlot - 1,  item);

		return item;
	}
	
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
	
	//Code for the method was taken from ElgamerYT
	public static ItemStack createPlayerSkull(Player p, int amount, int invSlot, String displayName, String... loreString) {

		ItemStack item;

		List<String> lore = new ArrayList<String>();
		
		//Creates new player head item stack
		item = new ItemStack(Material.PLAYER_HEAD);

		//Sets the meta data to that of a skull meta data
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		
		//Sets the display names
		meta.setDisplayName(displayName);
		for (String s : loreString) {
			lore.add(s);
		}
		meta.setLore(lore);
		meta.setOwningPlayer(p);
		item.setItemMeta(meta);
		
		return item;
	}
}

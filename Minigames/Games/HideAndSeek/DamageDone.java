
/**
 * @author 14walkerg
 * @date 30 Jan 2021
 * @time 11:18:21
 */

package Minigames.Games.HideAndSeek;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import Minigames.Announce;
import Minigames.minigamesMain;
import Minigames.Games.HideAndSeek.HideAndSeekGame;

public class DamageDone implements Listener
{
	private final minigamesMain mainPlugin;
	private final HideAndSeekGame plugin;
	
	public DamageDone(HideAndSeekGame plugin, minigamesMain mainPlugin)
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "DamageDone loaded");
		this.mainPlugin = mainPlugin;
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(this, mainPlugin);
	}
	
	@EventHandler
	public void Damage(EntityDamageByEntityEvent event)
	{
		int i, j;
		
		//Checks whether it is a PVP event
		if (event.getDamager().getType() == EntityType.PLAYER && event.getEntity().getType() == EntityType.PLAYER)
		{
			//Convert damager to player
			Player finder = (Player) event.getDamager();
			Player hider = (Player) event.getEntity();
			HideAndSeekFinder updatedFinder;
			
			//Go through seekers list until it finds the seeker
			for (i = 0 ; i < plugin.finders.size() ; i++)
			{
				//Identifies the actual seeker who found someone
				if (plugin.finders.get(i).player.equals(finder))
				{
					//Increments the amount of people found for this finder by 1
					updatedFinder = new HideAndSeekFinder(finder, plugin.finders.get(i).iFound + 1);
					plugin.finders.set(i, updatedFinder);
					
					//Creates a new finder object
					HideAndSeekFinder newFinder = new HideAndSeekFinder(hider);
					//Adds this to the finders
					plugin.finders.add(newFinder);
					
					//Removes the new finder from the hiders list
					for (j = 0 ; j < plugin.hiders.size() ; j++)
					{
						if (plugin.hiders.get(i).player.equals(hider))
						{
							plugin.hiders.remove(i);
							break;
						}
					}
					break;
				}
			}
			
			//Announces to all players that a player has been found
			Announce.announce(this.plugin.players, (ChatColor.BLUE +hider.getCustomName() +ChatColor.DARK_PURPLE +" was found by "+ChatColor.BLUE +finder.getCustomName()));
			
			if (plugin.hiders.size() == 0)
			{
				this.plugin.bTerminate = true;
			}			
		}
	}
} //End Class

//Created by Bluecarpet in London
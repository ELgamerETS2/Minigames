
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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import Minigames.Announce;
import Minigames.minigamesMain;
import Minigames.Games.HideAndSeek.HideAndSeekGame;

public class DamageDone implements Listener
{
	private final minigamesMain mainPlugin;
	private final HideAndSeekGame HideAndSeekGame;
	
	public DamageDone(HideAndSeekGame plugin, minigamesMain mainPlugin)
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Minigames] [Hide] DamageDone loaded");
		this.mainPlugin = mainPlugin;
		this.HideAndSeekGame = plugin;
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
			//	- If the seeker isn't found, nothing will happen
			for (i = 0 ; i < HideAndSeekGame.finders.size() ; i++)
			{
				//Identifies the actual seeker who found someone
				if (HideAndSeekGame.finders.get(i).player.equals(finder))
				{
					//Identifies the hider
					for (j = 0 ; j < HideAndSeekGame.hiders.size() ; j++)
					{
						if (HideAndSeekGame.hiders.get(j).player.equals(hider))
						{
							//Increments the amount of people found for this finder by 1
							updatedFinder = new HideAndSeekFinder(finder, HideAndSeekGame.finders.get(i).iFound + 1);
							HideAndSeekGame.finders.set(i, updatedFinder);
							
							//Updates scoreboard score for finder
							HideAndSeekGame.HSL.Found.getScore(HideAndSeekGame.finders.get(i).player.getName()).setScore(HideAndSeekGame.finders.get(i).iFound);
							
							int iCurrentFinders = HideAndSeekGame.finders.size();
							
							//Creates a new finder object. Hider, people found, finders when found
							HideAndSeekFinder newFinder = new HideAndSeekFinder(hider, 0, iCurrentFinders);
							
							//Adds this to the finders
							HideAndSeekGame.finders.add(newFinder);
							
							//Set score to 0
							HideAndSeekGame.HSL.Found.getScore(hider.getDisplayName()).setScore(0);
							hider.setWalkSpeed((float) mainPlugin.getConfig().getDouble("SeekerSpeed"));
		        			
							//Announces to all players that a player has been found
							Announce.announce(this.HideAndSeekGame.getPlayers(), ChatColor.BLUE +hider.getDisplayName() +ChatColor.GREEN +" was found by "+ChatColor.BLUE +finder.getDisplayName());
							Announce.announce(this.HideAndSeekGame.getPlayers(), hider.getDisplayName() +ChatColor.GOLD +" is now a seeker");
							
							HideAndSeekGame.hiders.remove(j);
							break;
						}
					}
					break;
				}
			}
			
			if (HideAndSeekGame.bTerminate == true)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] [DamageDone] Tried and prevented terminating game when game was not running");
			}
			else if (HideAndSeekGame.hiders.size() == 0)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] [DamageDone] Terminating game now there are no hiders left");
				this.HideAndSeekGame.terminate();
			}			
		}
	}
	
	@EventHandler
	public void Environment(EntityDeathEvent event)
	{
		int j;
		
		if (event.getEntityType() == EntityType.PLAYER)
		{
			Player hider = (Player) event.getEntity();
			
			//Identifies the hider
			for (j = 0 ; j < HideAndSeekGame.hiders.size() ; j++)
			{
				if (HideAndSeekGame.hiders.get(j).player.equals(hider))
				{					
					int iCurrentFinders = HideAndSeekGame.finders.size();
					
					//Creates a new finder object. Hider, people found, finders when found
					HideAndSeekFinder newFinder = new HideAndSeekFinder(hider, 0, iCurrentFinders);
					
					//Adds this to the finders
					HideAndSeekGame.finders.add(newFinder);
					
					//Set score to 0
					HideAndSeekGame.HSL.Found.getScore(hider.getName()).setScore(0);
					
					//Announces to all players that a player has been killed
					Announce.announce(this.HideAndSeekGame.getPlayers(), ChatColor.BLUE +hider.getDisplayName() +ChatColor.GREEN +" died");
					Announce.announce(this.HideAndSeekGame.getPlayers(), hider.getDisplayName() +ChatColor.GOLD +" is now a seeker");
					
					HideAndSeekGame.hiders.remove(j);
					break;
				}
			}
		}
	}
	
	public void unRegister()
	{
		HandlerList.unregisterAll(this);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Minigames] [Hide] DamageDone unregistered");
	}
} //End Class
//Created by Bluecarpet in London
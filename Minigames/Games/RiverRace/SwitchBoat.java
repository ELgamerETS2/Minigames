package Minigames.Games.RiverRace;

import org.bukkit.Bukkit;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import Minigames.minigamesMain;

public class SwitchBoat implements Listener
{
	private minigamesMain CorePlugin;
	private RiverRaceRacer racer;
	
	public SwitchBoat(minigamesMain CorePlugin, RiverRaceRacer racer)
	{
		this.CorePlugin = CorePlugin;
		this.racer = racer;
		Bukkit.getPluginManager().registerEvents(this, this.CorePlugin);
	}
	
	@EventHandler
	public void playerLeaveBoat(VehicleExitEvent event)
	{
		if (!(event.getExited() instanceof Player))
			return;

		if (!(event.getVehicle() instanceof Boat))
			return;

		if (!((Player) event.getExited()).getUniqueId().equals(racer.getUUID()))
			return;
		else
			racer.removeBoat();
	}
	
	@EventHandler
	public void playerEnterBoat(VehicleEnterEvent event)
	{
		if (!(event.getEntered() instanceof Player))
			return;

		if (!(event.getVehicle() instanceof Boat))
			return;

		if (!((Player) event.getEntered()).getUniqueId().equals(racer.getUUID()))
			return;
		else
			racer.addBoat((Boat) event.getVehicle());
	}
	
	public void unRegister()
	{
		HandlerList.unregisterAll(this);
	}
}

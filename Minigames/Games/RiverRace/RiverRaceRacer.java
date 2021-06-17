package Minigames.Games.RiverRace;

import java.util.UUID;

import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
/* import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
*/
import Minigames.minigamesMain;

public class RiverRaceRacer
{
	private Player player;
	private Boat boat;
	private int iCheckpoint;
	
	private SwitchBoat switcher;
		
	public RiverRaceRacer(Player player, Boat boat, minigamesMain plugin)
	{
		this.player = player;
		this.boat = boat;
		boat.addPassenger(player);
		iCheckpoint = 0;
				
		switcher = new SwitchBoat(plugin, this);
	}
	
	public void increaseBoatSpeed(Double dSpeed)
	{
	//	boat.setMaxSpeed(dSpeed);
	}
	
	public UUID getUUID()
	{
		return player.getUniqueId();
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public int getCheckpoint()
	{
		return iCheckpoint;
	}
	
	public void updateScore(int iNewScore)
	{
	//	Scoreboard SB = player.getScoreboard();
	//	Objective Checks = SB.getObjective(DisplaySlot.SIDEBAR);
	//	Score newScore = Checks.getScore(player.getName());
	//	newScore.setScore(iNewScore);
	}
	
	public void unBoard()
	{
		switcher.unRegister();
		if (boat !=null)
		{
			boat.eject();
			boat.remove();
		}
	}
	
	public void removeBoat()
	{
		try
		{
			boat = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addBoat(Boat boat)
	{
		this.boat = boat;
	}
}

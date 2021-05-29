package Minigames.Games.RiverRace;

import java.util.UUID;

import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class RiverRaceRacer
{
	private Player player;
	private Boat boat;
	private int iCheckpoint;
	
	public RiverRaceRacer(Player player, Boat boat)
	{
		this.player = player;
		this.boat = boat;
		boat.setPassenger(player);
		iCheckpoint = 0;
	}
	
	public void increaseBoatSpeed(Double dSpeed)
	{
		boat.setMaxSpeed(2D);
	}
	
	public UUID getUUID()
	{
		return player.getUniqueId();
	}
	
	public int getCheckpoint()
	{
		return iCheckpoint;
	}
	
	public void updateScore(int iNewScore)
	{
		Scoreboard SB = player.getScoreboard();
		Objective Checks = SB.getObjective(DisplaySlot.SIDEBAR);
		Score newScore = Checks.getScore(player.getName());
		newScore.setScore(iNewScore);
	}
}

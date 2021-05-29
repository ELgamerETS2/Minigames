package Minigames.Games.HideAndSeek;

import org.bukkit.entity.Player;

public class HideAndSeekPlayer
{
	public Player player;
	public boolean isFound;
	public int iPoints;
	
	public HideAndSeekPlayer(Player player)
	{
		this.player = player;
		this.iPoints = 0;
	}
	
	public HideAndSeekPlayer()
	{
		this.iPoints = 0;
	}
}

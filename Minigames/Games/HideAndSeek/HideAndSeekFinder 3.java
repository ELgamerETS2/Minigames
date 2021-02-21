package Minigames.Games.HideAndSeek;

import org.bukkit.entity.Player;

public class HideAndSeekFinder extends HideAndSeekPlayer
{
	public int iFound;
	
	public HideAndSeekFinder(Player player)
	{
		this.player = player;
		this.iFound = 0;
	}
	
	public HideAndSeekFinder(Player player, int iFound)
	{
		this.player = player;
		this.iFound = iFound;
	}
}

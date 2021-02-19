package Minigames;

import org.bukkit.entity.Player;

public class Announce
{
	public static void announce(Player[] players, String szMessage)
	{
		for (int i = 0 ; i < players.length ; i++)
		{
			players[i].sendMessage(szMessage);
		}
	}
}

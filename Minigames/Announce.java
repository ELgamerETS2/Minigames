package Minigames;

import java.util.ArrayList;

import org.bukkit.Sound;
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
	
	public static void announce(ArrayList<Player> players, String szMessage)
	{
		for (int i = 0 ; i < players.size() ; i++)
		{
			players.get(i).sendMessage(szMessage);
		}
	}
	
	public static void playNote(Player[] players, Sound Sound)
	{
		for (int i = 0 ; i < players.length ; i++)
		{
		//	players[i].playSound(players[i].getLocation(), Sound, 1.0F, 1.0F);
		}
	}
}

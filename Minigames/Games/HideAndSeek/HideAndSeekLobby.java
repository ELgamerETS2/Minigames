package Minigames.Games.HideAndSeek;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import Minigames.Announce;
import Minigames.minigamesMain;

public class HideAndSeekLobby
{
	public minigamesMain CorePlugin;
	public Location lobbyLocation;
	public List<Player> players;
	public int iFinders;
	public HideAndSeekMap Map;
	public HideAndSeekGame game;
	
	public HideAndSeekLobby(minigamesMain CorePlugin)
	{
		this.CorePlugin = CorePlugin;
		this.iFinders = 0;
		this.Map = new HideAndSeekMap();
		players = new ArrayList<Player>();
		Bukkit.getConsoleSender().sendMessage("[Minigames]" +ChatColor.GREEN +" Hide and seek lobby created");
		this.lobbyLocation = new Location(this.CorePlugin.getServer().getWorld("Earth Builds 2"), 10000, 65, 0);
	}
	
	public void playerJoinLobby(Player player)
	{
		if (players.contains(player))
		{
			player.sendMessage(ChatColor.RED +"You are already in the side and seek lobby");
		}
		else
		{
			player.teleport(lobbyLocation);
			players.add(player);
			player.sendMessage(ChatColor.LIGHT_PURPLE +"Welcome to the Hide and Seek lobby");
			Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +player.getCustomName() +" has joined the Hide and Seek lobby"));
			if (players.size() == 1)
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" player in lobby"));
			else
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" players in lobby"));
		}
	}
	
	public void playerLeaveLobby(Player player)
	{
		if (players.contains(player))
		{
			players.remove(player);
			if (players.size() == 1)
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" player in lobby"));
			else
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" players in lobby"));
		}
	}
	
	public void lobbyRun()
	{
	/*	while (players.size() < 4)
		{
			
		}
	*/
		iFinders = 1;
		
		game = new HideAndSeekGame((Player[]) players.toArray(new Player[players.size()]), iFinders, Map.iMapID, this.CorePlugin);
		
	//	players = new ArrayList<Player>();
		
		game.highGameProcesses();
	}
}
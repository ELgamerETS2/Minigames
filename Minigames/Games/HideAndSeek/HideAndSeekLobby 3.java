package Minigames.Games.HideAndSeek;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import Minigames.minigamesMain;

public class HideAndSeekLobby
{
	public minigamesMain CorePlugin;
	public List<Player> players;
	public int iFinders;
	public HideAndSeekMap Map;
	
	public HideAndSeekLobby(minigamesMain CorePlugin)
	{
		this.CorePlugin = CorePlugin;
		this.iFinders = 0;
		this.Map = new HideAndSeekMap();
		players = new ArrayList<Player>();
	}
	
	public void playerJoinLobby(Player player)
	{
		players.add(player);
	}
	
	public void lobbyRun()
	{
		while (players.size() < 4)
		{
			
		}
		
		HideAndSeekGame game = new HideAndSeekGame((Player[]) players.toArray(), iFinders, Map.iMapID, this.CorePlugin);
		
		game.highGameProcesses();
	}
}
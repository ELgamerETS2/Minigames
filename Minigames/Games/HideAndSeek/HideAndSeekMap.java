package Minigames.Games.HideAndSeek;

import org.bukkit.World;

public class HideAndSeekMap
{
	public int iMapID;
	public String szName;
	public String szCreator;
	public World mapWorld;
	public double[] spawnCoordinates;
	
	//Contructors
	public HideAndSeekMap(int iMap)
	{
		this.iMapID = iMap;
	}
	
	public HideAndSeekMap()
	{
		this.iMapID = 0;
		this.spawnCoordinates = new double[3];
		this.szCreator = "";
		this.szName = "";
	}
	
	//SQL setters
	public void setMapFromMapID()
	{
		
	}
}

package Minigames.Games.RiverRace;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class RRSelection
{
	Player player;
	ArrayList<Block> blocks;
	
	public RRSelection (Player player)
	{
		this.player = player;
		blocks = new ArrayList<Block>();
	}
	
	public UUID getUUID()
	{
		return player.getUniqueId();
	}
	
	public void addBlock(Block block)
	{
		blocks.add(block);
	}
	
	public ArrayList<Block> getBlocks()
	{
		return blocks;
	}
	
	public void reset()
	{
		blocks = new ArrayList<Block>();
	}
}

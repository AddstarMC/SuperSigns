package au.com.addstar.signmaker;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

public class StoredBlocks
{
	private int mWidth;
	private int mHeight;
	private BlockFace mFacing;
	
	private MaterialData[] mBlocks;
	
	private Location mLocation;
	
	public StoredBlocks(int width, int height, BlockFace facing)
	{
		mWidth = width;
		mHeight = height;
		mFacing = facing;
		
		mBlocks = new MaterialData[width*height];
	}
	
	public int getWidth()
	{
		return mWidth;
	}
	
	public int getHeight()
	{
		return mHeight;
	}
	
	public void setLocation(Location loc)
	{
		mLocation = loc;
	}
	
	public Location getLocation()
	{
		return mLocation;
	}
	
	public MaterialData getBlock(int x, int y)
	{
		return mBlocks[x + y * mWidth];
	}
	
	public void setBlock(int x, int y, MaterialData block)
	{
		mBlocks[x + y * mWidth] = block;
	}
	
	public void apply()
	{
		Validate.notNull(mLocation, "No location is set.");
		apply(mLocation);
	}
	
	public void apply(Location location)
	{
		for(int x = 0; x < mWidth; ++x)
		{
			for(int y = 0; y < mHeight; ++y)
			{
				MaterialData mat = mBlocks[x + y * mWidth];
				if(mat == null || mat.getItemType() == Material.AIR)
					continue;
				
				Block block = location.getWorld().getBlockAt(location.getBlockX() + (x * mFacing.getModX()), location.getBlockY() + y, location.getBlockZ() + (x * mFacing.getModZ()));
				block.setType(mat.getItemType());
				
				BlockState state = block.getState();
				state.setData(mat);
				state.update(true);
			}
		}
	}
}

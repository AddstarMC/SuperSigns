package au.com.addstar.signmaker.transitions;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import au.com.addstar.signmaker.StoredBlocks;

public class Cover extends AbstractTransition
{
	private StoredBlocks[] mOld;
	private StoredBlocks[] mNew;
    
    private World mWorld;
	
	private int mOffset;
	private int mTotal;
	private boolean mUp;
	
	public Cover(boolean up)
	{
		mOffset = 0;
		mUp = up;
	}
	
	@Override
	public void setOriginal( StoredBlocks[] original, Material mat )
	{
	    setMaterial(mat);
		mOld = original;
		if(original.length > 0)
		{
			mTotal = original[0].getHeight()+1;
			mWorld = original[0].getLocation().getWorld();
		}
	}

	@Override
	public void setNew( StoredBlocks[] blocks )
	{
		mNew = blocks;
		if(blocks.length > 0)
		{
			mTotal = blocks[0].getHeight()+1;
			mWorld = blocks[0].getLocation().getWorld();
		}
	}

	@Override
	public boolean isDone()
	{
		return mOffset >= mTotal;
	}

	@Override
	public void doStep()
	{
		++mOffset;
		
		int lines = Math.max(mOld.length, mNew.length);
		
		for(int l = 0; l < lines; ++l)
		{
			StoredBlocks old = (l < mOld.length ? mOld[l] : null);
			StoredBlocks current = (l < mNew.length ? mNew[l] : null);
			
			int width = (old != null ? old.getWidth() : 0);
			width = Math.max(width, (current != null ? current.getWidth() : 0));
			
			if(old != null)
			{
				int dstX = old.getLocation().getBlockX();
				int dstY = old.getLocation().getBlockY();
				int dstZ = old.getLocation().getBlockZ();
				BlockFace face = old.getFacing();
				
				for(int x = 0; x < old.getWidth(); ++x)
				{
					if(mUp)
					{
						Block dest = mWorld.getBlockAt(dstX + (x * face.getModX()), dstY + (mOffset-1), dstZ + (x * face.getModZ()));
						dest.setType(Material.AIR);
					}
					else
					{
						Block dest = mWorld.getBlockAt(dstX + (x * face.getModX()), dstY + (old.getHeight() - mOffset), dstZ + (x * face.getModZ()));
						dest.setType(Material.AIR);
					}
				}
			}
			
			// Draw new
			if(current != null)
			{
				int dstX = current.getLocation().getBlockX();
				int dstY = current.getLocation().getBlockY();
				int dstZ = current.getLocation().getBlockZ();
				BlockFace face = current.getFacing();
				
				for(int x = 0; x < current.getWidth(); ++x)
				{
					if(mUp)
					{
						for(int y = current.getHeight() - mOffset + 1; y < current.getHeight(); ++y)
						{
							BlockData data = current.getBlock(x, y);
							Block dest = mWorld.getBlockAt(dstX + (x * face.getModX()), dstY - current.getHeight() + mOffset + y - 1, dstZ + (x * face.getModZ()));
							updateBlock(data,dest);
						}
					}
					else
					{
						for(int y = 0; y < current.getHeight() && y < mOffset - 1; ++y)
						{
							BlockData data = current.getBlock(x, y);
							Block dest = mWorld.getBlockAt(dstX + (x * face.getModX()), dstY + (current.getHeight() - mOffset + 1 + y), dstZ + (x * face.getModZ()));
							updateBlock(data,dest);
						}
					}
				}
			}
			
		}
	}

}

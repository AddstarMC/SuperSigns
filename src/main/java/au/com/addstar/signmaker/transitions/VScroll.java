package au.com.addstar.signmaker.transitions;

import org.bukkit.Material;
import org.bukkit.World;

import au.com.addstar.signmaker.StoredBlocks;

public class VScroll extends AbstractTransition
{
	private StoredBlocks[] mOld;
	private StoredBlocks[] mNew;
	
	private World mWorld;
	
	private int mOffset;
	private int mTotal;
	private boolean mUp;
	
	public VScroll(boolean up)
	{
		mOffset = 0;
		mUp = up;
	}
	
	@Override
	public void setOriginal( StoredBlocks[] original ,Material material)
	{
		setMaterial(material);
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
			
			// Draw old
			updateOldBlocks(old,mWorld,mOffset,mUp);
			
			// Draw new
            updateCurrentBlocks(current,mWorld,mOffset,mUp);
			
		}
	}


}

package au.com.addstar.signmaker.transitions;

import org.bukkit.material.MaterialData;

import au.com.addstar.signmaker.StoredBlocks;

public interface Transition
{
	public void setOriginal(StoredBlocks[] original);
	public void setNew(StoredBlocks[] blocks);
	public void setMaterial(MaterialData mat);
	
	public boolean isDone();
	public void doStep();
}

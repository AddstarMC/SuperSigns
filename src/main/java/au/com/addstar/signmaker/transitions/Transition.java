package au.com.addstar.signmaker.transitions;

import org.bukkit.Material;

import au.com.addstar.signmaker.StoredBlocks;

public interface Transition
{
	public void setOriginal(StoredBlocks[] original);
	public void setNew(StoredBlocks[] blocks);
	public void setMaterial(Material mat);
	
	public boolean isDone();
	public void doStep();
}

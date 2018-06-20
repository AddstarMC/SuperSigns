package au.com.addstar.signmaker.transitions;

import org.bukkit.Material;

import au.com.addstar.signmaker.StoredBlocks;

public interface Transition
{
	void setOriginal(StoredBlocks[] original, Material material);
	void setNew(StoredBlocks[] blocks);
	void setMaterial(Material mat);
	Material getMaterial();
	boolean isDone();
	void doStep();
}

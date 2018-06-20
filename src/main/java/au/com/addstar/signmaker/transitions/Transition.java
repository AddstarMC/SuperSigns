package au.com.addstar.signmaker.transitions;

import au.com.addstar.signmaker.SignMakerPlugin;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;

import au.com.addstar.signmaker.StoredBlocks;

public interface Transition
{
	public void setOriginal(StoredBlocks[] original, Material material);
	public void setNew(StoredBlocks[] blocks);
	public void setMaterial(Material mat);
	public Material getMaterial();
	public boolean isDone();
	public void doStep();
}

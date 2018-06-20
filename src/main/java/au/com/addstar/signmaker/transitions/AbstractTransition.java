package au.com.addstar.signmaker.transitions;

import au.com.addstar.signmaker.SignMakerPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 20/06/2018.
 */
public abstract class AbstractTransition implements Transition {
    
    private Material material;
    
    protected void updateBlock(BlockData data, Block dest) {
        if(data == null || data.getMaterial() == Material.AIR)
            dest.setType(Material.AIR);
        else
        {
            dest.setType(data.getMaterial());
            BlockState state = dest.getState();
            state.setBlockData(data);
            state.update(true);
        }
    }
    
    //Override to support changing Materials
    @Override
    public void setMaterial( Material mat )	{
        material = mat;
    }
    
    @Override
    public Material getMaterial(){
        return material;
    }
}

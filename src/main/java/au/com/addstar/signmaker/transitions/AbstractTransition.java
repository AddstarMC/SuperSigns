package au.com.addstar.signmaker.transitions;

import au.com.addstar.signmaker.StoredBlocks;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
    
    public void updateCurrentBlocks(StoredBlocks current, World mWorld, int mOffset, boolean mUp){
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
    
    public void updateOldBlocks(StoredBlocks old, World mWorld, int mOffset, boolean mUp ){
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
                    for(int y = 0; y < old.getHeight() - mOffset; ++y)
                    {
                        BlockData data = old.getBlock(x, y);
                        Block dest = mWorld.getBlockAt(dstX + (x * face.getModX()), dstY + y + mOffset, dstZ + (x * face.getModZ()));
                        updateBlock(data, dest);
                    }
                    
                    Block dest = mWorld.getBlockAt(dstX + (x * face.getModX()), dstY + (mOffset-1), dstZ + (x * face.getModZ()));
                    dest.setType(Material.AIR);
                }
                else
                {
                    for(int y = mOffset; y < old.getHeight(); ++y)
                    {
                        BlockData data = old.getBlock(x, y);
                        Block dest = mWorld.getBlockAt(dstX + (x * face.getModX()), dstY + (y - mOffset), dstZ + (x * face.getModZ()));
                        updateBlock(data, dest);
                    }
                    
                    Block dest = mWorld.getBlockAt(dstX + (x * face.getModX()), dstY + (old.getHeight() - mOffset), dstZ + (x * face.getModZ()));
                    dest.setType(Material.AIR);
                }
            }
        }
    }
}

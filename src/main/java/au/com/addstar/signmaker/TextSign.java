package au.com.addstar.signmaker;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class TextSign
{
	private World mWorld;
	private BlockVector mMinimum;
	private BlockVector mMaximum;
	private BlockVector mOrigin;
	
	private Material mMaterial;
	private BlockFace mFace;
	private Justification mJustification;
	private String mFont;
	private String mText;
	
	private TextSign() {}
	public TextSign(Location location, BlockFace face, String font)
	{
		mOrigin = new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		mWorld = location.getWorld();
		mFace = face;
		mFont = font;
	}
	
	public void clear()
	{
		if(mMinimum == null)
			return;
		
		for(int x = mMinimum.getBlockX(); x <= mMaximum.getBlockX(); ++x)
		{
			for(int z = mMinimum.getBlockZ(); z <= mMaximum.getBlockZ(); ++z)
			{
				for(int y = mMinimum.getBlockY(); y <= mMaximum.getBlockY(); ++y)
				{
					mWorld.getBlockAt(x, y, z).setType(Material.AIR);
				}
			}
		}
	}
	
	public void redraw()
	{
		clear();
		
		CharSet font = TextWriter.getFont(mFont);
		if(font == null || mMaterial == null || mText == null || mJustification == null)
			return;
		
		TextWriter.writeText(mText, mOrigin.toLocation(mWorld), mFace, mJustification, font, mMaterial);
		
		int size = TextWriter.getWidth(mText, font);
		int height = TextWriter.getHeight(mText, font);
		
		mMinimum = mOrigin.clone();
		mMaximum = mOrigin.clone();
		
		switch(mJustification)
		{
		case Center:
			mMinimum.add(new Vector((size/2) * -mFace.getModX(), 0, (size/2) * -mFace.getModZ()));
			mMaximum.add(new Vector((size - (size/2)) * mFace.getModX(), height, (size - (size/2)) * mFace.getModZ()));
			break;
		case Right:
			mMinimum.add(new Vector(size * -mFace.getModX(), 0, size * -mFace.getModZ()));
			mMaximum.add(new Vector(0, height, 0));
			break;
		case Left:
			mMaximum.add(new Vector(size * mFace.getModX(), height, size * mFace.getModZ()));
			break;
		}
		
		BlockVector min = new BlockVector(Math.min(mMinimum.getBlockX(), mMaximum.getBlockX()), mMinimum.getBlockY(), Math.min(mMinimum.getBlockZ(), mMaximum.getBlockZ()));
		mMaximum = new BlockVector(Math.max(mMinimum.getBlockX(), mMaximum.getBlockX()), mMaximum.getBlockY(), Math.max(mMinimum.getBlockZ(), mMaximum.getBlockZ()));
		mMinimum = min;
	}
	
	public void setText(String text)
	{
		mText = text;
	}
	
	public String getText()
	{
		return mText;
	}
	
	public void setMaterial(Material material)
	{
		mMaterial = material;
	}
	
	public Material getMaterial()
	{
		return mMaterial;
	}
	
	public void setJustification(Justification justification)
	{
		mJustification = justification;
	}
	
	public Justification getJustification()
	{
		return mJustification;
	}
	
	public void setFont(String font)
	{
		mFont = font;
	}
	
	public String getFont()
	{
		return mFont;
	}
	
	public void setFacing(BlockFace face)
	{
		mFace = face;
	}
	
	public BlockFace getFacing()
	{
		return mFace;
	}
	
	public void save(ConfigurationSection section)
	{
		if(mText != null)
			section.set("text", mText);
		section.set("world", mWorld.getName());
		
		if(mMaterial != null)
			section.set("material", mMaterial.name());
		
		section.set("face", mFace.name());
		
		if(mJustification != null)
			section.set("justification", mJustification.name());
		
		if(mFont != null)
			section.set("font", mFont);
		
		section.set("origin", mOrigin);
		section.set("min", mMinimum);
		section.set("max", mMaximum);
	}
	
	public void save(File file) throws IOException
	{
		YamlConfiguration config = new YamlConfiguration();
		save(config);
		config.save(file);
	}
	
	public static TextSign load(ConfigurationSection section) throws InvalidConfigurationException
	{
		TextSign sign = new TextSign();
		
		sign.mWorld = Bukkit.getWorld(section.getString("world"));
		sign.mOrigin = (BlockVector)section.get("origin");
		sign.mMinimum = (BlockVector)section.get("min");
		sign.mMaximum = (BlockVector)section.get("max");
		
		if(section.isString("text"))
			sign.setText(section.getString("text"));
		if(section.isString("material"))
			sign.setMaterial(Material.valueOf(section.getString("material")));
		sign.setFacing(BlockFace.valueOf(section.getString("face")));
		if(section.isString("justification"))
			sign.setJustification(Justification.valueOf(section.getString("justification")));
		if(section.isString("font"))
			sign.setFont(section.getString("font"));
		
		return sign;
	}
	
	public static TextSign load(File file) throws IOException, InvalidConfigurationException
	{
		YamlConfiguration config = new YamlConfiguration();
		config.load(file);
		return load(config);
	}
}

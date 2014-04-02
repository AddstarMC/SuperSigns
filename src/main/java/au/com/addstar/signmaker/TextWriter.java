package au.com.addstar.signmaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;

public class TextWriter
{
	public static int getWidth(String text, CharSet set)
	{
		CharDef space = set.getChar(' ');
		
		int size = 0;
		
		for(int i = 0; i < text.length(); ++i)
		{
			CharDef ch = set.getChar(text.charAt(i));
			if(ch == null)
				ch = space;
			
			size += ch.getWidth() + 1;
		}
		
		return size;
	}
	
	public static void writeText(String text, Location location, BlockFace face, Justification justification, CharSet set, Material material)
	{
		Validate.isTrue(face == BlockFace.EAST || face == BlockFace.WEST || face == BlockFace.NORTH || face == BlockFace.SOUTH, "Can only use North, East, South, or West direction.");
		CharDef space = set.getChar(' ');
		
		ArrayList<CharDef> chars = new ArrayList<CharDef>(text.length());
		
		int size = 0;
		
		for(int i = 0; i < text.length(); ++i)
		{
			CharDef ch = set.getChar(text.charAt(i));
			if(ch == null)
				ch = space;
			
			chars.add(ch);
			size += ch.getWidth() + 1;
		}
		
		switch(justification)
		{
		case Center:
			location.add((size/2) * -face.getModX(), 0, (size/2) * -face.getModZ());
			break;
		case Right:
			location.add(size * -face.getModX(), 0, size * -face.getModZ());
			break;
		case Left:
			break;
		}
		
		MaterialData[] types = new MaterialData[BlockType.values().length];
		
		int index = 0;
		for(BlockType type : BlockType.values())
			types[index++] = mapBlockType(type, face, material);
		
		int offset = 0;
		
		for(CharDef ch : chars)
		{
			for(int x = 0; x < ch.getWidth(); ++x)
			{
				for(int y = 0; y < set.getHeight(); ++y)
				{
					Block block = location.getWorld().getBlockAt(location.getBlockX() + face.getModX() * (offset + x), location.getBlockY() + y, location.getBlockZ() + face.getModZ() * (offset + x));
					
					BlockState state = block.getState();
					MaterialData data = types[ch.getType(x, y).ordinal()];
					state.setType(data.getItemType());
					state.setData(data);
					state.update(true);
				}
			}
			
			offset += ch.getWidth() + 1;
		}
	}
	
	private static Material getStairMaterial(Material material)
	{
		switch(material)
		{
		case COBBLESTONE:
			return Material.COBBLESTONE_STAIRS;
		case SMOOTH_BRICK:
			return Material.SMOOTH_STAIRS;
		case BRICK:
			return Material.BRICK_STAIRS;
		case WOOD:
			return Material.WOOD_STAIRS;
		case NETHER_BRICK:
			return Material.NETHER_BRICK_STAIRS;
			
		case QUARTZ_BLOCK:
		default:
			return Material.QUARTZ_STAIRS;
		}
	}
	
	private static MaterialData getSlabMaterial(Material material)
	{
		switch(material)
		{
		case COBBLESTONE:
			return new Step(material);
		case SMOOTH_BRICK:
			return new Step(material);
		case BRICK:
			return new Step(material);
		case WOOD:
			return new WoodenStep();
		case NETHER_BRICK:
			return new Step(material);
			
		case QUARTZ_BLOCK:
		default:
			return new Step(Material.QUARTZ_BLOCK);
		}
	}
	
	private static MaterialData mapBlockType(BlockType type, BlockFace face, Material material)
	{
		MaterialData data = null;
		
		switch(type)
		{
		case Empty:
			return new MaterialData(Material.AIR);
		case HalfLower:
			data = getSlabMaterial(material);
			break;
		case HalfUpper:
			data = getSlabMaterial(material);
			
			if(data instanceof WoodenStep)
				((WoodenStep)data).setInverted(true);
			else
				((Step)data).setInverted(true);
			break;
		case LeftLower:
			data = new Stairs(getStairMaterial(material));
			((Stairs)data).setFacingDirection(face);
			break;
		case LeftUpper:
			data = new Stairs(getStairMaterial(material));
			((Stairs)data).setFacingDirection(face);
			((Stairs)data).setInverted(true);
			break;
		case RightLower:
			data = new Stairs(getStairMaterial(material));
			((Stairs)data).setFacingDirection(face.getOppositeFace());
			break;
		case RightUpper:
			data = new Stairs(getStairMaterial(material));
			((Stairs)data).setFacingDirection(face.getOppositeFace());
			((Stairs)data).setInverted(true);
			break;
		case Solid:
			return new MaterialData(material);
		}
		
		return data;
	}
	
	public static BlockFace lookToFace(float yaw)
	{
		if(yaw <= -180)
			yaw += 360;

		if(yaw >= 180)
			yaw -= 360;

		if(yaw >= -45 && yaw <= 45)
			return BlockFace.SOUTH;
		else if(yaw > 45 && yaw < 135)
			return BlockFace.WEST;
		else if(yaw > -135 && yaw < -45)
			return BlockFace.EAST;
		else
			return BlockFace.NORTH;
	}
	
	public static BlockFace rotateRight(BlockFace face)
	{
		switch(face)
		{
		case NORTH:
			return BlockFace.EAST;
		case EAST:
			return BlockFace.SOUTH;
		case SOUTH:
			return BlockFace.WEST;
		case WEST:
		default:
			return BlockFace.NORTH;
		}
	}
	
	private static Map<String, CharSet> mFonts = new HashMap<String, CharSet>();
	
	public static CharSet getFont(String name)
	{
		return mFonts.get(name);
	}
	
	public static void reloadFonts()
	{
		mFonts.clear();
		for(File file : SignMakerPlugin.getFontFolder().listFiles())
		{
			try
			{
				CharSet font = CharSet.load(file);
				mFonts.put(font.getName(), font);
				System.out.println("Loaded Font " + font.getName());
			}
			catch(IllegalArgumentException e)
			{
				System.out.println("Failed to load font " + file.getPath());
				System.out.println(e.getMessage());
			}
			catch(IOException e)
			{
				System.out.println("Failed to load font " + file.getPath());
				e.printStackTrace();
			}
		}
		
		SignMakerPlugin plugin = SignMakerPlugin.instance;
		
		InputStream fontsList = plugin.getResource("fonts.txt");
		if(fontsList == null)
			return;
		
		// Load internal fonts
		BufferedReader reader = new BufferedReader(new InputStreamReader(fontsList));
		
		try
		{
			while(reader.ready())
			{
				String line = reader.readLine();
				String path = "fonts/" + line;
				InputStream fontFile = plugin.getResource(path);
				if(fontFile == null)
				{
					plugin.getLogger().warning("Font " + line + " was specified in the font list but was not found in the jar");
					continue;
				}
				
				try
				{
					CharSet font = CharSet.load(fontFile);
					if(!mFonts.containsKey(font.getName()))
					{
						mFonts.put(font.getName(), font);
						System.out.println("Loaded Font " + font.getName());
					}
				}
				catch(IllegalArgumentException e)
				{
					System.out.println("Failed to load internal font " + path);
					System.out.println(e.getMessage());
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}

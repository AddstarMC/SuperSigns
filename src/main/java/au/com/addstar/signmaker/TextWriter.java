package au.com.addstar.signmaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;

public class TextWriter
{
	public static int getWidth(String text, CharSet set)
	{
		CharDef space = set.getChar(' ');
		
		int max = 0;
		String[] lines = text.split("\n");
		
		for(String line : lines)
		{
			int size = 0;
			for(int i = 0; i < line.length(); ++i)
			{
				CharDef ch = set.getChar(line.charAt(i));
				if(ch == null)
					ch = space;
				
				size += ch.getWidth() + 1;
			}
			
			if(size > max)
				max = size;
		}
		
		return max;
	}
	
	public static int getHeight(String text, CharSet set)
	{
		int lines = text.split("\n").length;
		return set.getHeight() + (lines - 1) * (set.getHeight() + 1);
	}
	
	public static void writeText(String text, Location location, BlockFace face, Justification justification, CharSet set, Material material)
	{
		for(StoredBlocks blocks : makeText(text, location, face, justification, set, material))
			blocks.apply();
	}
	
	public static StoredBlocks[] makeText(String text, Location location, BlockFace face, Justification justification, CharSet set, Material material)
	{
		String[] lines = text.split("\n");
		StoredBlocks[] lineBlocks = new StoredBlocks[lines.length];
		
		int yOffset = (lines.length - 1) * (set.getHeight() + 1);
		int index = 0;
		
		for(String line : lines)
		{
			StoredBlocks blocks = makeText(line, face, set, material);
			Location loc = location.clone();
			
			switch(justification)
			{
			case Center:
				loc.add((blocks.getWidth()/2) * -face.getModX(), 0, (blocks.getWidth()/2) * -face.getModZ());
				break;
			case Right:
				loc.add(blocks.getWidth() * -face.getModX(), 0, blocks.getWidth() * -face.getModZ());
				break;
			case Left:
				break;
			}
			
			loc.add(0, yOffset, 0);
			blocks.setLocation(loc);
			
			lineBlocks[index++] = blocks;
			
			yOffset -= 1 + set.getHeight();
		}
		
		return lineBlocks;
	}
	
	public static StoredBlocks makeText(String text, BlockFace face, CharSet set, Material material)
	{
		Validate.isTrue(face == BlockFace.EAST || face == BlockFace.WEST || face == BlockFace.NORTH || face == BlockFace.SOUTH, "Can only use North, East, South, or West direction.");
		CharDef space = set.getChar(' ');
		
		MaterialData[] types = new MaterialData[BlockType.values().length];
		
		int index = 0;
		for(BlockType type : BlockType.values())
			types[index++] = mapBlockType(type, face, material);
		
		CharDef[] chars = new CharDef[text.length()];
			
		int size = 0;
		for(int i = 0; i < text.length(); ++i)
		{
			CharDef ch = set.getChar(text.charAt(i));
			if(ch == null)
				ch = space;
			
			size += ch.getWidth() + 1;
			chars[i] = ch;
		}
		
		int offset = 0;
		StoredBlocks blocks = new StoredBlocks(size, set.getHeight(), face);
		
		for(int i = 0; i < chars.length; ++i)
		{
			CharDef ch = chars[i];
			
			for(int x = 0; x < ch.getWidth(); ++x)
			{
				for(int y = 0; y < set.getHeight(); ++y)
					blocks.setBlock(offset + x, y, types[ch.getType(x, y).ordinal()].clone());
			}
			
			offset += ch.getWidth() + 1;
		}
		
		return blocks;
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
		return mFonts.get(name.toLowerCase());
	}
	
	public static void reloadFonts()
	{
		mFonts.clear();
		for(File file : SignMakerPlugin.getFontFolder().listFiles())
		{
			try
			{
				CharSet font = CharSet.load(file);
				mFonts.put(font.getName().toLowerCase(), font);
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
						mFonts.put(font.getName().toLowerCase(), font);
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
	
	public static List<String> getFonts()
	{
		ArrayList<String> fonts = new ArrayList<String>(mFonts.size());
		for(CharSet font : mFonts.values())
			fonts.add(font.getName());
		
		return fonts;
	}
}

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
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;

public class TextWriter {
  private static Map<String, CharSet> mFonts = new HashMap<>();

  /**
   * Get the width of the text
   *
   * @param text String to read
   * @param set  the Charset
   * @return integer
   */
  public static int getWidth(String text, CharSet set) {
    CharDef space = set.getChar(' ');

    int max = 0;
    String[] lines = text.split("\n");

    for (String line : lines) {
      int size = 0;
      for (char c : line.toCharArray()) {
        CharDef ch = set.getChar(c);
        if (ch == null) {
          ch = space;
        }
        size += ch.getWidth() + 1;

      }
      if (size > max) {
        max = size;
      }
    }
    return max;
  }

  public static int getHeight(String text, CharSet set) {
    int lines = text.split("\n").length;
    return set.getHeight() + (lines - 1) * (set.getHeight() + 1);
  }

  /**
   * Output the Text
   *
   * @param text          The Text
   * @param location      THe Location to start
   * @param face          Which way it faces
   * @param justification The {@link Justification}
   * @param set           Charset
   * @param material      Material
   */
  public static void writeText(String text, Location location, BlockFace face,
                               Justification justification, CharSet
                                   set, Material material) {
    for (StoredBlocks blocks : makeText(text, location, face, justification, set, material)) {
      blocks.apply();
    }
  }

  /**
   * @param text          String
   * @param location      {@link Location}
   * @param face          {@link BlockFace}
   * @param justification {@link Justification}
   * @param set           {@link CharSet}
   * @param material      {@link org.bukkit.Material}
   * @return returns {@link StoredBlocks} array
   */
  public static StoredBlocks[] makeText(String text, Location location, BlockFace face,
                                        Justification justification, CharSet set, Material material) {
    String[] lines = text.split("\n");
    StoredBlocks[] lineBlocks = new StoredBlocks[lines.length];

    int yOffset = (lines.length - 1) * (set.getHeight() + 1);
    int index = 0;

    for (String line : lines) {
      StoredBlocks blocks = makeText(line, face, set, material);
      Location loc = location.clone();

      switch (justification) {
        case Center:
          loc.add((blocks.getWidth() / 2) * -face.getModX(), 0, (blocks.getWidth() / 2) * -face.getModZ());
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

  /**
   * @param text     String
   * @param face     {@link BlockFace}
   * @param set      {@link CharSet}
   * @param material {@link Material}
   * @return StoredBlocks
   */
  public static StoredBlocks makeText(String text, BlockFace face, CharSet set, Material material) {
    Validate.isTrue(face == BlockFace.EAST || face == BlockFace.WEST
            || face == BlockFace.NORTH || face == BlockFace.SOUTH,
        "Can only use North, East, South, or West direction.");
    CharDef space = set.getChar(' ');

    BlockData[] types = new BlockData[BlockType.values().length];

    int index = 0;
    for (BlockType type : BlockType.values()) {
      types[index++] = mapBlockType(type, face, material);
    }

    CharDef[] chars = new CharDef[text.length()];

    int size = 0;
    for (int i = 0; i < text.length(); ++i) {
      CharDef ch = set.getChar(text.charAt(i));
      if (ch == null) {
        ch = space;
      }
      if (i != 0) {
        ++size;
      }
      size += ch.getWidth();
      chars[i] = ch;
    }


    int offset = 0;
    StoredBlocks blocks = new StoredBlocks(size, set.getHeight(), face);

    for (CharDef ch : chars) {
      for (int x = 0; x < ch.getWidth(); ++x) {
        for (int y = 0; y < set.getHeight(); ++y) { //set each stored block to its own instance.
          String stringData = types[ch.getType(x, y).ordinal()].getAsString();
          blocks.setBlock(offset + x, y, Bukkit.createBlockData(stringData));
        }
      }
      offset += ch.getWidth() + 1;
    }

    return blocks;
  }

  private static BlockData getStair(Material material) {
    Material stairMat;
    switch (material) {
      case COBBLESTONE:
        stairMat = Material.COBBLESTONE_STAIRS;
        break;
      case STONE:
        stairMat = Material.STONE_BRICK_STAIRS;
        break;
      case BRICK:
        stairMat = Material.BRICK_STAIRS;
        break;
      case OAK_PLANKS:
        stairMat = Material.OAK_STAIRS;
        break;
      case NETHER_BRICK:
        stairMat = Material.NETHER_BRICK_STAIRS;
        break;
      case QUARTZ_BLOCK:
        stairMat = Material.QUARTZ_STAIRS;
        break;
      default:
        stairMat = material;
    }
    return Bukkit.createBlockData(stairMat);

  }

  private static BlockData getSlabMaterial(Material material) {
    Material slabMaterial;
    switch (material) {
      case COBBLESTONE:
        slabMaterial = Material.COBBLESTONE_SLAB;
        break;
      case STONE:
        slabMaterial = Material.STONE_SLAB;
        break;
      case BRICK:
        slabMaterial = Material.BRICK_SLAB;
        break;
      case OAK_PLANKS:
        slabMaterial = Material.OAK_SLAB;
        break;
      case NETHER_BRICK:
        slabMaterial = Material.NETHER_BRICK_SLAB;
        break;
      case ANDESITE:
        slabMaterial = Material.ANDESITE_SLAB;
        break;
      case PRISMARINE_BRICKS:
        slabMaterial = Material.PRISMARINE_BRICK_SLAB;
        break;
      case DARK_PRISMARINE:
        slabMaterial = Material.DARK_PRISMARINE_SLAB;
        break;
      case QUARTZ_BLOCK:
        slabMaterial = Material.QUARTZ_SLAB;
        break;
      default:
        slabMaterial = material;
        break;
    }
    return Bukkit.createBlockData(slabMaterial);

  }

  protected static BlockData mapBlockType(BlockType type, BlockFace face, Material material) {
    BlockData data;
    BlockData air = Bukkit.createBlockData(Material.AIR);
    switch (type) {
      case Empty:
        return air;
      case HalfLower:
        data = getSlabMaterial(material);
        if (data instanceof Slab) {
          ((Slab) data).setType(Slab.Type.BOTTOM);
        }
        break;
      case HalfUpper:
        data = getSlabMaterial(material);
        if (data instanceof Slab) {
          ((Slab) data).setType(Slab.Type.TOP);
        }
        break;
      case LeftLower:
        data = getStair(material);
        if (data instanceof Stairs) {
          ((Stairs) data).setFacing(face);
        }
        break;
      case LeftUpper:
        data = getStair(material);
        if (data instanceof Stairs) {
          ((Stairs) data).setFacing(face);
          ((Stairs) data).setHalf(Bisected.Half.TOP);
        }
        break;
      case RightLower:
        data = getStair(material);
        if (data instanceof Stairs) {
          ((Stairs) data).setFacing(face.getOppositeFace());
        }
        break;
      case RightUpper:
        data = getStair(material);
        if (data instanceof Stairs) {
          ((Stairs) data).setFacing(face.getOppositeFace());
          ((Stairs) data).setHalf(Bisected.Half.TOP);
        }
        break;
      case Solid:
      default:
        return Bukkit.createBlockData(material);
    }
    return data;
  }

  /**
   * @param yaw float
   * @return {@link BlockFace}
   */
  public static BlockFace lookToFace(float yaw) {
    if (yaw <= -180) {
      yaw += 360;
    }

    if (yaw >= 180) {
      yaw -= 360;
    }

    if (yaw >= -45 && yaw <= 45) {
      return BlockFace.SOUTH;
    } else if (yaw > 45 && yaw < 135) {
      return BlockFace.WEST;
    } else if (yaw > -135 && yaw < -45) {
      return BlockFace.EAST;
    } else {
      return BlockFace.NORTH;
    }
  }

  public static BlockFace rotateRight(BlockFace face) {
    switch (face) {
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

  /**
   * @param name String
   * @return {@link CharSet}
   */
  public static CharSet getFont(String name) {
    return mFonts.get(name.toLowerCase());
  }

  /**
   * Reload the font files
   */
  public static void reloadFonts() {
    mFonts.clear();
    File[] files = SignMakerPlugin.getFontFolder().listFiles();
    if (files != null && files.length > 0) {
      for (File file : files) {
        try {
          CharSet font = CharSet.load(file);
          mFonts.put(font.getName().toLowerCase(), font);
          System.out.println("Loaded Font " + font.getName());
        } catch (IllegalArgumentException e) {
          System.out.println("Failed to load font " + file.getPath());
          System.out.println(e.getMessage());
        } catch (IOException e) {
          System.out.println("Failed to load font " + file.getPath());
          e.printStackTrace();
        }
      }
    }

    SignMakerPlugin plugin = SignMakerPlugin.instance;

    InputStream fontsList = plugin.getResource("fonts.txt");
    if (fontsList == null) {
      return;
    }

    // Load internal fonts
    BufferedReader reader = new BufferedReader(new InputStreamReader(fontsList));

    try {
      while (reader.ready()) {
        String line = reader.readLine();
        String path = "fonts/" + line;
        InputStream fontFile = plugin.getResource(path);
        if (fontFile == null) {
          plugin.getLogger().warning("Font " + line + " was specified in the font list but"
              + " was not found in the jar");
          continue;
        }

        try {
          CharSet font = CharSet.load(fontFile);
          if (!mFonts.containsKey(font.getName())) {
            mFonts.put(font.getName().toLowerCase(), font);
            System.out.println("Loaded Font " + font.getName());
          }
        } catch (IllegalArgumentException e) {
          System.out.println("Failed to load internal font " + path);
          System.out.println(e.getMessage());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * List of fonts
   * @return List
   */
  public static List<String> getFonts() {
    ArrayList<String> fonts = new ArrayList<>(mFonts.size());
    for (CharSet font : mFonts.values()) {
      fonts.add(font.getName());
    }
    return fonts;
  }
}

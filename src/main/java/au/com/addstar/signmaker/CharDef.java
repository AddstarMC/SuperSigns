package au.com.addstar.signmaker;

public class CharDef {
  private int mWidth;

  private BlockType[] mTypes;

  public CharDef(int width, int height) {
    mWidth = width;
    mTypes = new BlockType[width * height];
  }

  public void setType(BlockType type, int x, int y) {
    mTypes[x + (y * mWidth)] = type;
  }

  public BlockType getType(int x, int y) {
    return mTypes[x + (y * mWidth)];
  }

  public int getWidth() {
    return mWidth;
  }


}

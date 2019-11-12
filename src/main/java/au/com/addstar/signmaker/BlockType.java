package au.com.addstar.signmaker;

public enum BlockType {
  Solid('#'),
  HalfUpper('-'),
  HalfLower('_'),
  LeftUpper('<'),
  LeftLower(','),
  RightUpper('>'),
  RightLower('.'),
  Empty(' ');

  private char mChar;

  BlockType(char c) {
    mChar = c;
  }

  /**
   * Gets a BlockType from a char map
   * @param c char
   * @return BlockType
   */
  public static BlockType from(char c) {
    for (BlockType type : values()) {
      if (type.mChar == c) {
        return type;
      }
    }
    return null;
  }
}

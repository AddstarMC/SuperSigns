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

  public static BlockType from(char c) {
    for (BlockType type : values()) {
      if (type.mChar == c)
        return type;
    }

    return null;
  }
}

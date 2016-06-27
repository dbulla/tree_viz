package com.nurflugel.dependencyvisualizer.enums;

/**  */
public enum Color {
  yellow,
  green,
  black,
  red,
  blue;

  /** Get the next color in the enum. */
  public static Color next(Color color) {
    int     ordinal = color.ordinal();
    Color[] values  = values();

    return values[(ordinal + 1) % values.length];
  }

  /** Get the next color in the enum. */
  public static Color previous(Color color) {
    int     ordinal       = color.ordinal();
    Color[] colors        = values();
    int     previousIndex = ordinal - 1;

    if (previousIndex < 0) { previousIndex = colors.length - 1; }

    return colors[previousIndex];
  }

  public static Color first() { return values()[0]; }

  public static Color last() { return values()[values().length - 1]; }
}

package com.nurflugel.dependencyvisualizer.enums;

public enum Shape {
  ellipse,
  rectangle,
  hexagon,
  triangle;

  /** Get the next color in the enum. */
  public static Shape next(Shape shape) {
    Shape[] shapes  = values();
    int     ordinal = shape.ordinal();
    Shape[] values  = values();

    return values[(ordinal + 1) % values.length];
  }

  /** Get the next shape in the enum. */
  public static Shape previous(Shape shape) {
    int     ordinal       = shape.ordinal();
    Shape[] values        = values();
    int     previousIndex = ordinal - 1;

    if (previousIndex < 0) { previousIndex = values.length - 1; }

    return values[previousIndex];
  }

  public static Shape first() { return values()[0]; }

  public static Shape last() { return values()[values().length - 1]; }
}

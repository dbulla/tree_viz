package com.nurflugel.dependencyvisualizer.enums;

public enum Shape
{
  ellipse,
  rectangle,
  hexagon,
  triangle;

  /** Get the next color in the enum. */
  public static Shape next(Shape shape)
  {
    Shape[] shapes = values();

    for (int i = 0; i < shapes.length; i++)
    {
      Shape testShape = shapes[i];

      if (testShape.equals(shape))
      {
        int nextIndex = i + 1;

        if (nextIndex >= shapes.length)
        {
          nextIndex = 0;
        }

        return shapes[nextIndex];
      }
    }

    assert false;

    return shapes[0];
  }

  /** Get the next shape in the enum. */
  public static Shape previous(Shape shape)
  {
    Shape[] shapes = values();

    for (int i = 0; i < shapes.length; i++)
    {
      Shape testShape = shapes[i];

      if (testShape.equals(shape))
      {
        int previousIndex = i - 1;

        if (previousIndex < 0)
        {
          previousIndex = shapes.length - 1;
        }

        return shapes[previousIndex];
      }
    }

    assert false;

    return shapes[0];
  }

  public static Shape first()
  {
    return values()[0];
  }

  public static Shape last()
  {
    return values()[values().length - 1];
  }
}

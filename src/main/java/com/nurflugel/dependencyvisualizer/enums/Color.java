package com.nurflugel.dependencyvisualizer.enums;

/**  */
public enum Color
{
  yellow,
  green,
  black,
  red,
  blue;

  /** Get the next color in the enum. */
  public static Color next(Color color)
  {
    Color[] colors = values();

    for (int i = 0; i < colors.length; i++)
    {
      Color testColor = colors[i];

      if (testColor.equals(color))
      {
        int nextIndex = i + 1;

        if (nextIndex >= colors.length)
        {
          nextIndex = 0;
        }

        return colors[nextIndex];
      }
    }

    assert false;

    return colors[0];
  }

  /** Get the next color in the enum. */
  public static Color previous(Color color)
  {
    Color[] colors = values();

    for (int i = 0; i < colors.length; i++)
    {
      Color testColor = colors[i];

      if (testColor.equals(color))
      {
        int previousIndex = i - 1;

        if (previousIndex < 0)
        {
          previousIndex = colors.length - 1;
        }

        return colors[previousIndex];
      }
    }

    assert false;

    return colors[0];
  }

  public static Color first()
  {
    return values()[0];
  }

  public static Color last()
  {
    return values()[values().length - 1];
  }
}

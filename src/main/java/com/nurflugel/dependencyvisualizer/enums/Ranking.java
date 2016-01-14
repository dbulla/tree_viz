package com.nurflugel.dependencyvisualizer.enums;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jdom.Element;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.nurflugel.dependencyvisualizer.Constants.*;

/**
 * This is the representation of the type of bounding polygon for the data item. It has a title, a color, a shape, and a rank. The rank is built up
 * from the list of types, so as new ones are added, the rank is incremented.
 */
@Data
@EqualsAndHashCode(of = "rank")
@SuppressWarnings({ "AccessingNonPublicFieldOfAnotherObject" })
public class Ranking implements Comparable
{
  // ------------------------------ FIELDS ------------------------------
  private static int                 rankCounter = 0;
  private static final List<Ranking> types       = new ArrayList<>();
  private String                     level;
  private Color                      color;
  private Shape                      shape;
  private final int                  rank;

  public static Ranking valueOf(String title) throws Exception
  {
    return types.stream()
                .filter(r -> r.getLevel().equals(title))
                .findFirst()
                .orElseThrow(() -> new Exception(title + " not found"));
  }

  @SuppressWarnings({ "AccessingNonPublicFieldOfAnotherObject" })
  public static Ranking valueOf(String title, Color color, Shape shape)
  {
    Optional<Ranking> first = types.stream()
                                   .filter(r -> r.getLevel().equals(title))
                                   .findFirst();

    if (first.isPresent())
    {
      return first.get();
    }

    // create the next type if it didn't already exist
    Ranking type = new Ranking(title, color, shape, rankCounter++);

    types.add(type);

    return type;
  }

  public static List<Ranking> values()
  {
    return types;
  }

  public static Ranking first()
  {
    return types.get(0);
  }

  // --------------------------- CONSTRUCTORS ---------------------------
  private Ranking(String level, Color color, Shape shape, int rank)
  {
    this.level = level;
    this.color = color;
    this.shape = shape;
    this.rank  = rank;
  }

  // --------------------- Interface Comparable ---------------------
  public int compareTo(Object o)
  {
    return Integer.compare(((Ranking) o).rank, rank);
  }

  // ------------------------ CANONICAL METHODS ------------------------
  @Override
  public String toString()
  {
    return level;
  }

  public Element getElement()
  {
    Element rankingElement = new Element(RANKING);

    rankingElement.setAttribute(LEVEL, level);

    return rankingElement;
  }

  public static Element getElements()
  {
    Element element = new Element("rankings");

    for (Ranking type : types)
    {
      Element rankingElement = new Element(RANKING);

      rankingElement.setAttribute(LEVEL, type.level);
      rankingElement.setAttribute(COLOR, type.color.toString());
      rankingElement.setAttribute(SHAPE, type.shape.toString());
      rankingElement.setAttribute(RANK, type.rank + "");
      element.addContent(rankingElement);
    }

    return element;
  }
}

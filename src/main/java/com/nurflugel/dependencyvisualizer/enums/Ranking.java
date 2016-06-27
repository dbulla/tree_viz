package com.nurflugel.dependencyvisualizer.enums;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.nurflugel.dependencyvisualizer.enums.Color.black;
import static com.nurflugel.dependencyvisualizer.enums.Shape.rectangle;

/**
 * This is the representation of the type of bounding polygon for the data item. It has a title, a color, a shape, and a rank. The rank is built up from the list of types, so as
 * new ones are added, the rank is incremented.
 *
 * <p>This is sort of like an enum but is determined from the data loaded</p>
 */
@Data
@EqualsAndHashCode(of = "rank")
@SuppressWarnings({ "AccessingNonPublicFieldOfAnotherObject" })
public class Ranking implements Comparable {
  // ------------------------------ FIELDS ------------------------------
  private static int                 rankCounter = 0;
  private static final List<Ranking> types       = new ArrayList<>();
  private String                     name;
  private Color                      color;
  private Shape                      shape;
  private final int                  rank;

  public static Ranking valueOf(String title) { return types.stream()
                                                            .filter(r -> r.getName().equals(title))
                                                            .findFirst()
                                                            .orElseGet(() -> valueOf(title, black, rectangle)); }

  @SuppressWarnings({ "AccessingNonPublicFieldOfAnotherObject" })
  public static Ranking valueOf(String title, Color color, Shape shape) {
    Optional<Ranking> first   = types.stream()
                                     .filter(r -> r.getName().equals(title))
                                     .findFirst();
    Ranking           ranking = first.orElseGet(() -> {
                                                  // create the next type if it didn't already exist
                                                  Ranking type = new Ranking(title, color, shape, rankCounter++);

                                                  types.add(type);

                                                  return type;
                                                });

    return ranking;
  }

  public static List<Ranking> values() { return types; }

  public static Ranking first() { return types.get(0); }

  // --------------------------- CONSTRUCTORS ---------------------------
  private Ranking(String name, Color color, Shape shape, int rank) {
    this.name  = name;
    this.color = color;
    this.shape = shape;
    this.rank  = rank;
  }

  // --------------------- Interface Comparable ---------------------
  @Override
  public int compareTo(Object o) { return Integer.compare(rank, ((Ranking) o).rank); }

  // ------------------------ CANONICAL METHODS ------------------------
  @Override
  public String toString() { return name; }

  public static void clearRankings() { types.clear(); }

  public static void addRanking(Ranking ranking) { types.add(ranking); }
}

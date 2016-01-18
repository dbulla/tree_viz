package com.nurflugel.dependencyvisualizer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;
import static org.apache.commons.lang3.StringUtils.replace;

/** Representation of an object. */
@Data
@EqualsAndHashCode(of = "name")
@NoArgsConstructor
public class DependencyObject implements Comparable
{
  // ------------------------------ FIELDS ------------------------------
  private String      name;
  private String      displayName;
  private String[]    notes        = new String[0];
  private String      ranking;
  private Set<String> dependencies = new HashSet<>();  // todo make this the unique names as a key, not the entire dependency object!

  // --------------------------- CONSTRUCTORS ---------------------------
  public DependencyObject(String name, String ranking)
  {
    this.name    = replaceAllBadChars(name);
    displayName  = name;
    this.ranking = ranking;
  }

  public DependencyObject(String name, String[] notes, String ranking)
  {
    this.name    = replaceAllBadChars(name);
    this.notes   = notes;
    this.ranking = ranking;
  }

  /**
   * Returns a job with all spaces and weird characters "fixed" for Dot processing.
   *
   * <p>todo - something better with regular expressions - anything except text and numbers in one expression</p>
   */
  public static String replaceAllBadChars(String text)
  {
    String newValue = text.trim();

    newValue = replace(newValue, "-", "_");
    newValue = replace(newValue, "@", "_");
    newValue = replace(newValue, " ", "_");
    newValue = replace(newValue, ".", "_");
    newValue = replace(newValue, "/", "_");
    newValue = replace(newValue, "\\", "_");
    newValue = replace(newValue, "^", "_");

    return newValue;
  }

  // --------------------- Interface Comparable ---------------------
  @Override
  public int compareTo(Object o)
  {
    if (o instanceof DependencyObject)
    {
      DependencyObject theOther = (DependencyObject) o;

      return name.compareTo(theOther.getName());
    }
    else
    {
      return 0;
    }
  }

  @Override
  public String toString()
  {
    return displayName;
  }

  // ------------------------ Class Methods ------------------------
  public void addDependency(String dependency)
  {
    dependencies.add(dependency);
  }

  public void removeAllDependencies()
  {
    dependencies.clear();
  }
}

package com.nurflugel.dependencyvisualizer.data.pojos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;
import static org.apache.commons.lang3.StringUtils.replace;

/** Created by douglas_bullard on 1/18/16. */
@Data
@EqualsAndHashCode(of = "name")
@NoArgsConstructor
@ToString(of = "displayName")
public class BaseDependencyObject implements Comparable{
  // ------------------------------ FIELDS ------------------------------
  protected String    name;
  protected String    displayName;
  protected String[]  notes        = new String[0];
  protected String    ranking;
  private Set<String> dependencies = new HashSet<>();

  public BaseDependencyObject(String name, String ranking){
    this.name    = replaceAllBadChars(name);
    displayName  = name;
    this.ranking = ranking;
  }

  public BaseDependencyObject(String name, String[] notes, String ranking){
    this.name    = replaceAllBadChars(name);
    this.notes   = notes;
    this.ranking = ranking;
  }

  /**
   * Returns a job with all spaces and weird characters "fixed" for Dot processing.
   *
   * <p>todo - something better with regular expressions - anything except text and numbers in one expression</p>
   */
  public static String replaceAllBadChars(String text){
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
  public int compareTo(Object o){
    if (o instanceof BaseDependencyObject){
      BaseDependencyObject theOther = (BaseDependencyObject) o;

      return name.compareTo(theOther.getName());
    }
    else { return 0; }
  }

  // ------------------------ Class Methods ------------------------
  public void addDependency(String dependency) { dependencies.add(dependency); }

  public void removeAllDependencies() { dependencies.clear(); }
}

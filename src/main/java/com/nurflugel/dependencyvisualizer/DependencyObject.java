package com.nurflugel.dependencyvisualizer;

import com.nurflugel.dependencyvisualizer.enums.Ranking;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jdom.Element;
import java.util.HashSet;
import java.util.Set;
import static com.nurflugel.dependencyvisualizer.Constants.*;
import static org.apache.commons.lang3.StringUtils.replace;

/** Representation of an object. */
@Data
@EqualsAndHashCode(of = "name")
@NoArgsConstructor
public class DependencyObject implements Comparable
{
  // ------------------------------ FIELDS ------------------------------
  private String                name;
  private String                displayName;
  private String[]              notes        = new String[0];
  private Ranking               ranking;
  private Set<DependencyObject> dependencies = new HashSet<>();  // todo make this the unique names as a key, not the entire dependency object!

  // --------------------------- CONSTRUCTORS ---------------------------
  public DependencyObject(String name, Ranking ranking)
  {
    this.name    = cleanDefinition(name);
    displayName  = name;
    this.ranking = ranking;
  }

  public DependencyObject(String name, String[] notes, Ranking ranking)
  {
    this.name    = cleanDefinition(name);
    this.notes   = notes;
    this.ranking = ranking;
  }

  /** Generates XML stuff for JDOM output. */
  public Element getElement()
  {
    Element element = new Element(DEPENDENCY_OBJECT);

    element.setAttribute(NAME, name);
    element.setAttribute(DISPLAY_NAME, displayName);
    element.addContent(ranking.getElement());

    Element notesElements = new Element(NOTES);

    element.addContent(notesElements);

    for (String note : notes)
    {
      Element noteElement = new Element(NOTE);

      notesElements.addContent(noteElement);
      noteElement.setAttribute(VALUE, note);
    }

    Element dependenciesElement = new Element(DEPENDENCIES);

    element.addContent(dependenciesElement);

    for (DependencyObject dependency : dependencies)
    {
      Element dependencyElement = new Element(DEPENDENCY);

      dependenciesElement.addContent(dependencyElement);
      dependencyElement.setAttribute(NAME, dependency.getName());
    }

    return element;
  }

  /**
   * Returns a job with all spaces and weird characters "fixed" for Dot processing.
   *
   * <p>todo - something better with regular expressions - anything except text and numbers in one expression</p>
   */
  public static String cleanDefinition(String jobNumber)
  {
    String newValue = jobNumber;

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

      return theOther.getName().compareTo(name);

      // return theOther.getNotes().compareTo(notes) * -1;
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
  public void addDependency(DependencyObject dependency)
  {
    dependencies.add(dependency);
  }

  public void removeAllDependencies()
  {
    dependencies.clear();
  }
}

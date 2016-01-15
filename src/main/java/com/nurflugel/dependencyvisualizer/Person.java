package com.nurflugel.dependencyvisualizer;

import com.nurflugel.dependencyvisualizer.enums.Ranking;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jdom.Element;
import java.util.HashSet;
import java.util.Set;
import static com.nurflugel.dependencyvisualizer.Constants.*;

/** A person is a special type of object, with birth dates, death dates, and spouses. */
@Data
@NoArgsConstructor
public class Person extends DependencyObject
{
  private Set<String> spouses   = new HashSet<>();
  private String      birthDate;
  private String      deathDate;

  public Person(String name, Ranking type)
  {
    super(name, type);
  }

  public Person(String name, String[] notes, Ranking type)
  {
    super(name, notes, type);
  }

  public void addSpouse(Person spouse)
  {
    spouses.add(spouse.getName());
  }

  public Element getElement()
  {
    Element element = super.getElement();

    element.setAttribute(BIRTHDATE, birthDate);
    element.setAttribute(DEATH_DATE, deathDate);

    Element spousesElement = new Element(SPOUSES);

    element.addContent(spousesElement);

    for (String spouse : spouses)
    {
      Element spouseElement = new Element(SPOUSE);

      spousesElement.addContent(spouseElement);
      spouseElement.setAttribute(NAME, spouse);
    }

    return element;
  }

  @Override
  public String toString()
  {
    return getDisplayName();
  }

  public void removeAllSpouses()
  {
    spouses.clear();
  }
}

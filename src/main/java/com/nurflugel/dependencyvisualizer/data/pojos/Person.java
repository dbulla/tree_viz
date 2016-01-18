package com.nurflugel.dependencyvisualizer.data.pojos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

/** A person is a special type of object, with birth dates, death dates, and spouses. */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuppressWarnings("Lombok")
public class Person extends BaseDependencyObject
{
  private Set<String> spouses   = new HashSet<>();
  private String      birthDate;
  private String      deathDate;

  public Person(String name, String type)
  {
    super(name, type);
  }

  public Person(String name, String[] notes, String type)
  {
    super(name, notes, type);
  }

  public void addSpouse(Person spouse)
  {
    spouses.add(spouse.getName());
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

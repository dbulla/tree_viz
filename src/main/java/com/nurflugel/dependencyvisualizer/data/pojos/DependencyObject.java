package com.nurflugel.dependencyvisualizer.data.pojos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** Representation of an object. */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DependencyObject extends BaseDependencyObject
{
  // --------------------------- CONSTRUCTORS ---------------------------
  public DependencyObject(String name, String ranking)
  {
    super(name, ranking);
  }

  public DependencyObject(String name, String[] notes, String ranking)
  {
    super(name, notes, ranking);
  }
}

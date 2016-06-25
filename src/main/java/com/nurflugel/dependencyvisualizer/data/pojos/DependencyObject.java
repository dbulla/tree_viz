package com.nurflugel.dependencyvisualizer.data.pojos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Representation of an object. */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class DependencyObject extends BaseDependencyObject{
  // --------------------------- CONSTRUCTORS ---------------------------
  public DependencyObject(String name, String ranking) { super(name, ranking); }
}

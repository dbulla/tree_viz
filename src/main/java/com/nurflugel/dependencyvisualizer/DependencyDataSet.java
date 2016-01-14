package com.nurflugel.dependencyvisualizer;

import com.nurflugel.dependencyvisualizer.enums.Ranking;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Collection;

/** Created by douglas_bullard on 1/13/16. */
@AllArgsConstructor
@Data
public class DependencyDataSet
{
  private boolean                      isFamilyTree;
  private Collection<DependencyObject> objects;
  private Collection<Ranking>          rankings;
}

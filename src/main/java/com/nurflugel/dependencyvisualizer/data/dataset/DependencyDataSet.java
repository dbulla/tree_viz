package com.nurflugel.dependencyvisualizer.data.dataset;

import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject;
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/** Created by douglas_bullard on 1/17/16. */
public class DependencyDataSet extends BaseDependencyDataSet
{
  private Map<String, DependencyObject> objectsMap = new HashMap<>();

  // rather than return the map, add operations on the dataset each type can implement (get, put, contains)
  @Override
  public Stream<BaseDependencyObject> getObjects()
  {
    return objectsMap.values().stream()
                     .map(d -> (BaseDependencyObject) d);
  }

  @Override
  public BaseDependencyObject get(String key)
  {
    return objectsMap.get(key);
  }

  @Override
  public boolean containsKey(String key)
  {
    return objectsMap.containsKey(key);
  }

  @Override
  public void put(String key, BaseDependencyObject object)
  {
    assert object instanceof DependencyObject;
    objectsMap.put(key, (DependencyObject) object);
  }
}

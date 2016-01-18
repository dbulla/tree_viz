package com.nurflugel.dependencyvisualizer.data.dataset;

import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject;
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;

/** Created by douglas_bullard on 1/17/16. */
public class DependencyDataSet
{
  private boolean                           isFamilyTree;
  private Map<String, BaseDependencyObject> objectsMap = new HashMap<>();
  private List<Ranking>                     rankings;
  private static Logger                     logger     = LoggerFactory.getLogger(DependencyDataSet.class);

  public Stream<BaseDependencyObject> getObjects()
  {
    return objectsMap.values().stream();
  }

  // public abstract Map<String,DependencyObject> getObjectsMap() ;
  public Collection<Ranking> getRankings()
  {
    List<Ranking> collect = objectsMap.values().stream()
                                      .map(BaseDependencyObject::getRanking)
                                      .distinct()
                                      .map(Ranking::valueOf)
                                      .collect(toList());

    return collect;
  }

  public void add(BaseDependencyObject newObject)
  {
    objectsMap.put(newObject.getName(), newObject);
  }

  public BaseDependencyObject getLoaderObjectByName(String name)
  {
    String               trimmedName          = BaseDependencyObject.replaceAllBadChars(name.trim());
    BaseDependencyObject baseDependencyObject = objectsMap.computeIfAbsent(trimmedName,
                                                                           i ->
                                                                           {
                                                                             BaseDependencyObject newObject = new DependencyObject(trimmedName,
                                                                                                                                   Ranking.first()
                                                                                                                                     .getName());

                                                                             if (logger.isDebugEnabled())
                                                                             {
                                                                               logger.debug("Adding unregistered object: " + trimmedName
                                                                                              + " as object of type " + newObject.getRanking());
                                                                             }

                                                                             objectsMap.put(trimmedName, newObject);

                                                                             return newObject;
                                                                           });

    return baseDependencyObject;
  }

  /** Go through the dependencies - some may be empty, just placeholders, while some might not. Remove dupes, streamline. */
  public void rectify()
  {
    objectsMap.entrySet().forEach(entry ->
                                  {
                                    String               name                 = entry.getKey();
                                    BaseDependencyObject baseDependencyObject = entry.getValue();

                                    System.out.println("name = " + name);
                                    System.out.println("dependencyObject = " + baseDependencyObject);
                                  });
  }

  public void generateRankingsMap()
  {
    rankings = Ranking.values();
  }

  public boolean isFamilyTree()
  {
    return isFamilyTree;
  }

  public void setFamilyTree(boolean isFamilyTree)
  {
    this.isFamilyTree = isFamilyTree;
  }

  public boolean containsKey(String key)
  {
    return objectsMap.containsKey(key);
  }

  public BaseDependencyObject get(String key)
  {
    return objectsMap.get(key);
  }
}

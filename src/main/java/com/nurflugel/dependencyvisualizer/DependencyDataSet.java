package com.nurflugel.dependencyvisualizer;

import com.nurflugel.dependencyvisualizer.enums.Ranking;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import static java.util.stream.Collectors.toList;

/** Created by douglas_bullard on 1/13/16. */
@AllArgsConstructor
@Data
public class DependencyDataSet
{
  private boolean                       isFamilyTree;
  private Map<String, DependencyObject> objectsMap  = new HashMap<>();
  private Map<String, Ranking>          rankingsMap = new HashMap<>();
  private static final Logger           logger      = LoggerFactory.getLogger(DependencyDataSet.class);

  public DependencyDataSet()
  {
    objectsMap  = new HashMap<>();
    rankingsMap = new HashMap<>();
  }

  public Collection<DependencyObject> getObjects()
  {
    return objectsMap.values();
  }

  public Collection<Ranking> getRankings()
  {
    return objectsMap.values().stream()
                     .map(DependencyObject::getRanking)
                     .collect(toList());
  }

  public void add(DependencyObject newObject)
  {
    objectsMap.put(newObject.getName(), newObject);

    // rankingsMap.put(newObject.getRanking().get)
  }

  public DependencyObject getLoaderObjectByName(String name)
  {
    String           trimmedName      = DependencyObject.replaceAllBadChars(name.trim());
    DependencyObject dependencyObject = objectsMap.computeIfAbsent(trimmedName,
                                                                   i ->
                                                                   {
                                                                     DependencyObject newObject = new DependencyObject(trimmedName, Ranking.first());

                                                                     if (logger.isDebugEnabled())
                                                                     {
                                                                       logger.debug("Adding unregistered object: " + trimmedName
                                                                                      + " as object of type " + newObject.getRanking());
                                                                     }

                                                                     objectsMap.put(trimmedName, newObject);

                                                                     return newObject;
                                                                   });

    return dependencyObject;
  }

  /** Go through the dependencies - some may be empty, just placeholders, while some might not. Remove dupes, streamline. */
  public void rectify()
  {
    objectsMap.entrySet().forEach(entry ->
                                  {
                                    String           name             = entry.getKey();
                                    DependencyObject dependencyObject = entry.getValue();

                                    System.out.println("name = " + name);
                                    System.out.println("dependencyObject = " + dependencyObject);
                                  });
  }
}

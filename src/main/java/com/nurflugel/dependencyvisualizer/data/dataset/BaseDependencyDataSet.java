package com.nurflugel.dependencyvisualizer.data.dataset;

import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject;
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;

/** Created by douglas_bullard on 1/18/16. */
public abstract class BaseDependencyDataSet
{
  private static Logger logger       = LoggerFactory.getLogger(DependencyDataSet.class);
  private boolean       isFamilyTree;
  private List<Ranking> rankings;

  public abstract Stream<BaseDependencyObject> getObjects();

  // public abstract Map<String,DependencyObject> getObjectsMap() ;
  public Collection<Ranking> getRankings()
  {
    List<Ranking> collect = getObjects()
                              .map(BaseDependencyObject::getRanking)
                                        .distinct()
                                        .map(Ranking::valueOf)
                                        .collect(toList());

    return collect;
  }

  public void add(BaseDependencyObject newObject)
  {
    put(newObject.getName(), newObject);
  }

  public BaseDependencyObject getLoaderObjectByName(String name)
  {
    String               trimmedName          = BaseDependencyObject.replaceAllBadChars(name.trim());
    boolean              exists               = containsKey(trimmedName);
    BaseDependencyObject baseDependencyObject;

    if (exists)
    {
      baseDependencyObject = get(trimmedName);
    }
    else
    {
      baseDependencyObject = new DependencyObject(trimmedName, Ranking.first().getName());

      if (logger.isDebugEnabled())
      {
        logger.debug("Adding unregistered object: " + trimmedName + " as object of type " + baseDependencyObject.getRanking());
      }

      put(trimmedName, baseDependencyObject);
    }

    return baseDependencyObject;
  }

  /** Go through the dependencies - some may be empty, just placeholders, while some might not. Remove dupes, streamline. */
  public void rectify()
  {
    // getObjectsMap().entrySet().forEach(entry ->
    // {
    // String               name                 = entry.getKey();
    // BaseDependencyObject baseDependencyObject = entry.getValue();
    //
    // System.out.println("name = " + name);
    // System.out.println("dependencyObject = " + baseDependencyObject);
    // });
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

  public abstract BaseDependencyObject get(String key);
  public abstract boolean containsKey(String key);
  public abstract void put(String key, BaseDependencyObject object);
}

package com.nurflugel.dependencyvisualizer.data;

import com.google.common.collect.Sets;
import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet;
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject;
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import java.util.*;
import static com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.Down;
import static com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.Up;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**  */
public class ObjectFilterer{
  private List<DirectionalFilter> directionalFilters = new ArrayList<>();
  private List<Ranking>           typesToFilter      = new ArrayList<>();

  public ObjectFilterer(List<DirectionalFilter> directionalFilters, List<Ranking> typesToFilter){
    this.directionalFilters.addAll(directionalFilters);
    this.typesToFilter.addAll(typesToFilter);
  }
  // -------------------------- OTHER METHODS --------------------------

  /**
   * Some assumptions here - if they don't pass in any filters, they want everything. If they DO pass in a filter, then filter everything based on that.
   *
   * @param   dataSet     the objects to filter
   * @param   keyObjects  - keep the stream objects if they reference key objects
   *
   * @return  the filtered array of typesToFilter
   */
  public Collection<BaseDependencyObject> filter(BaseDependencyDataSet dataSet, Set<BaseDependencyObject> keyObjects){
    Set<BaseDependencyObject> objectsToFilter = dataSet.getObjects()
                                                       .collect(toSet());

    // quick test
    if (directionalFilters.isEmpty() && typesToFilter.isEmpty() && keyObjects.isEmpty()) { return objectsToFilter; }

    Set<BaseDependencyObject> filteredObjects = new TreeSet<>();

    // handle empty case - fix this in the UI, dammit!
    if (directionalFilters.isEmpty()){
      directionalFilters.add(Down);
      directionalFilters.add(Up);
    }
    else{
      for (DirectionalFilter directionalFilter : directionalFilters){
        Set<BaseDependencyObject> loaderObjects = filterObjectsByDirection(dataSet, keyObjects, 0, directionalFilter);

        filteredObjects.addAll(loaderObjects);
      }
    }

    return filteredObjects;
  }

  /**
   * This is a recursive method - it'll take several passes to get all the objects. At the end of the method, it calls itself to see if there were any more objects added. If not,
   * it exits. If so, it calls itself again.
   */
  private Set<BaseDependencyObject> filterObjectsByDirection(BaseDependencyDataSet dataSet, Set<BaseDependencyObject> keyObjects, int initialSize,
                                                             DirectionalFilter directionalFilter){
    Set<BaseDependencyObject> filteredObjects = new HashSet<>();

    if (directionalFilter.equals(Up)){
      Set<BaseDependencyObject> objects = filterUp(dataSet, keyObjects);

      filteredObjects.addAll(objects);
    }

    if (directionalFilter.equals(Down)) { filteredObjects.addAll(filterDown(dataSet, keyObjects)); }

    int                       currentSize           = filteredObjects.size();
    Set<BaseDependencyObject> loaderObjectsToReturn = new TreeSet<>(filteredObjects);  // todo remove temp set when unit tests pass

    // Keep doing this until it stabilizes
    if (currentSize != initialSize) { loaderObjectsToReturn = filterObjectsByDirection(dataSet, loaderObjectsToReturn, currentSize, directionalFilter); }

    return loaderObjectsToReturn;
  }

  /** Filter from this object on up. */
  private Set<BaseDependencyObject> filterUp(BaseDependencyDataSet dataSet, Collection<BaseDependencyObject> keyObjects){
    Set<BaseDependencyObject> filteredObjects = new TreeSet<>();

    if (directionalFilters.contains(Up)){
      // Is this object in the list of key objects we're interested in?
      // Add the object itself if it has a higher ranking.
      // now, valueOf all dependencies of this object that have a higher ranking, too
      dataSet.getObjects()
             .filter(keyObjects::contains)
             .forEach(mainObject ->{
                        // Add the object itself if it has a higher ranking.
                        filteredObjects.add(mainObject);

                        // now, valueOf all dependencies of this object that have a higher ranking, too
                        Collection<String>         dependencies          = mainObject.getDependencies();
                        List<BaseDependencyObject> baseDependencyObjects = dependencies.stream()
                                                                             .map(dataSet::get)
                                                                             .collect(toList());

                        filteredObjects.addAll(baseDependencyObjects);
                        });
    }

    return filteredObjects;
  }

  /**
   * Filter from this object on down.
   *
   * <p>Go through each of the objects, and see if any of the key objects call them as references. If so, add them to the list.</p>
   */
  private Set<BaseDependencyObject> filterDown(BaseDependencyDataSet dataSet, Set<BaseDependencyObject> keyObjects){
    Set<BaseDependencyObject> filteredObjects = new TreeSet<>();
    Set<String>               keyNames        = keyObjects.stream()
                                                          .map(BaseDependencyObject::getName)
                                                          .collect(toSet());

    if (!directionalFilters.contains(Down)) { return keyObjects; }

    filteredObjects.addAll(keyObjects);

    if (!keyNames.isEmpty()){  //
      dataSet.getObjects()

             // todo can filter and collect
             // for this object, are any of the keyNames in it's list of dependencies?
             .forEach(mainObject ->{  //

                        Set<String>          dependencies = mainObject.getDependencies();
                        Sets.SetView<String> intersection = Sets.intersection(keyNames, dependencies);

                        if (!intersection.isEmpty()) { filteredObjects.add(mainObject); }
                        });
    }

    return filteredObjects;
  }
}

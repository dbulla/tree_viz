package com.nurflugel.dependencyvisualizer;

import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import java.util.*;
import static com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.Up;
import static java.util.stream.Collectors.toList;

/**  */
public class ObjectFilterer
{
  private List<DirectionalFilter> directionalFilters = new ArrayList<>();
  private List<Ranking>           typesToFilter      = new ArrayList<>();

  public ObjectFilterer(List<DirectionalFilter> directionalFilters, List<Ranking> typesToFilter)
  {
    this.directionalFilters.addAll(directionalFilters);
    this.typesToFilter.addAll(typesToFilter);
  }
  // -------------------------- OTHER METHODS --------------------------

  /**
   * Some assumptions here - if they don't pass in any filters, they want everything. If they DO pass in a filter, then filter everything based on
   * that.
   *
   * @param   objectsToFilter  the array of typesToFilter to filter
   * @param   keyObjects
   *
   * @return  the filtered array of typesToFilter
   */
  public Collection<DependencyObject> filter(final Collection<DependencyObject> objectsToFilter, final Collection<DependencyObject> keyObjects)
  {
    // quick test
    if (directionalFilters.isEmpty() && typesToFilter.isEmpty() && keyObjects.isEmpty())
    {
      return objectsToFilter;
    }

    Set<DependencyObject> filteredObjects = new HashSet<>();

    // handle empty case - fix this in the UI, dammit!
    if (directionalFilters.isEmpty())
    {
      directionalFilters.add(DirectionalFilter.Down);
      directionalFilters.add(Up);
    }

    if (!directionalFilters.isEmpty())
    {
      for (DirectionalFilter directionalFilter : directionalFilters)
      {
        Set<DependencyObject> loaderObjects = filterObjectsByDirection(objectsToFilter, keyObjects, 0, directionalFilter);

        filteredObjects.addAll(loaderObjects);
      }
    }

    // if (!typesToFilter.isEmpty())
    // {
    // filteredObjects = filterObjectsByType(filteredObjects);
    // }
    return new TreeSet<>(filteredObjects);
  }

  /**
   * This is a recursive method - it'll take several passes to get all the objects. At the end of the method, it calls itself to see if there were any
   * more objects added. If not, it exits. If so, it calls itself again.
   */
  private Set<DependencyObject> filterObjectsByDirection(Collection<DependencyObject> objects, Collection<DependencyObject> keyObjects,
                                                         int initialSize, DirectionalFilter directionalFilter)
  {
    Set<DependencyObject> filteredObjects = new HashSet<>();

    if (directionalFilter.equals(Up))
    {
      filteredObjects.addAll(filterUp(objects, keyObjects));
    }

    if (directionalFilter.equals(DirectionalFilter.Down))
    {
      filteredObjects.addAll(filterDown(objects, keyObjects));
    }

    int                   currentSize           = filteredObjects.size();
    Set<DependencyObject> loaderObjectsToReturn = new TreeSet<>(filteredObjects);

    // Keep doing this until it stabilizes
    if (currentSize != initialSize)
    {
      loaderObjectsToReturn = filterObjectsByDirection(objects, loaderObjectsToReturn, currentSize, directionalFilter);
    }

    return loaderObjectsToReturn;
  }

  /** Filter from this object on up. */
  private Set<DependencyObject> filterUp(Collection<DependencyObject> objects, Collection<DependencyObject> keyObjects)
  {
    Set<DependencyObject> filteredObjects = new TreeSet<>();

    if (directionalFilters.contains(Up))
    {
      // Is this object in the list of key objects we're interested in?
      // Add the object itself if it has a higher ranking.
      // now, valueOf all dependencies of this object that have a higher ranking, too
      // filteredObjects.addAll(dependencies);//todo uncomment
      objects.stream()
             .filter(keyObjects::contains)
             .forEach(mainObject ->
                      {
                        // Add the object itself if it has a higher ranking.
                        filteredObjects.add(mainObject);

                        // now, valueOf all dependencies of this object that have a higher ranking, too
                        Collection<String> dependencies = mainObject.getDependencies();

                        // filteredObjects.addAll(dependencies);//todo uncomment
                        });
    }

    return filteredObjects;
  }

  /** Filter from this object on down. */
  private Set<DependencyObject> filterDown(Collection<DependencyObject> objects, Collection<DependencyObject> keyObjects)
  {
    Set<DependencyObject> filteredObjects = new TreeSet<>();

    if (directionalFilters.contains(DirectionalFilter.Down))
    {
      filteredObjects.addAll(keyObjects);

      for (DependencyObject mainObject : objects)
      {
        Collection<String> dependencies = mainObject.getDependencies();

        if (!dependencies.isEmpty())
        {
          filteredObjects.addAll(keyObjects.stream()
                                   .filter(dependencies::contains)
                                   .filter(keyObject -> mainObject.getRanking().getRank() > keyObject.getRanking().getRank())
                                   .map(keyObject -> mainObject)
                                   .collect(toList()));
        }
      }
    }

    return filteredObjects;
  }

  /**
   * This will "turn on" or "turn off" sets of objects - for example, don't show any CDB views.
   *
   * @param  filteredObjects  - the type of objects not to show.
   */
  private List<DependencyObject> filterObjectsByType(List<DependencyObject> filteredObjects)
  {
    return filteredObjects;
  }
}

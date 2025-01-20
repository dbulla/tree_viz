package com.nurflugel.dependencyvisualizer.data

import com.google.common.collect.Sets
import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.DOWN
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.UP
import com.nurflugel.dependencyvisualizer.enums.Ranking
import java.util.*

/** Utility class to filter the graph of objects so we only see what we're interested in.  */
class ObjectFilterer(directionalFilters: List<DirectionalFilter>, typesToFilter: List<Ranking>) {
    private val directionalFilters: MutableList<DirectionalFilter> = mutableListOf()
    private val typesToFilter: MutableList<Ranking> = mutableListOf()

    init {
        this.directionalFilters.addAll(directionalFilters)
        this.typesToFilter.addAll(typesToFilter)
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
    fun filter(dataSet: BaseDependencyDataSet, keyObjects: Set<BaseDependencyObject>): Collection<BaseDependencyObject> {
        val objectsToFilter = dataSet.getObjects()
            .toSet()

        // quick test
        if (directionalFilters.isEmpty() && typesToFilter.isEmpty() && keyObjects.isEmpty()) {
            return objectsToFilter
        }

        val filteredObjects: MutableSet<BaseDependencyObject> = mutableSetOf()

        // handle empty case - fix this in the UI, dammit!
        if (directionalFilters.isEmpty()) {
            directionalFilters.add(DOWN)
            directionalFilters.add(UP)
        }
        else {
            directionalFilters
                .map { filterObjectsByDirection(dataSet, keyObjects, 0, it) }
                .forEach { filteredObjects.addAll(it) }
        }

        return filteredObjects
    }

    /**
     * This is a recursive method - it'll take several passes to get all the objects. At the end of the method, it calls itself to see if there were any more objects added. If not,
     * it exits. If so, it calls itself again.
     */
    private fun filterObjectsByDirection(
        dataSet: BaseDependencyDataSet,
        keyObjects: Set<BaseDependencyObject>,
        initialSize: Int,
        directionalFilter: DirectionalFilter
    ): Set<BaseDependencyObject> {
        val filteredObjects: MutableSet<BaseDependencyObject> = mutableSetOf()

        if (directionalFilter == UP) {
            val objects: Set<BaseDependencyObject> = filterUp(dataSet, keyObjects)

            filteredObjects.addAll(objects)
        }

        if (directionalFilter == DOWN) {
            filteredObjects.addAll(filterDown(dataSet, keyObjects))
        }

        val currentSize = filteredObjects.size
        var loaderObjectsToReturn: Set<BaseDependencyObject> = filteredObjects.toSet() // todo remove temp set when unit tests pass

        // Keep doing this until it stabilizes
        if (currentSize != initialSize) {
            loaderObjectsToReturn = filterObjectsByDirection(dataSet, loaderObjectsToReturn, currentSize, directionalFilter)
        }

        return loaderObjectsToReturn
    }

    /** Filter from this object on up.  */
    private fun filterUp(dataSet: BaseDependencyDataSet, keyObjects: Collection<BaseDependencyObject>): Set<BaseDependencyObject> {
        val filteredObjects: MutableSet<BaseDependencyObject> = TreeSet()

        if (directionalFilters.contains(UP)) {
            // Is this object in the list of key objects we're interested in?
            // Add the object itself if it has a higher ranking.
            // now, valueOf all dependencies of this object that have a higher ranking, too
            dataSet.getObjects()
                .filter(keyObjects::contains)
                .forEach { mainObject ->
                    // Add the object itself if it has a higher ranking.
                    filteredObjects.add(mainObject)

                    // now, valueOf all dependencies of this object that have a higher ranking, too
//                    val dependencies: Collection<String> = mainObject.dependencies.
                    val dependencies = mainObject.dependencies
                    val baseDependencyObjects = dependencies
                        .map(dataSet::objectByName)
                    filteredObjects.addAll(baseDependencyObjects)
                }
        }

        return filteredObjects
    }

    /**
     * Filter from this object on down.
     *
     * Go through each of the objects, and see if any of the key objects call them as references. If so, add them to the list.
     */
    private fun filterDown(dataSet: BaseDependencyDataSet, keyObjects: Set<BaseDependencyObject>): Set<BaseDependencyObject> {
        val filteredObjects: MutableSet<BaseDependencyObject> = mutableSetOf()
        val keyNames = keyObjects
            .map(BaseDependencyObject::name)
            .toSet()

        if (!directionalFilters.contains(DOWN)) {
            return keyObjects
        }

        filteredObjects.addAll(keyObjects)

        if (keyNames.isNotEmpty()) {  //
            dataSet.getObjects() // todo can filter and collect
                // for this object, are any of the keyNames in it's list of dependencies?

                .forEach { mainObject ->   //
                    val dependencies: Set<String?> = mainObject.dependencies
                    val intersection = Sets.intersection(keyNames, dependencies)
                    if (!intersection.isEmpty()) {
                        filteredObjects.add(mainObject)
                    }
                }
        }

        return filteredObjects
    }
}

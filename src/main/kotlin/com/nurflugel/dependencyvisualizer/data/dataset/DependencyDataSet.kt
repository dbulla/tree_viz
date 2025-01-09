package com.nurflugel.dependencyvisualizer.data.dataset

import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject

/** Created by douglas_bullard on 1/17/16.  */
class DependencyDataSet : BaseDependencyDataSet() {
    private val objectsMap: MutableMap<String, DependencyObject> = HashMap()

    override fun getObjects(): MutableList<BaseDependencyObject> {
        return objectsMap.values.toMutableList()
    }

    override fun get(key: String): BaseDependencyObject {
        return objectsMap[key]!! //todo deal with no value
    }

    override fun containsKey(key: String): Boolean {
        return objectsMap.containsKey(key)
    }

    override fun put(key: String, theObject: BaseDependencyObject) {
        assert(theObject is DependencyObject)
        objectsMap[key] = theObject as DependencyObject
    }
}

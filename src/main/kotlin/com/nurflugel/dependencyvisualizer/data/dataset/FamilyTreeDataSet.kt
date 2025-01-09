package com.nurflugel.dependencyvisualizer.data.dataset

import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.Person

/** Created by douglas_bullard on 1/17/16.  */
class FamilyTreeDataSet : BaseDependencyDataSet() {
    private val objectsMap: MutableMap<String, Person> = HashMap()


    init {
        isFamilyTree = true
    }

    // rather than return the map, add operations on the dataset each type can implement (get, put, contains)
    override fun getObjects(): MutableList<BaseDependencyObject> {
        return objectsMap.values.toMutableList()
    }

    override fun get(key: String): BaseDependencyObject {
        return objectsMap[key]!!
    }

    override fun containsKey(key: String): Boolean {
        return objectsMap.containsKey(key)
    }

    override fun put(key: String, theObject: BaseDependencyObject) {
        assert(theObject is Person)
        objectsMap[key] = theObject as Person
    }
}

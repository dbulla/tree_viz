package com.nurflugel.dependencyvisualizer.data.dataset

import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject

/** Created by douglas_bullard on 1/17/16.  */
class DependencyDataSet : BaseDependencyDataSet() {
    private val objectsMap: MutableMap<String, DependencyObject> = mutableMapOf()

    override fun setValue(name: String, value: BaseDependencyObject) {
        objectsMap[name] = value as DependencyObject
    }

    override fun getObjects(): MutableList<BaseDependencyObject> {
        return objectsMap.values.toMutableList()
    }

    override fun add(newObject: BaseDependencyObject) {
        objectsMap[newObject.name] = newObject as DependencyObject
    }

    override fun containsKey(name: String): Boolean {
        return objectsMap.containsKey(name.trim())
    }

    override fun getValue(name: String): BaseDependencyObject {
        return objectsMap.getValue(name) // replace with get or else?  Still want to log that a new instance was created...
    }
}

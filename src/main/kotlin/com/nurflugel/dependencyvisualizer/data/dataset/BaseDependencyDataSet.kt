package com.nurflugel.dependencyvisualizer.data.dataset

import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject.Companion.replaceAllBadChars
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject
import com.nurflugel.dependencyvisualizer.enums.Ranking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

/**
 * Created by douglas_bullard on 1/18/16.
 */
abstract class BaseDependencyDataSet {
    var isFamilyTree: Boolean = false
    private var rankings: MutableList<Ranking> = mutableListOf()

    abstract fun getObjects(): MutableList<BaseDependencyObject>
    abstract fun add(newObject: BaseDependencyObject)
    abstract fun containsKey(name: String): Boolean
    abstract fun getValue(name: String): BaseDependencyObject
    abstract fun setValue(name: String, value: BaseDependencyObject)

    // If we have the object, return it, else create one, store it, and return that new instance.
    fun objectByName(name: String): BaseDependencyObject {
        val trimmedName = replaceAllBadChars(name.trim { it <= ' ' })
        val exists = containsKey(trimmedName)
        val baseDependencyObject: BaseDependencyObject

        if (exists) {
            baseDependencyObject = getValue(trimmedName) // replace with get or else?  Still want to log that a new instance was created...
        }
        else {
            baseDependencyObject = DependencyObject(trimmedName, Ranking.first().name) // todo create a new instance of the right class

            //            if (LOGGER.isDebugEnabled) {
            //                LOGGER.debug("Adding unregistered object: {} as object of type {}", trimmedName, baseDependencyObject.ranking)
            //            }

            setValue(trimmedName, baseDependencyObject)
        }

        return baseDependencyObject
    }

    /** Deserialization doesn't populate the lists if missing, so we have to rectify it here, make them empty instead */
    @Suppress("SENSELESS_COMPARISON")
    fun rectify() {
        rankings.forEach(Consumer { ranking: Ranking -> Ranking.addRanking(ranking) })
        getObjects()
            .forEach {
                if (it.dependencies == null) it.dependencies = mutableSetOf()
                if (it.notes == null) it.notes = mutableListOf()
            }
    }

    // todo very bad name - what does this do
    fun generateRankings() {
        rankings = Ranking.values().toMutableList()
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DependencyDataSet::class.java)
    }
}

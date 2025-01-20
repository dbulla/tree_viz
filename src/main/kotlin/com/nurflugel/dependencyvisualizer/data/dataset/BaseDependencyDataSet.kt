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

    // todo this is duplicated in the child classes... not good
    //    val objects: MutableList<BaseDependencyObject> = mutableListOf()
    private val dependencyMap: MutableMap<String, BaseDependencyObject> = mutableMapOf()

    fun getObjects(): MutableList<BaseDependencyObject> {
        return dependencyMap.values.toMutableList()
    }

    fun getRankings(): Collection<Ranking> {
        val collect: List<Ranking> = dependencyMap.values.toList()
            .map(BaseDependencyObject::ranking)
            .distinct()
            .map { title: String -> Ranking.valueOf(title) }
        return collect
    }

    fun add(newObject: BaseDependencyObject) {
        dependencyMap.put(newObject.name, newObject)
    }

    // If we have the object, return it, else create one, store it, and return that new instance.
    fun objectByName(name: String): BaseDependencyObject {
        val trimmedName = replaceAllBadChars(name.trim { it <= ' ' })
        val exists = dependencyMap.containsKey(trimmedName)
        val baseDependencyObject: BaseDependencyObject

        if (exists) {
            baseDependencyObject = dependencyMap.getValue(trimmedName) // replace with get or else?  Still want to log that a new instance was created...
        }
        else {
            baseDependencyObject = DependencyObject(trimmedName, Ranking.first().name) // todo create a new instance of the right class

            if (LOGGER.isDebugEnabled) {
                LOGGER.debug("Adding unregistered object: {} as object of type {}", trimmedName, baseDependencyObject.ranking)
            }

            dependencyMap.put(trimmedName, baseDependencyObject)
        }

        return baseDependencyObject
    }

    /**
     * Deserialization doesn't populate the list of ranking types, so we have to rectify it here.
     */
    fun rectify() {
        rankings.forEach(Consumer { ranking: Ranking -> Ranking.addRanking(ranking) })
    }

    fun generateRankingsMap() {
        rankings = Ranking.values().toMutableList()
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DependencyDataSet::class.java)
    }
}

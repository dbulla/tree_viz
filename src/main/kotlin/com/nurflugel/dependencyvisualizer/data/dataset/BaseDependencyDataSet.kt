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

    abstract fun getObjects(): MutableList<BaseDependencyObject>

    // public abstract Map<String,DependencyObject> getObjectsMap() ;
    fun getRankings(): Collection<Ranking> {
        val collect: List<Ranking> = getObjects()
            .map(BaseDependencyObject::ranking)
            .distinct()
            .map { title: String -> Ranking.valueOf(title) }
        return collect
    }

    fun add(newObject: BaseDependencyObject) {
        put(newObject.name, newObject)
    }

    fun getLoaderObjectByName(name: String): BaseDependencyObject {
        val trimmedName = replaceAllBadChars(name.trim { it <= ' ' })
        val exists = containsKey(trimmedName)
        val baseDependencyObject: BaseDependencyObject

        if (exists) {
            baseDependencyObject = get(trimmedName)
        }
        else {
            baseDependencyObject = DependencyObject(trimmedName, Ranking.first().name)

            if (LOGGER.isDebugEnabled) {
                LOGGER.debug("Adding unregistered object: {} as object of type {}", trimmedName, baseDependencyObject.ranking)
            }

            put(trimmedName, baseDependencyObject)
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

    abstract fun get(key: String): BaseDependencyObject

    abstract fun containsKey(key: String): Boolean

    abstract fun put(key: String, theObject: BaseDependencyObject)

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DependencyDataSet::class.java)
    }
}

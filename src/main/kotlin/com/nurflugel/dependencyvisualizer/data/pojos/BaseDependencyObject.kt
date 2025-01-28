package com.nurflugel.dependencyvisualizer.data.pojos

import org.apache.commons.lang3.StringUtils.replace

/**
 * Is there a reason this isn't merged with DependencyObject???  Yes, as Person extends it, too.
 *
 * Created by douglas_bullard on 1/18/16.
 */
open class BaseDependencyObject : Comparable<Any> {
    // ------------------------------ FIELDS ------------------------------
    lateinit var name: String
    lateinit var displayName: String
    var notes: MutableList<String> = mutableListOf()
    lateinit var ranking: String
    var dependencies: MutableSet<String> = mutableSetOf()

    constructor(name: String, ranking: String) {
        this.name = replaceAllBadChars(name)
        this.displayName = name
        this.ranking = ranking
    }

    constructor(name: String, notes: List<String>, ranking: String) {
        this.name = replaceAllBadChars(name)
        this.notes = notes.toMutableList()
        this.ranking = ranking
    }

    constructor()

    // ------------------------ Class Methods ------------------------
    fun addDependency(dependency: String) {
        dependencies.add(dependency)
    }

    fun removeAllDependencies() {
        dependencies.clear()
    }

    // --------------------- Interface Comparable ---------------------
    override fun compareTo(o: Any): Int {
        return if (o is BaseDependencyObject) name.compareTo(o.name)
        else 0
    }

    override fun toString(): String {
        return displayName as String
    }

    companion object {
        /**
         * Returns a job with all spaces and weird characters "fixed" for Dot processing.
         *
         *
         * todo - something better with regular expressions - anything except text and numbers in one expression.  And, add a test!!!
         */
        fun replaceAllBadChars(text: String): String {
            var newValue = text.trim { it <= ' ' }

            newValue = replace(newValue, "-", "_")
            newValue = replace(newValue, "@", "_")
            newValue = replace(newValue, " ", "_")
            newValue = replace(newValue, ".", "_")
            newValue = replace(newValue, "/", "_")
            newValue = replace(newValue, "\\", "_")
            newValue = replace(newValue, "^", "_")

            return newValue
        }
    }
}

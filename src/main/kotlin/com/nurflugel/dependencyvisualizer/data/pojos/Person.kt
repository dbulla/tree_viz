package com.nurflugel.dependencyvisualizer.data.pojos

/**
 * A person is a special type of object, with birth dates, death dates, and spouses.
 */
class Person(name: String, type: String) : BaseDependencyObject(name, type) {
    var spouses: MutableSet<String> = mutableSetOf()
    var birthDate: String? = null
    var deathDate: String? = null

    fun addSpouse(spouse: Person) {
        spouses.add(spouse.name)
    }

    override fun toString(): String {
        return displayName
    }

    fun removeAllSpouses() {
        spouses.clear()
    }
}

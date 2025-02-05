package com.nurflugel.dependencyvisualizer.data.pojos

/**
 * A person is a special type of object, with birth dates & death dates.
 * Spouses are recorded as a list of Marriages in the dataset.
 */
class Person(name: String, type: String) : BaseDependencyObject(name, type) {
    var birthDate: String? = null
    var deathDate: String? = null

    override fun toString(): String {
        return displayName
    }
}

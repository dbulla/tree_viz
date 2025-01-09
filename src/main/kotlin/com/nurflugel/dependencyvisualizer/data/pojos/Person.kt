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

    //    fun getSpouses(): Set<String> {
    //        return this.spouses
    //    }

    //    fun setSpouses(spouses: MutableSet<String>) {
    //        this.spouses = spouses
    //    }

    //    override fun equals(o: Any?): Boolean {
    //        if (o === this) return true
    //        if (o !is Person) return false
    //        if (!o.canEqual(this as Any)) return false
    //        if (!super.equals(o)) return false
    //        val `this$spouses`: Any = this.getSpouses()
    //        val `other$spouses`: Any = o.getSpouses()
    //        if (if (`this$spouses` == null) `other$spouses` != null else (`this$spouses` != `other$spouses`)) return false
    //        val `this$birthDate`: Any? = this.birthDate
    //        val `other$birthDate`: Any? = o.birthDate
    //        if (if (`this$birthDate` == null) `other$birthDate` != null else (`this$birthDate` != `other$birthDate`)) return false
    //        val `this$deathDate`: Any? = this.deathDate
    //        val `other$deathDate`: Any? = o.deathDate
    //        if (if (`this$deathDate` == null) `other$deathDate` != null else (`this$deathDate` != `other$deathDate`)) return false
    //        return true
    //    }
    //
    //    override fun canEqual(other: Any): Boolean {
    //        return other is Person
    //    }
    //
    //    override fun hashCode(): Int {
    //        val PRIME = 59
    //        var result = super.hashCode()
    //        val `$spouses`: Any = this.getSpouses()
    //        result = result * PRIME + (`$spouses`?.hashCode()
    //                                   ?: 43)
    //        val `$birthDate`: Any? = this.birthDate
    //        result = result * PRIME + (`$birthDate`?.hashCode()
    //                                   ?: 43)
    //        val `$deathDate`: Any? = this.deathDate
    //        result = result * PRIME + (`$deathDate`?.hashCode()
    //                                   ?: 43)
    //        return result
    //    }
}

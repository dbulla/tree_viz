package com.nurflugel.dependencyvisualizer.data.dataset

//class Marriage(val person1: String, val person2: String, val marriageDate: LocalDate?) {
class Marriage(val person1: String, val person2: String) {
//    constructor(person1: String, person2: String) : this(person1, person2, null) // no specified marriage date

    /** check if these people are married */
    fun hasMarried(person1: String, person2: String): Boolean {
        return (person1 == this.person1 && person2 == this.person2) || (person1 == this.person2 && person2 == this.person1)
    }

    /** check if this person has ever married */
    fun hasMarried(person: String): Boolean {
        return (person == this.person1) || (person == this.person2)
    }

    /** Get the spouse of the given person in this marriage */
    fun getSpouse(person: String): String {
        return when (person) {
            person1 -> person2
            else    -> person1
        }
    }

    override fun toString(): String {
            return "Marriage(person1=$person1, person2=$person2)"
    }
}

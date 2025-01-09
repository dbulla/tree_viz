package com.nurflugel.dependencyvisualizer.enums

import com.nurflugel.dependencyvisualizer.enums.Ranking.Companion.valueOf

/**
 * todo delete me
 * Created by douglas_bullard on 1/16/16.
 */
class RankingName(var name: String) {
    @Throws(Exception::class)
    fun getRanking(title: String): Ranking {
        return valueOf(title)
    }

    override fun toString(): String {
        return "RankingName(name=" + this.name + ")"
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is RankingName) return false
        val other = o
        if (!other.canEqual(this as Any)) return false
        val `this$name`: Any = this.name
        val `other$name`: Any = other.name
        return `this$name` == `other$name`
    }

    private fun canEqual(other: Any): Boolean {
        return other is RankingName
    }

    override fun hashCode(): Int {
//        val PRIME = 59
//        var result = 1
//        val `$name`: Any = this.name
//        result = result * PRIME + (`$name`?.hashCode()
//                                   ?: 43)
        return name.hashCode()
    }
}

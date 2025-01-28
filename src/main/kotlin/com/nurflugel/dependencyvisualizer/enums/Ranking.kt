package com.nurflugel.dependencyvisualizer.enums

/**
 * This is the representation of the type of bounding polygon for the data item. It has a title, a color, a shape, and a rank. The rank is built up from the list of types, so as
 * new ones are added, the rank is incremented.
 *
 *
 * This is sort of like an enum but is determined from the data loaded
 */
 data class Ranking // --------------------------- CONSTRUCTORS ---------------------------
    (var name: String, var color: Color, var shape: Shape, val rank: Int) :
    Comparable<Any> {
    // --------------------- Interface Comparable ---------------------
    override fun compareTo(o: Any): Int {
        return rank.compareTo((o as Ranking).rank)
    }
//
//    // ------------------------ CANONICAL METHODS ------------------------
    override fun toString(): String {
        return name
    }

    companion object {
        // ------------------------------ FIELDS ------------------------------
        private var rankCounter = 0
        private val types: MutableList<Ranking> = ArrayList()

        fun valueOf(title: String): Ranking {
            return types.firstOrNull { r: Ranking -> r.name == title }
                   ?: valueOf(title, Color.BLACK, Shape.RECTANGLE)
        }

        fun valueOf(title: String, color: Color, shape: Shape): Ranking {
            val first: Ranking? = types.firstOrNull { r: Ranking -> r.name == title }
            val ranking: Ranking = when {
                first !=null -> first
                else         -> {
                    // create the next type if it didn't already exist
                    val type = Ranking(title, color, shape, rankCounter++)

                    types.add(type)
                    type
                }
            }

            return ranking
        }

        fun values(): List<Ranking> {
            return types
        }

        fun first(): Ranking {
            return types[0]
        }

        fun clearRankings() {
            types.clear()
        }

        fun addRanking(ranking: Ranking) {
            types.add(ranking)
        }
    }
}

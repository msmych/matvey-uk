package uk.matvey.drinki.types

sealed class Amount {
    
    data object Some : Amount() {
        override fun toString(): String {
            return "some"
        }
    }
    
    class Oz(val n: Int, val d: Int) : Amount() {
        
        companion object {
            fun parse(s: String): Oz {
                val parts = s.substringBefore("oz").split('/')
                return Oz(parts[0].toInt(), parts.getOrNull(1)?.toInt() ?: 1)
            }
        }
        
        override fun toString(): String {
            return "$n/${d}oz"
        }
    }
    
    class Cl(val n: Int) : Amount() {
    }
    
    companion object {
        fun parse(s: String): Amount {
            return when {
                s == "some" -> Some
                s.endsWith("oz") -> Oz.parse(s)
                else -> throw IllegalArgumentException()
            }
        }
    }
}

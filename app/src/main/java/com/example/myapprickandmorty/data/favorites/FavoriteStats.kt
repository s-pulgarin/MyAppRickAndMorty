package com.example.myapprickandmorty.data.favorites

/** Resumen de los favoritos del usuario para mostrar en el Perfil. */
data class FavoriteStats(
    val total: Int,
    val alive: Int,
    val dead: Int,
    val unknown: Int,
    /** Especies más repetidas (máx. 3), de mayor a menor: especie -> cantidad. */
    val topSpecies: List<Pair<String, Int>>,
    val lastAdded: FavoriteCharacter?
) {
    companion object {
        fun from(favorites: List<FavoriteCharacter>): FavoriteStats {
            val alive = favorites.count { it.status.equals("alive", ignoreCase = true) }
            val dead = favorites.count { it.status.equals("dead", ignoreCase = true) }
            val topSpecies = favorites
                .groupingBy { it.species.ifBlank { "unknown" } }
                .eachCount()
                .entries
                .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
                .take(3)
                .map { it.key to it.value }
            return FavoriteStats(
                total = favorites.size,
                alive = alive,
                dead = dead,
                unknown = favorites.size - alive - dead,
                topSpecies = topSpecies,
                lastAdded = favorites.maxByOrNull { it.addedAt }
            )
        }
    }
}

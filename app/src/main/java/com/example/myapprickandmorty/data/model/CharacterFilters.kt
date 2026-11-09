package com.example.myapprickandmorty.data.model

/** Una opción de filtro: [value] es lo que se manda a la API y [label] lo que ve el usuario. */
data class FilterOption(val value: String, val label: String)

object FilterOptions {
    val status = listOf(
        FilterOption("alive", "Vivo"),
        FilterOption("dead", "Muerto"),
        FilterOption("unknown", "Desconocido")
    )

    val gender = listOf(
        FilterOption("male", "Masculino"),
        FilterOption("female", "Femenino"),
        FilterOption("genderless", "Sin género"),
        FilterOption("unknown", "Desconocido")
    )

    /** La API acepta cualquier texto de especie; estas son las más comunes. */
    val species = listOf(
        FilterOption("Human", "Humano"),
        FilterOption("Alien", "Alienígena"),
        FilterOption("Humanoid", "Humanoide"),
        FilterOption("Robot", "Robot"),
        FilterOption("Animal", "Animal"),
        FilterOption("Cronenberg", "Cronenberg"),
        FilterOption("Mythological Creature", "Criatura mitológica"),
        FilterOption("Poopybutthole", "Poopybutthole"),
        FilterOption("Disease", "Enfermedad")
    )
}

/** Filtros activos de la lista de personajes. null = sin filtro. */
data class CharacterFilters(
    val status: String? = null,
    val gender: String? = null,
    val species: String? = null
) {
    val activeCount: Int get() = listOfNotNull(status, gender, species).size
    val isEmpty: Boolean get() = activeCount == 0
}

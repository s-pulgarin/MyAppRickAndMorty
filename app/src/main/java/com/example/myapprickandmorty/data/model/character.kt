package com.example.myapprickandmorty.data.model

data class CharacterResponse(
    val info: PageInfo,
    val results: List<RickCharacter>
)

data class PageInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

data class RickCharacter(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String = "",
    val gender: String = "",
    val image: String,
    val origin: Origin,
    val location: Origin? = null,
    val episode: List<String> = emptyList()
) {
    /** IDs de los episodios, sacados del final de cada URL (".../episode/12" -> 12). */
    val episodeIds: List<Int>
        get() = episode.mapNotNull { it.substringAfterLast('/').toIntOrNull() }
}

/** Nombre y URL de un lugar (se usa para el origen y para la última ubicación). */
data class Origin(
    val name: String,
    val url: String = ""
)

data class Episode(
    val id: Int,
    val name: String,
    val air_date: String,
    val episode: String,
    val characters: List<String> = emptyList()
) {
    /** Temporada a partir del código ("S03E07" -> 3). */
    val season: Int get() = episode.substringAfter('S').substringBefore('E').toIntOrNull() ?: 0

    /** Número dentro de la temporada ("S03E07" -> 7). */
    val number: Int get() = episode.substringAfter('E').toIntOrNull() ?: 0

    val characterIds: List<Int>
        get() = characters.mapNotNull { it.substringAfterLast('/').toIntOrNull() }
}

data class EpisodeResponse(
    val info: PageInfo,
    val results: List<Episode>
)

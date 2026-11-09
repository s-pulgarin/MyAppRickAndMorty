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
    val episode: String
)

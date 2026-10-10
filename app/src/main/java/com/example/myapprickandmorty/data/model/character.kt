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
    val image: String,
    val origin: Origin
)

data class Origin(
    val name: String
)

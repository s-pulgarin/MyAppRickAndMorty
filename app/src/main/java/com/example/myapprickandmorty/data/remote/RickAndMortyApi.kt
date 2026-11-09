package com.example.myapprickandmorty.data.remote

import com.example.myapprickandmorty.data.model.RickCharacter
import com.example.myapprickandmorty.data.model.CharacterResponse
import com.example.myapprickandmorty.data.model.Episode
import com.example.myapprickandmorty.data.model.EpisodeResponse
import com.example.myapprickandmorty.data.model.Location
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("gender") gender: String? = null,
        @Query("species") species: String? = null
    ): CharacterResponse

    @GET("character/{id}")
    suspend fun getCharacterById(
        @Path("id") id: Int
    ): RickCharacter

    /** Ojo: con 2+ IDs la API responde una lista; con uno solo responde un objeto. */
    @GET("character/{ids}")
    suspend fun getCharactersByIds(
        @Path("ids") ids: String
    ): List<RickCharacter>

    @GET("location/{id}")
    suspend fun getLocationById(
        @Path("id") id: Int
    ): Location

    @GET("episode")
    suspend fun getEpisodes(
        @Query("page") page: Int = 1
    ): EpisodeResponse

    @GET("episode/{id}")
    suspend fun getEpisodeById(
        @Path("id") id: Int
    ): Episode

    /** Ojo: con 2+ IDs la API responde una lista; con uno solo responde un objeto. */
    @GET("episode/{ids}")
    suspend fun getEpisodesByIds(
        @Path("ids") ids: String
    ): List<Episode>
}

object RetrofitClient {
    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    val api: RickAndMortyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RickAndMortyApi::class.java)
    }
}
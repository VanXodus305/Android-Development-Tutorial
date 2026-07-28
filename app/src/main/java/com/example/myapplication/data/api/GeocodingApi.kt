package com.example.myapplication.data.api

import com.example.myapplication.data.model.GeocodingDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
	@GET("search")
	suspend fun searchLocation(
		@Query("name") name: String,
		@Query("count") count: Int = 5,
		@Query("language") language: String = "en",
		@Query("format") format: String = "json"
	): Response<GeocodingDto>

	companion object {
		const val BASE_URL = "https://geocoding-api.open-meteo.com/v1/"
	}
}

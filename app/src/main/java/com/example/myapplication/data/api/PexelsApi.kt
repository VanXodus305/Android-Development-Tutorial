package com.example.myapplication.data.api

import com.example.myapplication.data.model.PexelsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PexelsApi {
	@GET("videos/search")
	suspend fun searchVideos(
		@Header("Authorization") apiKey: String,
		@Query("query") query: String,
		@Query("orientation") orientation: String = "portrait",
		@Query("per_page") perPage: Int = 1,
		@Query("size") size: String = "medium"
	): Response<PexelsResponseDto>

	companion object {
		const val BASE_URL = "https://api.pexels.com/v1/"
	}
}

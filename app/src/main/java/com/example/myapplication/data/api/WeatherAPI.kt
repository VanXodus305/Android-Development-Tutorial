package com.example.myapplication.data.api

import com.example.myapplication.data.model.WeatherDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
	@GET("forecast")
	suspend fun getWeatherData(
		@Query("latitude") lat: Double,
		@Query("longitude") lon: Double,
		@Query("current") currentVariables: String = "temperature_2m,wind_speed_10m,weather_code,relative_humidity_2m,apparent_temperature,is_day",
		@Query("timezone") timezone: String = "auto"
	): Response<WeatherDto>

	companion object {
		// Appending /v1/ directly to the Base URL stabilizes Retrofit routing
		const val BASE_URL = "https://api.open-meteo.com/v1/"
	}
}
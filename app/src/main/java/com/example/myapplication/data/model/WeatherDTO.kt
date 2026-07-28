package com.example.myapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
	@SerialName("current") val current: CurrentWeatherDto
)

@Serializable
data class CurrentWeatherDto(
	@SerialName("temperature_2m") val temperature: Double,
	@SerialName("wind_speed_10m") val windSpeed: Double,
	@SerialName("weather_code") val weatherCode: Int,
	@SerialName("relative_humidity_2m") val humidity: Int,
	@SerialName("apparent_temperature") val feelsLike: Double,
	@SerialName("is_day") val isDay: Int
)
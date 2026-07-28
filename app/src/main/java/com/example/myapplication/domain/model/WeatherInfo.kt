package com.example.myapplication.domain.model

data class WeatherInfo(
	val temperature: Double,
	val windSpeed: Double,
	val condition: String,
	val humidity: Int,
	val feelsLike: Double,
	val isDay: Boolean
)
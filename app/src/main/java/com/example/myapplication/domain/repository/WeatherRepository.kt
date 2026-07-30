package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.LocationInfo
import com.example.myapplication.domain.model.WeatherInfo
import com.example.myapplication.domain.util.Resource

interface WeatherRepository {
	suspend fun fetchWeather(lat: Double, lon: Double): Resource<WeatherInfo>
	suspend fun searchLocation(query: String): Resource<List<LocationInfo>>
	suspend fun fetchBackgroundVideo(condition: String, timeOfDay: String): Resource<String>
}

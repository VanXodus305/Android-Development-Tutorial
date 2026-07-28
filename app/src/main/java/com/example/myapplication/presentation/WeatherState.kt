package com.example.myapplication.presentation

import com.example.myapplication.domain.model.LocationInfo
import com.example.myapplication.domain.model.WeatherInfo

data class WeatherState(
	val weatherInfo: WeatherInfo? = null,
	val isLoading: Boolean = false,
	val error: String? = null,
	val searchQuery: String = "",
	val searchResults: List<LocationInfo> = emptyList(),
	val locationName: String = "Your Location",
	val isSearching: Boolean = false
)

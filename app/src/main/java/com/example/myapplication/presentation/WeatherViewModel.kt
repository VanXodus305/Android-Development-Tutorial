package com.example.myapplication.presentation

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.location.DefaultLocationTracker
import com.example.myapplication.data.repository.WeatherRepositoryImpl
import com.example.myapplication.domain.location.LocationTracker
import com.example.myapplication.domain.model.LocationInfo
import com.example.myapplication.domain.repository.WeatherRepository
import com.example.myapplication.domain.util.Resource
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
	private val repository: WeatherRepository = WeatherRepositoryImpl()
	private val locationTracker: LocationTracker = DefaultLocationTracker(
		LocationServices.getFusedLocationProviderClient(application),
		application
	)

	private val _state = mutableStateOf(WeatherState())
	val state: State<WeatherState> = _state

	private var searchJob: Job? = null

	init {
		loadWeatherForCurrentLocation()
	}

	fun loadWeatherForCurrentLocation() {
		_state.value = _state.value.copy(isLoading = true, error = null)
		viewModelScope.launch {
			val location = locationTracker.getCurrentLocation()
			if (location != null) {
				fetchWeather(location.latitude, location.longitude, "Your Location")
			} else {
				_state.value = _state.value.copy(
					isLoading = false,
					error = "Couldn't retrieve location. Make sure to grant permission and enable GPS."
				)
			}
		}
	}

	fun onSearchQueryChange(query: String) {
		_state.value = _state.value.copy(searchQuery = query)
		searchJob?.cancel()
		if (query.length >= 2) {
			_state.value = _state.value.copy(isSearching = true)
			searchJob = viewModelScope.launch {
				delay(500)
				when (val result = repository.searchLocation(query)) {
					is Resource.Success -> {
						_state.value = _state.value.copy(searchResults = result.data, isSearching = false)
					}

					is Resource.Error -> {
						_state.value = _state.value.copy(isSearching = false)
					}
				}
			}
		} else {
			_state.value = _state.value.copy(searchResults = emptyList(), isSearching = false)
		}
	}

	fun onLocationSelected(location: LocationInfo) {
		_state.value = _state.value.copy(
			searchQuery = "",
			searchResults = emptyList(),
			isLoading = true
		)
		fetchWeather(location.latitude, location.longitude, location.name)
	}

	private fun fetchWeather(lat: Double, lon: Double, name: String) {
		_state.value = _state.value.copy(isLoading = true, error = null)
		viewModelScope.launch {
			when (val result = repository.fetchWeather(lat, lon)) {
				is Resource.Success -> {
					_state.value = _state.value.copy(
						weatherInfo = result.data,
						isLoading = false,
						locationName = name
					)
				}

				is Resource.Error -> {
					_state.value = _state.value.copy(
						error = result.message,
						isLoading = false
					)
				}
			}
		}
	}
}

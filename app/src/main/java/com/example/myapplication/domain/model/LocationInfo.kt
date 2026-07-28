package com.example.myapplication.domain.model

data class LocationInfo(
	val name: String,
	val latitude: Double,
	val longitude: Double,
	val country: String? = null,
	val region: String? = null
)

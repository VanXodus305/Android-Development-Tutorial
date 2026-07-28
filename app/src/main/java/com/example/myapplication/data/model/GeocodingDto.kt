package com.example.myapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingDto(
	@SerialName("results") val results: List<GeocodingResultDto>? = null
)

@Serializable
data class GeocodingResultDto(
	@SerialName("name") val name: String,
	@SerialName("latitude") val latitude: Double,
	@SerialName("longitude") val longitude: Double,
	@SerialName("country") val country: String? = null,
	@SerialName("admin1") val admin1: String? = null // Often the state or region
)

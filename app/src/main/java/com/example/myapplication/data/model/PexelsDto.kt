package com.example.myapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PexelsResponseDto(
	@SerialName("videos") val videos: List<PexelsVideoDto>
)

@Serializable
data class PexelsVideoDto(
	@SerialName("video_files") val videoFiles: List<PexelsVideoFileDto>
)

@Serializable
data class PexelsVideoFileDto(
	@SerialName("link") val link: String,
	@SerialName("quality") val quality: String? = null,
	@SerialName("width") val width: Int? = null,
	@SerialName("height") val height: Int? = null
)

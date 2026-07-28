package com.example.myapplication.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.WeatherInfo
import kotlin.math.sin

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
	val state by viewModel.state

	LaunchedEffect(Unit) {
		viewModel.loadWeatherForCurrentLocation()
	}

	Box(modifier = Modifier.fillMaxSize()) {
		AnimatedBackground(isDay = state.weatherInfo?.isDay != false)

		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 20.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Spacer(modifier = Modifier.height(56.dp))

			SearchBar(
				query = state.searchQuery,
				onQueryChange = viewModel::onSearchQueryChange,
				searchResults = state.searchResults,
				onLocationSelected = viewModel::onLocationSelected,
				isSearching = state.isSearching
			)

			Spacer(modifier = Modifier.height(24.dp))

			AnimatedContent(
				targetState = state,
				transitionSpec = {
					fadeIn(animationSpec = tween(600)) togetherWith fadeOut(animationSpec = tween(600))
				},
				label = "WeatherStateContent"
			) { targetState ->
				Box(modifier = Modifier.fillMaxSize()) {
					when {
						targetState.isLoading -> {
							Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
								CircularProgressIndicator(color = Color.White)
							}
						}

						targetState.error != null -> {
							ErrorLayout(
								error = targetState.error,
								onRetry = { viewModel.loadWeatherForCurrentLocation() })
						}

						targetState.weatherInfo != null -> {
							WeatherContent(
								locationName = targetState.locationName,
								weatherInfo = targetState.weatherInfo
							)
						}
					}
				}
			}
		}
	}
}

@Composable
fun AnimatedBackground(isDay: Boolean) {
	val infiniteTransition = rememberInfiniteTransition(label = "BackgroundTransition")

	val phase by infiniteTransition.animateFloat(
		initialValue = 0f,
		targetValue = 2f * Math.PI.toFloat(),
		animationSpec = infiniteRepeatable(
			animation = tween(10000, easing = LinearEasing),
			repeatMode = RepeatMode.Restart
		),
		label = "PhaseAnimation"
	)

	val color1 by animateColorAsState(
		targetValue = if (isDay) Color(0xFF4facfe) else Color(0xFF0F2027),
		animationSpec = tween(1000), label = "Color1"
	)
	val color2 by animateColorAsState(
		targetValue = if (isDay) Color(0xFF00f2fe) else Color(0xFF2C5364),
		animationSpec = tween(1000), label = "Color2"
	)

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(Brush.verticalGradient(listOf(color1, color2)))
	) {
		Canvas(
			modifier = Modifier
				.fillMaxSize()
				.blur(80.dp)
		) {
			val width = size.width
			val height = size.height

			drawCircle(
				color = color1.copy(alpha = 0.4f),
				radius = width * 0.6f,
				center = Offset(
					x = width * (0.5f + 0.2f * sin(phase.toDouble()).toFloat()),
					y = height * (0.2f + 0.1f * sin(phase.toDouble() + 1.0).toFloat())
				)
			)

			drawCircle(
				color = color2.copy(alpha = 0.3f),
				radius = width * 0.8f,
				center = Offset(
					x = width * (0.3f + 0.15f * sin(phase.toDouble() * 0.7 + 2.0).toFloat()),
					y = height * (0.7f + 0.2f * sin(phase.toDouble() * 0.8 + 3.0).toFloat())
				)
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
	query: String,
	onQueryChange: (String) -> Unit,
	searchResults: List<com.example.myapplication.domain.model.LocationInfo>,
	onLocationSelected: (com.example.myapplication.domain.model.LocationInfo) -> Unit,
	isSearching: Boolean
) {
	Column(modifier = Modifier.fillMaxWidth()) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(24.dp))
				.background(Color.White.copy(alpha = 0.12f))
				.border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
		) {
			Column {
				TextField(
					value = query,
					onValueChange = onQueryChange,
					modifier = Modifier.fillMaxWidth(),
					placeholder = { Text("Search city...", color = Color.White.copy(alpha = 0.5f)) },
					leadingIcon = {
						Icon(
							Icons.Default.Search,
							contentDescription = null,
							tint = Color.White
						)
					},
					trailingIcon = {
						if (query.isNotEmpty()) {
							IconButton(onClick = { onQueryChange("") }) {
								Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
							}
						}
					},
					colors = TextFieldDefaults.colors(
						focusedContainerColor = Color.Transparent,
						unfocusedContainerColor = Color.Transparent,
						cursorColor = Color.White,
						focusedIndicatorColor = Color.Transparent,
						unfocusedIndicatorColor = Color.Transparent,
						focusedTextColor = Color.White,
						unfocusedTextColor = Color.White
					),
					singleLine = true
				)
				if (isSearching) {
					LinearProgressIndicator(
						modifier = Modifier
							.fillMaxWidth()
							.height(2.dp),
						color = Color.White,
						trackColor = Color.Transparent
					)
				}
			}
		}

		AnimatedVisibility(
			visible = searchResults.isNotEmpty(),
			enter = expandVertically() + fadeIn(),
			exit = shrinkVertically() + fadeOut()
		) {
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 8.dp)
					.border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp)),
				colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
				shape = RoundedCornerShape(24.dp)
			) {
				LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) {
					items(searchResults) { location ->
						ListItem(
							headlineContent = {
								Text(
									location.name,
									color = Color.White,
									fontWeight = FontWeight.SemiBold
								)
							},
							supportingContent = {
								Text(
									"${location.region ?: ""}${if (location.region != null) ", " else ""}${location.country ?: ""}",
									color = Color.White.copy(alpha = 0.6f),
									fontSize = 12.sp
								)
							},
							modifier = Modifier
								.clickable { onLocationSelected(location) }
								.background(Color.Transparent),
							colors = ListItemDefaults.colors(containerColor = Color.Transparent)
						)
						HorizontalDivider(
							modifier = Modifier.padding(horizontal = 16.dp),
							color = Color.White.copy(alpha = 0.1f)
						)
					}
				}
			}
		}
	}
}

@Composable
fun WeatherContent(
	locationName: String,
	weatherInfo: WeatherInfo
) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = locationName,
			fontSize = 28.sp,
			fontWeight = FontWeight.Medium,
			color = Color.White,
			textAlign = TextAlign.Center
		)

		Spacer(modifier = Modifier.height(16.dp))

		val infiniteTransition = rememberInfiniteTransition(label = "IconPulse")
		val scale by infiniteTransition.animateFloat(
			initialValue = 1f,
			targetValue = 1.05f,
			animationSpec = infiniteRepeatable(
				animation = tween(2000, easing = EaseInOutSine),
				repeatMode = RepeatMode.Reverse
			),
			label = "ScaleAnimation"
		)

		Icon(
			imageVector = getWeatherIcon(weatherInfo.condition),
			contentDescription = null,
			tint = Color.White,
			modifier = Modifier
				.size(160.dp)
				.graphicsLayer(scaleX = scale, scaleY = scale)
		)

		Text(
			text = "${weatherInfo.temperature.toInt()}°",
			fontSize = 110.sp,
			fontWeight = FontWeight.Thin,
			color = Color.White
		)

		Text(
			text = weatherInfo.condition,
			fontSize = 22.sp,
			fontWeight = FontWeight.Light,
			color = Color.White.copy(alpha = 0.9f)
		)

		Spacer(modifier = Modifier.height(32.dp))

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(32.dp))
				.background(Color.White.copy(alpha = 0.08f))
				.border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(32.dp))
				.padding(vertical = 24.dp),
			horizontalArrangement = Arrangement.SpaceEvenly
		) {
			WeatherDetailItem(Icons.Default.Thermostat, "${weatherInfo.feelsLike.toInt()}°", "Feels Like")
			WeatherDetailItem(Icons.Default.WaterDrop, "${weatherInfo.humidity}%", "Humidity")
			WeatherDetailItem(Icons.Default.Air, "${weatherInfo.windSpeed} km/h", "Wind")
		}
	}
}

@Composable
fun WeatherDetailItem(icon: ImageVector, value: String, label: String) {
	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
		Spacer(modifier = Modifier.height(8.dp))
		Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
		Text(text = label, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
	}
}

@Composable
fun ErrorLayout(error: String, onRetry: () -> Unit) {
	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Icon(
			Icons.Default.CloudOff,
			contentDescription = null,
			tint = Color.White,
			modifier = Modifier.size(80.dp)
		)
		Spacer(modifier = Modifier.height(16.dp))
		Text(
			text = error,
			color = Color.White,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(horizontal = 32.dp)
		)
		Spacer(modifier = Modifier.height(24.dp))
		Button(
			onClick = onRetry,
			colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
			shape = RoundedCornerShape(16.dp)
		) {
			Text("Retry", color = Color.White)
		}
	}
}

fun getWeatherIcon(condition: String): ImageVector {
	return when {
		condition.contains("Clear", ignoreCase = true) -> Icons.Default.WbSunny
		condition.contains("Partly", ignoreCase = true) -> Icons.Default.CloudQueue
		condition.contains("Cloud", ignoreCase = true) -> Icons.Default.Cloud
		condition.contains("Rain", ignoreCase = true) -> Icons.Default.Thunderstorm
		condition.contains("Snow", ignoreCase = true) -> Icons.Default.AcUnit
		condition.contains("Fog", ignoreCase = true) -> Icons.Default.Cloud
		else -> Icons.Default.WbCloudy
	}
}

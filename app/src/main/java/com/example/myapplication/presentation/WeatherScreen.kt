package com.example.myapplication.presentation

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.example.myapplication.domain.model.WeatherInfo
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.time.Duration.Companion.milliseconds

@UnstableApi
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
	val state by viewModel.state

	LaunchedEffect(Unit) {
		viewModel.loadWeatherForCurrentLocation()
	}

	Box(modifier = Modifier.fillMaxSize()) {
		DynamicWeatherBackground(
			isDay = state.weatherInfo?.isDay != false,
			videoUrl = state.backgroundVideoUrl
		)

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
				isSearching = state.isSearching
			)

			Spacer(modifier = Modifier.height(24.dp))

			AnimatedContent(
				targetState = state,
				transitionSpec = {
					fadeIn(animationSpec = tween(800)) togetherWith fadeOut(animationSpec = tween(800))
				},
				label = "MainContentTransition"
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

		// Floating Search Results Dropdown (Improved Visibility and Interaction)
		if (state.searchResults.isNotEmpty() || state.isSearching) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(Color.Black.copy(alpha = 0.4f))
					.clickable { viewModel.onSearchQueryChange("") } // Click background to close
					.zIndex(100f) // Extremely high zIndex
			) {
				Card(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 120.dp)
						.padding(horizontal = 20.dp)
						.clickable(enabled = false) { } // Consume clicks inside
						.border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
					colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
					shape = RoundedCornerShape(24.dp),
					elevation = CardDefaults.cardElevation(16.dp)
				) {
					if (state.isSearching && state.searchResults.isEmpty()) {
						Box(
							modifier = Modifier
								.fillMaxWidth()
								.padding(40.dp),
							contentAlignment = Alignment.Center
						) {
							CircularProgressIndicator(color = Color.White)
						}
					} else {
						LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
							items(state.searchResults) { location ->
								ListItem(
									headlineContent = {
										Text(
											location.name,
											color = Color.White,
											fontWeight = FontWeight.ExtraBold
										)
									},
									supportingContent = {
										Text(
											"${location.region ?: ""}${if (location.region != null) ", " else ""}${location.country ?: ""}",
											color = Color.White.copy(alpha = 0.7f),
											fontSize = 14.sp
										)
									},
									modifier = Modifier.clickable {
										Log.d("WeatherScreen", "Selected location: ${location.name}")
										viewModel.onLocationSelected(location)
									},
									colors = ListItemDefaults.colors(containerColor = Color.Transparent)
								)
								HorizontalDivider(
									modifier = Modifier.padding(horizontal = 16.dp),
									color = Color.White.copy(alpha = 0.15f)
								)
							}
						}
					}
				}
			}
		}
	}
}

@UnstableApi
@Composable
fun DynamicWeatherBackground(isDay: Boolean, videoUrl: String?) {
	val currentVideoUrl = remember(videoUrl) { videoUrl }

	Box(modifier = Modifier.fillMaxSize()) {
		// High-Quality Vibrant Fallback Gradient
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(
					Brush.verticalGradient(
						if (isDay) listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
						else listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
					)
				)
		)

		if (currentVideoUrl != null) {
			VideoBackground(videoUrl = currentVideoUrl)
		}

		val overlayOpacity by animateFloatAsState(
			targetValue = if (isDay) 0.15f else 0.35f,
			animationSpec = tween(1500), label = "OverlayOpacity"
		)
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(Color.Black.copy(alpha = overlayOpacity))
		)
	}
}

@UnstableApi
@Composable
fun VideoBackground(videoUrl: String) {
	val context = LocalContext.current
	val lifecycleOwner = LocalLifecycleOwner.current
	var isVideoReady by remember { mutableStateOf(false) }

	val unsafeOkHttpClient = remember { createUnsafeOkHttpClient() }

	val exoPlayer = remember {
		ExoPlayer.Builder(context).build().apply {
			repeatMode = Player.REPEAT_MODE_ALL
			playWhenReady = true
			addListener(object : Player.Listener {
				override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
					Log.e("VideoBackground", "ExoPlayer Error: ${error.message} (URL: $videoUrl)", error)
					isVideoReady = false
				}

				override fun onPlaybackStateChanged(state: Int) {
					Log.d("VideoBackground", "ExoPlayer State: $state")
					if (state == Player.STATE_READY) isVideoReady = true
				}
			})
		}
	}

	LaunchedEffect(videoUrl) {
		isVideoReady = false
		val dataSourceFactory = OkHttpDataSource.Factory(unsafeOkHttpClient)
			.setUserAgent("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36")

		val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
			.createMediaSource(MediaItem.fromUri(videoUrl.toUri()))

		exoPlayer.setMediaSource(mediaSource)
		exoPlayer.prepare()
	}

	DisposableEffect(lifecycleOwner) {
		val observer = LifecycleEventObserver { _, event ->
			when (event) {
				Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
				Lifecycle.Event.ON_RESUME -> exoPlayer.play()
				else -> {}
			}
		}
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose {
			lifecycleOwner.lifecycle.removeObserver(observer)
			exoPlayer.release()
		}
	}

	Box(modifier = Modifier.fillMaxSize()) {
		AnimatedVisibility(
			visible = isVideoReady,
			enter = fadeIn(tween(1200)),
			exit = fadeOut(tween(600))
		) {
			AndroidView(
				factory = {
					PlayerView(context).apply {
						player = exoPlayer
						useController = false
						setBackgroundColor(android.graphics.Color.TRANSPARENT)
						setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
						resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
						layoutParams = FrameLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.MATCH_PARENT
						)
					}
				},
				modifier = Modifier.fillMaxSize()
			)
		}
	}
}

fun createUnsafeOkHttpClient(): OkHttpClient {
	val trustAllCerts = arrayOf<TrustManager>(
		@SuppressLint("CustomX509TrustManager")
		object : X509TrustManager {
			@SuppressLint("TrustAllX509TrustManager")
			override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
			}

			@SuppressLint("TrustAllX509TrustManager")
			override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
			}

			override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
		}
	)
	val sslContext = SSLContext.getInstance("SSL")
	sslContext.init(null, trustAllCerts, SecureRandom())
	return OkHttpClient.Builder()
		.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
		.hostnameVerifier { _, _ -> true }
		.build()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
	query: String,
	onQueryChange: (String) -> Unit,
	isSearching: Boolean
) {
	Surface(
		modifier = Modifier
			.fillMaxWidth()
			.zIndex(50f),
		color = Color.White.copy(alpha = 0.2f),
		shape = RoundedCornerShape(24.dp),
		border = BorderStroke(
			1.dp,
			Brush.linearGradient(listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f)))
		)
	) {
		Column {
			TextField(
				value = query,
				onValueChange = {
					Log.d("WeatherScreen", "Search input: $it")
					onQueryChange(it)
				},
				modifier = Modifier.fillMaxWidth(),
				placeholder = { Text("Search city...", color = Color.White.copy(alpha = 0.7f)) },
				leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
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
}

@Composable
fun WeatherContent(
	locationName: String,
	weatherInfo: WeatherInfo
) {
	var visible by remember { mutableStateOf(false) }
	LaunchedEffect(weatherInfo) {
		visible = false
		delay(100.milliseconds)
		visible = true
	}

	Column(
		modifier = Modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Spacer(modifier = Modifier.height(20.dp))

		AnimatedVisibility(visible = visible, enter = slideInVertically { -40 } + fadeIn(tween(600))) {
			Text(
				text = locationName,
				fontSize = 32.sp,
				fontWeight = FontWeight.Bold,
				color = Color.White,
				textAlign = TextAlign.Center
			)
		}

		Spacer(modifier = Modifier.height(20.dp))

		val infiniteTransition = rememberInfiniteTransition(label = "MainIconPulse")
		val scale by infiniteTransition.animateFloat(
			initialValue = 1f,
			targetValue = 1.08f,
			animationSpec = infiniteRepeatable(
				animation = tween(2500, easing = EaseInOutSine),
				repeatMode = RepeatMode.Reverse
			),
			label = "IconScale"
		)

		AnimatedVisibility(visible = visible, enter = scaleIn(tween(800)) + fadeIn(tween(800))) {
			Icon(
				imageVector = getWeatherIcon(weatherInfo.condition),
				contentDescription = null,
				tint = Color.White,
				modifier = Modifier
					.size(160.dp)
					.graphicsLayer(scaleX = scale, scaleY = scale)
			)
		}

		AnimatedVisibility(visible = visible, enter = fadeIn(tween(1000, 400))) {
			Column(horizontalAlignment = Alignment.CenterHorizontally) {
				Text(
					text = "${weatherInfo.temperature.toInt()}°",
					fontSize = 120.sp,
					fontWeight = FontWeight.ExtraLight,
					color = Color.White
				)
				Text(
					text = weatherInfo.condition,
					fontSize = 24.sp,
					fontWeight = FontWeight.Light,
					color = Color.White.copy(alpha = 0.9f)
				)
			}
		}

		Spacer(modifier = Modifier.weight(1f))

		AnimatedVisibility(
			visible = visible,
			enter = slideInVertically { 100 } + fadeIn(tween(800, 600))) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 40.dp)
					.clip(RoundedCornerShape(32.dp))
					.background(Color.White.copy(alpha = 0.12f))
					.border(
						width = 1.dp,
						brush = Brush.verticalGradient(
							listOf(
								Color.White.copy(alpha = 0.3f),
								Color.Transparent
							)
						),
						shape = RoundedCornerShape(32.dp)
					)
					.padding(vertical = 28.dp),
				horizontalArrangement = Arrangement.SpaceEvenly
			) {
				WeatherDetailItem(
					Icons.Default.Thermostat,
					"${weatherInfo.feelsLike.toInt()}°",
					"Feels Like"
				)
				WeatherDetailItem(Icons.Default.WaterDrop, "${weatherInfo.humidity}%", "Humidity")
				WeatherDetailItem(Icons.Default.Air, "${weatherInfo.windSpeed} km/h", "Wind")
			}
		}
	}
}

@Composable
fun WeatherDetailItem(icon: ImageVector, value: String, label: String) {
	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
		Spacer(modifier = Modifier.height(8.dp))
		Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
		Text(text = label, color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
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
			modifier = Modifier.size(90.dp)
		)
		Spacer(modifier = Modifier.height(20.dp))
		Text(
			text = error,
			color = Color.White,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(horizontal = 40.dp)
		)
		Spacer(modifier = Modifier.height(30.dp))
		Button(
			onClick = onRetry,
			colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
			shape = RoundedCornerShape(20.dp)
		) {
			Text("Retry", color = Color.White)
		}
	}
}

fun getWeatherIcon(condition: String): ImageVector {
	return when {
		condition.contains("Thunderstorm", ignoreCase = true) -> Icons.Default.Thunderstorm
		condition.contains("Rain", ignoreCase = true) || condition.contains(
			"Drizzle",
			ignoreCase = true
		) -> Icons.Default.Thunderstorm

		condition.contains("Snow", ignoreCase = true) -> Icons.Default.AcUnit
		condition.contains("Fog", ignoreCase = true) -> Icons.Default.Cloud
		condition.contains("Clear Sky", ignoreCase = true) -> Icons.Default.WbSunny
		condition.contains("Mainly Clear", ignoreCase = true) || condition.contains(
			"Cloud",
			ignoreCase = true
		) -> Icons.Default.CloudQueue

		else -> Icons.Default.WbCloudy
	}
}

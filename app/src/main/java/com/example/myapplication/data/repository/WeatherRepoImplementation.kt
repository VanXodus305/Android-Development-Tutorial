package com.example.myapplication.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.myapplication.data.api.GeocodingApi
import com.example.myapplication.data.api.PexelsApi
import com.example.myapplication.data.api.WeatherApi
import com.example.myapplication.domain.model.LocationInfo
import com.example.myapplication.domain.model.WeatherInfo
import com.example.myapplication.domain.repository.WeatherRepository
import com.example.myapplication.domain.util.Resource
import kotlinx.serialization.json.Json
import okhttp3.Dns
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class WeatherRepositoryImpl : WeatherRepository {

	private val okHttpClient = createUnsafeOkHttpClient()

	private val json = Json {
		ignoreUnknownKeys = true
	}

	private val weatherApi = Retrofit.Builder()
		.baseUrl(WeatherApi.BASE_URL)
		.client(okHttpClient)
		.addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
		.build()
		.create(WeatherApi::class.java)

	private val geocodingApi = Retrofit.Builder()
		.baseUrl(GeocodingApi.BASE_URL)
		.client(okHttpClient)
		.addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
		.build()
		.create(GeocodingApi::class.java)

	private val pexelsApi = Retrofit.Builder()
		.baseUrl(PexelsApi.BASE_URL)
		.client(okHttpClient)
		.addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
		.build()
		.create(PexelsApi::class.java)

	private val pexelsApiKey = "9uFab6m17YJ2fINGNDUKqMueSFdBllCVklHPy8L8xM0VflFSWy4XJgd7"

	override suspend fun fetchWeather(lat: Double, lon: Double): Resource<WeatherInfo> {
		return try {
			val response = weatherApi.getWeatherData(lat, lon)
			val body = response.body()

			if (response.isSuccessful && body != null) {
				Resource.Success(
					WeatherInfo(
						temperature = body.current.temperature,
						windSpeed = body.current.windSpeed,
						condition = mapWeatherCode(body.current.weatherCode),
						humidity = body.current.humidity,
						feelsLike = body.current.feelsLike,
						isDay = body.current.isDay == 1
					)
				)
			} else {
				Resource.Error("Server error code: ${response.code()}")
			}
		} catch (e: IOException) {
			Resource.Error("Network failure: ${e.localizedMessage ?: "Check connection."}")
		} catch (e: Exception) {
			Resource.Error("Parsing error: ${e.localizedMessage}")
		}
	}

	override suspend fun searchLocation(query: String): Resource<List<LocationInfo>> {
		Log.d("WeatherRepo", "Searching for: $query")
		return try {
			val response = geocodingApi.searchLocation(query)
			val body = response.body()

			if (response.isSuccessful && body != null) {
				Log.d("WeatherRepo", "Search successful: ${body.results?.size ?: 0} results")
				Resource.Success(
					body.results?.map {
						LocationInfo(
							name = it.name,
							latitude = it.latitude,
							longitude = it.longitude,
							country = it.country,
							region = it.admin1
						)
					} ?: emptyList()
				)
			} else {
				Log.e("WeatherRepo", "Search error: ${response.code()} ${response.errorBody()?.string()}")
				Resource.Error("Search error: ${response.code()}")
			}
		} catch (e: Exception) {
			Log.e("WeatherRepo", "Search failed: ${e.localizedMessage}")
			Resource.Error("Search failed: ${e.localizedMessage}")
		}
	}

	override suspend fun fetchBackgroundVideo(condition: String): Resource<String> {
		return try {
			val query = when {
				condition.contains("Thunder", ignoreCase = true) -> "thunderstorm sky"
				condition.contains("Rain", ignoreCase = true) -> "rainy day"
				condition.contains("Drizzle", ignoreCase = true) -> "light rain"
				condition.contains("Snow", ignoreCase = true) -> "snowy forest"
				condition.contains("Fog", ignoreCase = true) -> "foggy morning"
				condition.contains("Cloud", ignoreCase = true) -> "cloudy sky"
				condition.contains("Clear", ignoreCase = true) -> "sunny nature"
				else -> "blue sky"
			}

			val response = pexelsApi.searchVideos(pexelsApiKey, query)
			val body = response.body()

			if (response.isSuccessful && body != null && body.videos.isNotEmpty()) {
				val videoFile = body.videos[0].videoFiles.find { it.quality == "sd" || it.quality == "hd" }
					?: body.videos[0].videoFiles[0]
				Resource.Success(videoFile.link)
			} else {
				Resource.Error("Video not found")
			}
		} catch (e: Exception) {
			Resource.Error("Video fetch failed: ${e.localizedMessage}")
		}
	}

	private fun createUnsafeOkHttpClient(): OkHttpClient {
		return try {
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

			OkHttpClient.Builder()
				.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
				.hostnameVerifier { _, _ -> true }
				.connectTimeout(15, TimeUnit.SECONDS)
				.readTimeout(15, TimeUnit.SECONDS)
				.dns { hostname -> Dns.SYSTEM.lookup(hostname).sortedBy { it is java.net.Inet6Address } }
				.build()
		} catch (e: Exception) {
			throw RuntimeException(e)
		}
	}

	private fun mapWeatherCode(code: Int): String {
		return when (code) {
			0 -> "Clear Sky"
			1 -> "Mainly Clear"
			2 -> "Partly Cloudy"
			3 -> "Overcast"
			45, 48 -> "Foggy"
			51, 53, 55 -> "Drizzle"
			56, 57 -> "Freezing Drizzle"
			61, 63, 65 -> "Rainy"
			66, 67 -> "Freezing Rain"
			71, 73, 75 -> "Snowy"
			77 -> "Snow Grains"
			80, 81, 82 -> "Rain Showers"
			85, 86 -> "Snow Showers"
			95 -> "Thunderstorm"
			96, 99 -> "Thunderstorm with Hail"
			else -> "Unknown Weather"
		}
	}
}

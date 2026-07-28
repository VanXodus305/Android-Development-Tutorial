package com.example.myapplication.data.repository

import com.example.myapplication.data.api.GeocodingApi
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
		return try {
			val response = geocodingApi.searchLocation(query)
			val body = response.body()

			if (response.isSuccessful && body != null) {
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
				Resource.Error("Search error: ${response.code()}")
			}
		} catch (e: Exception) {
			Resource.Error("Search failed: ${e.localizedMessage}")
		}
	}

	private fun createUnsafeOkHttpClient(): OkHttpClient {
		return try {
			val trustAllCerts = arrayOf<TrustManager>(
				object : X509TrustManager {
					override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
					override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
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
			1, 2, 3 -> "Partly Cloudy"
			45, 48 -> "Foggy"
			51, 53, 55 -> "Drizzle"
			61, 63, 65 -> "Rainy"
			71, 73, 75 -> "Snowy"
			80, 81, 82 -> "Rain Showers"
			95, 96, 99 -> "Thunderstorm"
			else -> "Unknown Weather"
		}
	}
}

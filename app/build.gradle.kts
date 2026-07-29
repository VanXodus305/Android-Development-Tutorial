plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.kotlin.serialization)
	id("com.google.devtools.ksp")
}

android {
	namespace = "com.example.myapplication"
	compileSdk {
		version = release(37) {
			minorApiLevel = 1
		}
	}

	defaultConfig {
		applicationId = "com.example.myapplication"
		minSdk = 24
		targetSdk = 37
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildFeatures {
		viewBinding = true
	}

	buildTypes {
		release {
			optimization {
				enable = false
			}
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	buildFeatures {
		compose = true
	}
}

dependencies {
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.ui)
	implementation(libs.androidx.compose.ui.graphics)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.material3)
	implementation(libs.androidx.room.ktx)
	implementation(libs.work.runtime.ktx)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.compose.ui.test.junit4)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(libs.androidx.junit)
	debugImplementation(libs.androidx.compose.ui.test.manifest)
	debugImplementation(libs.androidx.compose.ui.tooling)

	implementation(libs.androidx.navigation.compose)
	implementation(libs.androidx.compose.material.icons.extended)
	implementation(libs.androidx.datastore.preferences)
	implementation(libs.androidx.room.runtime)
	ksp(libs.room.compiler)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.lifecycle.runtime.compose)

	implementation(libs.retrofit)
	implementation(libs.converter.gson)
	implementation(libs.okhttp)

	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.kotlinx.coroutines.android)

	implementation(libs.kotlinx.serialization.json)
	implementation(libs.kotlinx.serialization.converter)
	implementation(libs.google.play.services.location)
	implementation(libs.androidx.media3.exoplayer)
	implementation(libs.androidx.media3.ui)
	implementation(libs.androidx.media3.datasource.okhttp)

	implementation(libs.androidx.lifecycle.viewmodel.ktx)
}
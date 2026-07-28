package com.example.myapplication.domain.location

import android.location.Location

interface LocationTracker {
	suspend fun getCurrentLocation(): Location?
}

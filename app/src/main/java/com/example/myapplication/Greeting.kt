package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Greeting(name: String) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		Text(
			text = "Hello $name!",
			color = Color.Blue,
			fontSize = 30.sp
		)
		Text(
			text = "Welcome to Jetpack Compose",
			color = Color.Green,
			textAlign = TextAlign.Center,
			fontSize = 25.sp,
			modifier = Modifier.padding(top = 8.dp)
		)
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(top = 16.dp)
				.align(Alignment.CenterHorizontally)
		) {
			items(20) { index ->
				Box(
					modifier = Modifier
						.background(Color.LightGray)
						.padding(8.dp)
						.fillMaxWidth()
				) {
					Image(
						modifier = Modifier
							.padding(top = 16.dp)
							.background(Color.DarkGray)
							.align(Alignment.Center)
							.size(100.dp),
						painter = painterResource(id = R.drawable.ic_launcher_foreground),
						contentDescription = "App Logo"
					)
				}
			}
		}
	}
}
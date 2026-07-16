package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // greeting("Android")

                myAppNavigation()

                // var count by remember { mutableStateOf(0) }
                // Column(
                //     modifier = Modifier.fillMaxSize(),
                //     verticalArrangement = Arrangement.Center,
                //     horizontalAlignment = Alignment.CenterHorizontally
                // ) {
                //     Text(
                //         text = count.toString(),
                //         color = Color.Blue,
                //         fontSize = 30.sp
                //     )
                //     Button(
                //         onClick = { count++ },
                //         modifier = Modifier.padding(top = 16.dp),
                //         colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta)
                //     ) {
                //         Text(text = "Click Me")
                //     }
                // }
            }
        }
    }
}

@Composable
fun greeting(name: String) {
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

@Preview(showBackground = true)
@Composable
fun previewGreeting() {
    MyApplicationTheme {
        greeting("Android")
    }
}
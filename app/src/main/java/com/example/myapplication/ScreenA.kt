package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import java.lang.module.ModuleDescriptor

@Composable
fun screenA(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize() 
            .padding(16.dp)
    ) {
        Text(
            text = "This is Screen A",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = { navController.navigate(Routes.SCREEN_B + "?name=John") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Go to Screen B")
        }
    }
}
package com.example.petsync1.screens.splash

import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.petsync1.R
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.petsync1.ui.theme.PetSync1Theme

@Composable
fun AppSplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        delay(2000) // Show splash screen for 2 seconds
        navController.navigate("login") { // Navigate to lo-gin instead of home
            popUpTo("splash") { inclusive = true } // Remove splash from back stack
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splashscreen), // Use your background image
            contentDescription = "PetSync Splash Screen",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppSplashScreen() {
    PetSync1Theme(darkTheme = false) {
        AppSplashScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppSplashScreenDark() {
    PetSync1Theme(darkTheme = true) {
        AppSplashScreen(navController = rememberNavController())
    }
}

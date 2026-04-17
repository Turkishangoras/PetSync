package com.example.petsync1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.petsync1.screens.auth.LoginScreen
import com.example.petsync1.screens.auth.SignUpScreen
import com.example.petsync1.ui.theme.PetSync1Theme
import com.example.petsync1.screens.auth.ForgotPasswordScreen
import com.example.petsync1.screens.home.HomeScreen
import com.example.petsync1.screens.reminders.RemindersScreen
import com.example.petsync1.screens.healthtracker.HealthTrackerScreen
import com.example.petsync1.screens.wellnesstips.WellnessTipsScreen
import com.example.petsync1.screens.profiles.AddPetScreen
import com.example.petsync1.screens.profiles.PetProfileScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.petsync1.screens.healthtracker.AddHealthRecordScreen
import com.example.petsync1.viewmodels.PetViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.petsync1.viewmodels.ThemeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.petsync1.screens.reminders.AddReminderScreen
import com.example.petsync1.screens.settings.SettingsScreen
import com.example.petsync1.screens.settings.PrivacyPolicyScreen
import com.example.petsync1.screens.findvets.FindVetScreenContent
import com.example.petsync1.screens.splash.AppSplashScreen
import com.example.petsync1.utils.requestExactAlarmPermission

/**
 * The main activity of the application, responsible for setting up the UI and navigation.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize the splash screen before the activity is created
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Initialize Firebase services for the application
        FirebaseApp.initializeApp(this)

        // Request exact alarm permission for Android 12+ to ensure timely notifications
        requestExactAlarmPermission(this)

        setContent {
            // Provide ThemeViewModel to manage app-wide dark mode state
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            // Apply the custom PetSync1 theme to the entire application
            PetSync1Theme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                // Set up the central navigation graph for the app
                NavigationGraph(navController, themeViewModel)
            }
        }
    }
}


/**
 * Defines all the navigation routes and their corresponding screens.
 */
@Composable
fun NavigationGraph(navController: NavHostController, themeViewModel: ThemeViewModel) {
    // Shared PetViewModel instance to maintain state across profile-related screens
    val petViewModel: PetViewModel = viewModel()
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    NavHost(navController = navController, startDestination = "splash") {
        // Auth and Splash routes
        composable("splash") { AppSplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }

        // Main feature routes
        composable("home") { HomeScreen(navController, isDarkMode = isDarkMode) }
        composable("pet_profile") { PetProfileScreen(navController, petViewModel, isDarkMode = isDarkMode) }
        composable("add_pet") { AddPetScreen(navController, isDarkMode = isDarkMode) }
        composable("reminders") { RemindersScreen(navController, isDarkMode = isDarkMode) }
        composable("add_reminder_screen") { AddReminderScreen(navController, isDarkMode = isDarkMode) }
        composable("wellness_tips") { WellnessTipsScreen(navController, isDarkMode = isDarkMode) }
        composable("health_tracker") { HealthTrackerScreen(navController, isDarkMode = isDarkMode) }
        composable("find_vet") { FindVetScreenContent(navController, isDarkMode = isDarkMode) }
        composable("add_health_record_screen") { AddHealthRecordScreen(navController, isDarkMode = isDarkMode) }

        // Settings and policy routes
        composable("settings") {
            val context = LocalContext.current
            SettingsScreen(
                navController = navController,
                isDarkMode = isDarkMode,
                onDarkModeChange = { themeViewModel.toggleDarkMode(it) },
                onSignOut = {
                    // Handle user sign out through Firebase and navigate back to log-in
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("privacy_policy") { PrivacyPolicyScreen(navController) }
    }
}

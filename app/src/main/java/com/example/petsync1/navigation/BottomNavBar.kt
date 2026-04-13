package com.example.petsync1.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.petsync1.R
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.petsync1.ui.theme.PetSync1Theme

@Composable
fun BottomNavBar(navController: NavController?, currentRoute: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(navController, R.drawable.homenavb, "Home", "home", currentRoute)
            BottomNavItem(navController, R.drawable.heakthtrackernavb, "Health", "health_tracker", currentRoute)
            BottomNavItem(navController, R.drawable.remindernavb, "Reminders", "reminders", currentRoute)
            BottomNavItem(navController, R.drawable.findvetnavb, "Find Vet", "find_vet", currentRoute)
            BottomNavItem(navController, R.drawable.settingsnavb, "Settings", "settings", currentRoute)
        }
    }
}

@Composable
fun BottomNavItem(
    navController: NavController?,
    iconRes: Int,
    label: String,
    route: String,
    currentRoute: String
) {
    val isSelected = currentRoute == route
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                if (navController != null && !isSelected) {
                    navController.navigate(route) {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }
            .padding(horizontal = 4.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = contentColor
        )
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    PetSync1Theme {
        BottomNavBar(navController = null, currentRoute = "home")
    }
}

@Preview
@Composable
fun BottomNavBarDarkPreview() {
    PetSync1Theme(darkTheme = true) {
        BottomNavBar(navController = null, currentRoute = "home")
    }
}

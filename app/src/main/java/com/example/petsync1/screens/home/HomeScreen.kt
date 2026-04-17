package com.example.petsync1.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.petsync1.R
import com.example.petsync1.navigation.BottomNavBar

@Composable
fun HomeScreen(navController: NavController, isDarkMode: Boolean = false) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = "home"
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Full-screen background image with subtle overlay
            Image(
                painter = painterResource(id = R.drawable.allscreensbackg),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.4f,
                colorFilter = if (isDarkMode) ColorFilter.tint(Color.Black.copy(alpha = 0.4f), BlendMode.Darken) else null
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // --- Header Section ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Welcome to",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "PetSync",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.petsynclogot1),
                        contentDescription = "PetSync Logo",
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(0.dp))

                // --- Dashboard Section (The "Green Box") ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp), 
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 30.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 20.dp), // Increased vertical padding
                        verticalArrangement = Arrangement.spacedBy(36.dp), // Increased row spacing
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // First Row: Profile and Add Pet (Large Icons)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                HomeDashboardIcon(
                                    resId = R.drawable.new_petprofile_logo,
                                    label = "Profile",
                                    iconSize = 220.dp, // Slightly larger
                                    onClick = { navController.navigate("pet_profile") }
                                )
                            }
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                HomeDashboardIcon(
                                    resId = R.drawable.new_addpet_logo,
                                    label = "Add Pet",
                                    iconSize = 220.dp, // Slightly larger
                                    onClick = { navController.navigate("add_pet") }
                                )
                            }
                        }

                        // Second Row: Reminders, Find Vet, Settings (Medium Icons)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                HomeDashboardIcon(
                                    resId = R.drawable.new_reminders_logo,
                                    label = "Reminders",
                                    iconSize = 150.dp, // Larger
                                    onClick = { navController.navigate("reminders") }
                                )
                            }
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                HomeDashboardIcon(
                                    resId = R.drawable.new_findvet_logo,
                                    label = "Find Vet",
                                    iconSize = 150.dp, // Larger
                                    onClick = { navController.navigate("find_vet") }
                                )
                            }
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                HomeDashboardIcon(
                                    resId = R.drawable.new_settings_logo,
                                    label = "Settings",
                                    iconSize = 150.dp, // Larger
                                    onClick = { navController.navigate("settings") }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp)) // Increased spacer to push Wellness Tips down further

                // --- Wellness Tips Section ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp) // Added more bottom padding
                        .clickable { navController.navigate("wellness_tips") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp) // Slightly more internal padding
                    ) {
                        Text(
                            text = "Wellness Tips",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "To be a successful cat parent, you need the right gear. Many of us think of no-brainer like food and water right away, but some things are more subtle, like nice, flat, wide bowls for that food and water.",
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Text(
                            text = "Tap to read more...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable component for Dashboard Icons (Image only).
 */
@Composable
private fun HomeDashboardIcon(
    resId: Int,
    label: String,
    iconSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(0.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = label,
            modifier = Modifier.size(iconSize),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420", showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    com.example.petsync1.ui.theme.PetSync1Theme {
        HomeScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420", showSystemUi = true)
@Composable
fun PreviewHomeScreenDark() {
    com.example.petsync1.ui.theme.PetSync1Theme(darkTheme = true) {
        HomeScreen(navController = rememberNavController(), isDarkMode = true)
    }
}

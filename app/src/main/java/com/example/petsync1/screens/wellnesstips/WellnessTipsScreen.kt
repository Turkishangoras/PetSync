package com.example.petsync1.screens.wellnesstips

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.petsync1.R
import com.example.petsync1.navigation.BottomNavBar
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import com.example.petsync1.ui.theme.PetSync1Theme


@Composable
fun WellnessTipsScreen(navController: NavHostController, isDarkMode: Boolean = false) {
    var selectedCategory by remember { mutableStateOf("Diet") }

    val wellnessTips = mapOf(
        "Diet" to listOf(
            "Provide a balanced diet with high-quality protein sources.",
            "Ensure your cat always has access to fresh, clean water.",
            "Avoid feeding your cat dog food or human junk food.",
            "Limit treats to no more than 10% of daily calorie intake.",
            "Feed your cat based on their age, weight, and health needs.",
            "Incorporate wet food to help with hydration.",
            "Use slow-feeder bowls to prevent rapid eating.",
            "Consult your vet before changing your cat’s diet."
        ),
        "Hygiene" to listOf(
            "Brush your cat regularly to reduce shedding and hairballs.",
            "Clean the litter box daily to prevent infections.",
            "Trim your cat’s nails every few weeks.",
            "Check and clean your cat’s ears regularly.",
            "Brush your cat’s teeth to prevent dental issues.",
            "Wipe tear stains gently with a soft, damp cloth.",
            "Bathe your cat occasionally if necessary using cat-safe shampoo.",
            "Keep bedding and toys clean and sanitized."
        ),
        "Exercise" to listOf(
            "Engage your cat with daily interactive play sessions.",
            "Use toys like laser pointers and feather wands for fun workouts.",
            "Provide climbing trees and scratching posts for physical activity.",
            "Rotate toys weekly to keep your cat interested and active.",
            "Encourage movement with puzzle feeders or treat-dispensing toys.",
            "Set up a safe outdoor enclosure for supervised exploration.",
            "Play hide and seek using treats or toys.",
            "Allow access to windows for visual stimulation and jumping."
        ),
        "Mental Stimulation" to listOf(
            "Teach your cat simple tricks using treats and clicker training.",
            "Place bird feeders outside windows to keep your cat entertained.",
            "Hide treats around the house to encourage foraging behavior.",
            "Use interactive puzzle toys to challenge their mind.",
            "Change the layout of furniture or play spaces for novelty.",
            "Introduce safe, new scents like catnip or silvervine.",
            "Provide a variety of textures and materials to explore.",
            "Schedule regular short training sessions to keep them sharp."
        ),
        "Grooming" to listOf(
            "Brush long-haired cats daily to prevent matting.",
            "Check their fur regularly for ticks or fleas.",
            "Use grooming gloves for gentle brushing and bonding.",
            "Clean tear stains gently with a soft cloth.",
            "Trim fur around paws to keep it neat and clean.",
            "Use a flea comb during grooming sessions.",
            "Make grooming a calm and rewarding experience.",
            "Introduce grooming early to make it a positive routine."
        )
    )

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController, currentRoute = "Wellness_Tips_Screen")
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.allscreensbackg),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = if (isDarkMode) ColorFilter.tint(Color.Black.copy(alpha = 0.4f), BlendMode.Darken) else null
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    text = "Wellness Tips",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Category Selection Buttons
                val categories = wellnessTips.keys.toList()
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    categories.chunked(2).forEach { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowCategories.forEach { category ->
                                Button(
                                    onClick = { selectedCategory = category },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedCategory == category) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (selectedCategory == category) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = category,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display Tips in themed container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f), shape = RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    val tips = wellnessTips[selectedCategory] ?: emptyList()

                    if (tips.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No tips available",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            tips.forEach { tip ->
                                Text(
                                    text = "• $tip",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWellnessTipsScreen() {
    PetSync1Theme {
        WellnessTipsScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWellnessTipsScreenDark() {
    PetSync1Theme(darkTheme = true) {
        WellnessTipsScreen(navController = rememberNavController(), isDarkMode = true)
    }
}

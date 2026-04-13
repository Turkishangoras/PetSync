package com.example.petsync1.screens.profiles

import androidx.core.net.toUri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.petsync1.R
import com.example.petsync1.navigation.BottomNavBar
import com.example.petsync1.viewmodels.Pet
import com.example.petsync1.viewmodels.PetViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.petsync1.ui.theme.PetSync1Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetProfileScreen(navController: NavHostController, petViewModel: PetViewModel, isDarkMode: Boolean = false) {
    val coroutineScope = rememberCoroutineScope()

    var pets by remember { mutableStateOf<List<Pet>>(emptyList()) }
    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var healthExpanded by remember { mutableStateOf(false) }
    val healthStatusOptions = listOf("Healthy", "Needs Attention", "Under Treatment")

    val isPreview = LocalInspectionMode.current
    val ownerId = if (isPreview) "" else FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(ownerId) {
        if (ownerId.isNotEmpty() && !isPreview) {
            coroutineScope.launch {
                petViewModel.getAllPetsForUser(ownerId) { petList ->
                    pets = petList
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController, currentRoute = "profile_screen")
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // Remove any automatic insets
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Custom Background Image
            Image(
                painter = painterResource(id = R.drawable.allscreensbackg),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.3f,
                colorFilter = if (isDarkMode) ColorFilter.tint(Color.Black.copy(alpha = 0.4f), BlendMode.Darken) else null
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Pet Profile",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box {
                    Button(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .width(200.dp)
                            .height(50.dp),
                    ) {
                        Text(
                            text = selectedPet?.name ?: "Select a Pet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        pets.forEach { pet ->
                            DropdownMenuItem(
                                text = { Text(pet.name) },
                                onClick = {
                                    selectedPet = pet
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (selectedPet == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No pet selected. Choose a pet to display profile",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = if (selectedPet!!.imageUrl.isNotEmpty()) {
                                    rememberAsyncImagePainter(selectedPet!!.imageUrl.toUri())
                                } else {
                                    painterResource(id = R.drawable.petprofilepic)
                                },
                                contentDescription = "Pet Profile Picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                listOf(
                                    "Breed: ${selectedPet!!.breed}",
                                    "Age: ${selectedPet!!.age} years",
                                    "Weight: ${selectedPet!!.weight} kg",
                                ).forEach { text ->
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                    ) {
                                        Text(
                                            text = text,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(12.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                // Interactive Health Status
                                Box {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                        onClick = { healthExpanded = true }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Health: ${selectedPet!!.healthStatus}",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Change Health Status",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = healthExpanded,
                                        onDismissRequest = { healthExpanded = false }
                                    ) {
                                        healthStatusOptions.forEach { status ->
                                            DropdownMenuItem(
                                                text = { Text(status) },
                                                onClick = {
                                                    val petId = selectedPet?.id ?: ""
                                                    if (petId.isNotEmpty()) {
                                                        petViewModel.updatePetHealthStatus(petId, status, {
                                                            // Update local state to reflect change immediately
                                                            selectedPet = selectedPet?.copy(healthStatus = status)
                                                            healthExpanded = false
                                                        }, {
                                                            healthExpanded = false
                                                        })
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { navController.navigate("health_tracker") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "View Health Records",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
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
fun PreviewPetProfileScreen() {
    PetSync1Theme(darkTheme = false) {
        PetProfileScreen(
            navController = rememberNavController(),
            petViewModel = PetViewModel(),
            isDarkMode = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPetProfileScreenDark() {
    PetSync1Theme(darkTheme = true) {
        PetProfileScreen(
            navController = rememberNavController(),
            petViewModel = PetViewModel(),
            isDarkMode = true
        )
    }
}

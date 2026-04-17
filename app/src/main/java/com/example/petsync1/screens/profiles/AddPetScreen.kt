package com.example.petsync1.screens.profiles

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.petsync1.R
import com.example.petsync1.navigation.BottomNavBar
import com.example.petsync1.viewmodels.Pet
import com.example.petsync1.viewmodels.PetViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.petsync1.ui.theme.PetSync1Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(navController: NavHostController, petViewModel: PetViewModel = viewModel(), isDarkMode: Boolean = false) {
    AddPetContent(
        navController = navController,
        isDarkMode = isDarkMode,
        onAddPet = { petName, age, breed, weight, gender, healthStatus, petImageUri, onSuccess, onFailure ->
            val user = FirebaseAuth.getInstance().currentUser
            val ownerId = user?.uid ?: ""

            if (ownerId.isEmpty()) {
                onFailure(Exception("User not logged in."))
                return@AddPetContent
            }

            val newPet = Pet(
                name = petName,
                age = age,
                breed = breed,
                weight = weight,
                gender = gender,
                healthStatus = healthStatus,
                ownerId = ownerId
            )

            petViewModel.addPetWithImage(
                newPet,
                petImageUri,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetContent(
    navController: NavHostController,
    isDarkMode: Boolean = false,
    onAddPet: (String, String, String, String, String, String, Uri?, () -> Unit, (Exception) -> Unit) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = "Add_Pet_Screen"
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // Remove any automatic insets

    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            var petName by remember { mutableStateOf(TextFieldValue("")) }
            var breed by remember { mutableStateOf(TextFieldValue("")) }
            var age by remember { mutableStateOf(TextFieldValue("")) }
            var weight by remember { mutableStateOf(TextFieldValue("")) }
            var gender by remember { mutableStateOf("Male") }
            var genderExpanded by remember { mutableStateOf(false) }
            val genderOptions = listOf("Male", "Female")
            var healthStatus by remember { mutableStateOf("Healthy") }
            var healthStatusExpanded by remember { mutableStateOf(false) }
            val healthStatusOptions = listOf("Healthy", "Needs Attention", "Under Treatment")
            var errorMessage by remember { mutableStateOf("") }
            var petImageUri by remember { mutableStateOf<Uri?>(null) }
            var isUploading by remember { mutableStateOf(false) }

            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                petImageUri = uri
            }

            Box(modifier = Modifier.fillMaxSize()) {
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Add New Pet",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") }
                    ) {
                        if (petImageUri != null) {
                            AsyncImage(
                                model = petImageUri,
                                contentDescription = "Pet Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.petprofilepic),
                                contentDescription = "Upload Pet Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
                        TextFieldLabel("PET NAME")
                        CustomTextField(petName, { petName = it }, "Enter Pet's Name")

                        TextFieldLabel("BREED")
                        CustomTextField(breed, { breed = it }, "Enter Breed")

                        TextFieldLabel("AGE (years)")
                        CustomTextField(age, { input ->
                            if (input.text.isEmpty() || input.text.all { it.isDigit() }) age = input
                        }, "Enter Age")

                        TextFieldLabel("WEIGHT (kg)")
                        CustomTextField(weight, { input ->
                            if (input.text.isEmpty() || (input.text.count { it == '.' } <= 1 && input.text.all { it.isDigit() || it == '.' })) {
                                weight = input
                            }
                        }, "Enter Weight")

                        TextFieldLabel("GENDER")
                        ExposedDropdownMenuBox(
                            expanded = genderExpanded,
                            onExpandedChange = { genderExpanded = !genderExpanded },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            TextField(
                                value = gender,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp)),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = genderExpanded,
                                onDismissRequest = { genderExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                genderOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            gender = option
                                            genderExpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }

                        TextFieldLabel("HEALTH STATUS")
                        ExposedDropdownMenuBox(
                            expanded = healthStatusExpanded,
                            onExpandedChange = { healthStatusExpanded = !healthStatusExpanded },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            TextField(
                                value = healthStatus,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp)),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = healthStatusExpanded)
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = healthStatusExpanded,
                                onDismissRequest = { healthStatusExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                healthStatusOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            healthStatus = option
                                            healthStatusExpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            errorMessage = ""
                            when {
                                petName.text.isBlank() || breed.text.isBlank() || age.text.isBlank() || weight.text.isBlank() -> {
                                    errorMessage = "All fields are required."
                                }

                                age.text.toIntOrNull() == null || age.text.toInt() <= 0 -> {
                                    errorMessage = "Age must be a positive number."
                                }

                                weight.text.toFloatOrNull() == null || weight.text.toFloat() <= 0 -> {
                                    errorMessage = "Weight must be a positive number."
                                }

                                else -> {
                                    isUploading = true
                                    onAddPet(
                                        petName.text,
                                        age.text,
                                        breed.text,
                                        weight.text,
                                        gender,
                                        healthStatus,
                                        petImageUri,
                                        {
                                            Toast.makeText(context, "Pet added successfully!", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                            isUploading = false
                                        },
                                        { error ->
                                            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                            isUploading = false
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .width(200.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isUploading
                    ) {
                        Text("Add Pet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun TextFieldLabel(label: String) {
    Text(
        text = label,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 12.dp)
    )
}

@Composable
fun CustomTextField(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit, placeholder: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp)),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        ),
        placeholder = { Text(placeholder) }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAddPetScreen() {
    PetSync1Theme(darkTheme = false) {
        AddPetContent(
            navController = rememberNavController(),
            isDarkMode = false,
            onAddPet = { _, _, _, _, _, _, _, _, _ -> })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddPetScreenDark() {
    PetSync1Theme(darkTheme = true) {
        AddPetContent(
            navController = rememberNavController(),
            isDarkMode = true,
            onAddPet = { _, _, _, _, _, _, _, _, _ -> })
    }
}

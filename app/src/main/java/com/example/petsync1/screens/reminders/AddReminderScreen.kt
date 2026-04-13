package com.example.petsync1.screens.reminders

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.petsync1.R
import com.example.petsync1.models.Reminder
import com.example.petsync1.navigation.BottomNavBar
import com.example.petsync1.viewmodels.PetViewModel
import com.example.petsync1.viewmodels.ReminderViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import com.example.petsync1.ui.theme.PetSync1Theme

@Composable
fun AddReminderScreen(
    navController: NavHostController,
    reminderViewModel: ReminderViewModel = viewModel(),
    petViewModel: PetViewModel = viewModel(),
    isDarkMode: Boolean = false
) {
    val context = LocalContext.current
    val pets by petViewModel.petList.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            petViewModel.getAllPetsForUser(userId) { }
        }
    }

    AddReminderContent(
        navController = navController,
        pets = pets,
        onAddReminder = { reminder ->
            reminderViewModel.addStandaloneReminder(context, reminder)
        },
        isDarkMode = isDarkMode
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderContent(
    navController: NavHostController,
    pets: List<com.example.petsync1.viewmodels.Pet>,
    onAddReminder: (Reminder) -> Unit,
    isDarkMode: Boolean = false
) {
    var selectedPetId by remember { mutableStateOf("") }
    var selectedPetName by remember { mutableStateOf("") }
    var petExpanded by remember { mutableStateOf(false) }

    var reminderType by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var dueDate by remember { mutableStateOf("") }
    var perDay by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("General") }
    val context = LocalContext.current

    val generalTypes = listOf("Vaccination", "Deworming", "Vet Visit", "Grooming", "Flea/Tick Treatment", "Other")
    val medicationTypes = listOf("Antibiotics", "Pain Relief", "Insulin", "Supplements", "Heartworm", "Other")

    LaunchedEffect(selectedCategory) {
        reminderType = ""
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, currentRoute = "add_reminder_screen") },
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
                colorFilter = if (isDarkMode) ColorFilter.tint(
                    Color.Black.copy(alpha = 0.4f),
                    BlendMode.Darken
                ) else null
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    text = "Add Reminder",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(20.dp))

                val categories = listOf("General", "Medications")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    categories.forEach { category ->
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

                Spacer(modifier = Modifier.height(16.dp))

                // Pet Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = petExpanded,
                    onExpandedChange = { petExpanded = !petExpanded },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    TextField(
                        value = selectedPetName,
                        onValueChange = {},
                        label = { Text("Select Pet") },
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
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = petExpanded)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = petExpanded,
                        onDismissRequest = { petExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        pets.forEach { pet ->
                            DropdownMenuItem(
                                text = { Text(pet.name) },
                                onClick = {
                                    selectedPetName = pet.name
                                    selectedPetId = pet.id
                                    petExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                        if (pets.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No pets found. Add one first!") },
                                onClick = { petExpanded = false },
                                enabled = false
                            )
                        }
                    }
                }

                // Type Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    TextField(
                        value = reminderType,
                        onValueChange = {},
                        label = { Text("Reminder Type") },
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
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        val currentTypes = if (selectedCategory == "General") generalTypes else medicationTypes
                        currentTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    reminderType = type
                                    typeExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                if (selectedCategory == "General") {
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerField(label = "Due Date", selectedDate = dueDate, onDateSelected = { dueDate = it }, context = LocalContext.current)
                } else if (selectedCategory == "Medications") {
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomTextField(
                        value = perDay,
                        onValueChange = { input ->
                            val filteredInput = input.filter { it.isDigit() }
                            val intValue = filteredInput.toIntOrNull()
                            perDay = when {
                                intValue == null -> ""
                                intValue in 1..10 -> filteredInput
                                intValue > 10 -> "10"
                                else -> "1"
                            }
                        },
                        label = "Per Day"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerField(label = "End Date", selectedDate = endDate, onDateSelected = { endDate = it }, context = LocalContext.current)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        errorMessage = ""
                        when {
                            selectedPetId.isEmpty() -> errorMessage = "Please select a pet."
                            reminderType.isBlank() -> errorMessage = "Please select a reminder type."
                            selectedCategory == "General" && dueDate.isBlank() -> errorMessage = "Due date is required."
                            selectedCategory == "Medications" && (perDay.isBlank() || endDate.isBlank()) -> errorMessage = "All medication fields are required."
                            else -> {
                                isSaving = true
                                val reminder = Reminder(
                                    petId = selectedPetId,
                                    petName = selectedPetName,
                                    type = reminderType,
                                    dueDate = if (selectedCategory == "General") dueDate else "",
                                    perDay = if (selectedCategory == "Medications") perDay else "",
                                    endDate = if (selectedCategory == "Medications") endDate else "",
                                    category = selectedCategory,
                                    ownerId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                )

                                onAddReminder(reminder)
                                Toast.makeText(context, "Reminder added successfully!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier.width(200.dp).height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving
                ) {
                    Text("Save Reminder", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(RoundedCornerShape(24.dp)),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        label = { Text(label) }
    )
}

@Composable
fun DatePickerField(label: String, selectedDate: String, onDateSelected: (String) -> Unit, context: Context) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = "${month + 1}/$dayOfMonth/$year"
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = selectedDate,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select Date",
                    modifier = Modifier.clickable { datePickerDialog.show() }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() }
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp)),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddReminderScreen() {
    PetSync1Theme {
        AddReminderContent(
            navController = rememberNavController(),
            pets = listOf(com.example.petsync1.viewmodels.Pet(id = "1", name = "Buddy")),
            onAddReminder = {}
        )
    }
}

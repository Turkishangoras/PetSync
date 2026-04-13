package com.example.petsync1.screens.healthtracker

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import android.app.DatePickerDialog
import android.content.Context
import java.util.Calendar
import android.app.TimePickerDialog
import java.util.Locale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import com.example.petsync1.R
import com.example.petsync1.navigation.BottomNavBar
import com.example.petsync1.viewmodels.HealthRecord
import com.example.petsync1.viewmodels.HealthRecordViewModel
import com.example.petsync1.viewmodels.PetViewModel
import com.example.petsync1.viewmodels.Pet
import com.google.firebase.auth.FirebaseAuth
import com.example.petsync1.ui.theme.PetSync1Theme

@Composable
fun AddHealthRecordScreen(
    navController: NavHostController,
    healthRecordViewModel: HealthRecordViewModel = viewModel(),
    petViewModel: PetViewModel = viewModel(),
    isDarkMode: Boolean = false
) {
    val petList by petViewModel.petList.collectAsState()

    // Ensure pets are loaded
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.uid?.let { userId ->
            petViewModel.getAllPetsForUser(userId) {}
        }
    }

    AddHealthRecordContent(
        navController = navController,
        petList = petList,
        onSaveRecord = { petId, _, healthRecord ->
            healthRecordViewModel.addHealthRecord(petId, healthRecord)
        },
        isDarkMode = isDarkMode
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHealthRecordContent(
    navController: NavHostController,
    petList: List<Pet>,
    onSaveRecord: (String, String, HealthRecord) -> Unit,
    isDarkMode: Boolean = false
) {
    var selectedPetId by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf("Vaccinations") }
    var date by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var perDay by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var nextDueDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var isDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, currentRoute = "add_health_record_screen") },
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
                    text = "Add Health Record",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Select Pet Dropdown
                Box {
                    Button(
                        onClick = { isDropdownExpanded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .width(150.dp)
                            .height(50.dp)
                    ) {
                        Text(
                            text = petList.find { it.id == selectedPetId }?.name ?: "Select Pet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        petList.forEach { pet ->
                            DropdownMenuItem(
                                text = { Text(text = pet.name) },
                                onClick = {
                                    selectedPetId = pet.id
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selection Buttons
                val categories = listOf("Vaccinations", "Deworming", "Vet Visits", "Medications")
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Split categories into two rows
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

                DatePickerField(label = "Date", selectedDate = date, onDateSelected = { date = it }, context = LocalContext.current)

                Spacer(modifier = Modifier.height(8.dp))

                // Type selection field (now a dropdown)
                TypeSelectionField(
                    category = selectedCategory,
                    selectedType = type,
                    onTypeSelected = { type = it }
                )

                // Fields for Medications category
                if (selectedCategory == "Medications") {
                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTimePickerField(
                        label = "Start Time",
                        selectedTime = startTime,
                        onTimeSelected = { startTime = it },
                        context = LocalContext.current
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = perDay,
                        onValueChange = { input ->
                            // Allow only digits and restrict values between 1 and 10
                            val filteredInput = input.filter { it.isDigit() }
                            val intValue = filteredInput.toIntOrNull()

                            perDay = when {
                                filteredInput.isEmpty() -> ""
                                intValue == null -> "" // Clear input if invalid
                                intValue in 1..10 -> filteredInput // Allow values between 1-10
                                intValue > 10 -> "10" // Limit max to 10
                                else -> "1" // Default to 1 if below range
                            }
                        },
                        label = "Per Day",
                    )


                    Spacer(modifier = Modifier.height(8.dp))

                    DatePickerField(label = "End Date", selectedDate = endDate, onDateSelected = { endDate = it }, context = LocalContext.current)

                }

                // Show Next Due Date for categories other than Medications
                if (selectedCategory != "Medications") {
                    Spacer(modifier = Modifier.height(16.dp))

                    DatePickerField(
                        label = "Next Due Date",
                        selectedDate = nextDueDate,
                        onDateSelected = { nextDueDate = it },
                        context = LocalContext.current
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes (Optional)",
                    singleLine = false,
                    maxLines = 3
                )


                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Button(
                    onClick = {
                        errorMessage = ""

                        when {
                            selectedPetId.isNullOrEmpty() -> {
                                errorMessage = "Please select a pet."
                            }
                            date.isBlank() || type.isBlank() ||
                                    (selectedCategory != "Medications" && nextDueDate.isBlank()) ||
                                    (selectedCategory == "Medications" && (startTime.isBlank() || perDay.isBlank())) -> {
                                errorMessage = "All fields are required."
                            }
                            else -> {
                                isSaving = true

                                selectedPetId?.let { petId ->
                                    val healthRecord = HealthRecord(
                                        category = selectedCategory,
                                        date = date,
                                        type = type,
                                        nextDueDate = if (selectedCategory == "Medications") "" else nextDueDate,
                                        startTime = if (selectedCategory == "Medications") startTime else "",
                                        perDay = if (selectedCategory == "Medications") perDay else "",
                                        endDate = if (selectedCategory == "Medications") endDate else "",
                                        notes = notes
                                    )
                                    onSaveRecord(petId, selectedCategory, healthRecord)

                                    Toast.makeText(context, "Health record added successfully!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }

                                isSaving = false
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
                    enabled = !isSaving
                ) {
                    Text("Save Record", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                // Show error message if any
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeSelectionField(category: String, selectedType: String, onTypeSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = when (category) {
        "Vaccinations" -> listOf("Rabies", "DHPP", "Bordetella", "Leptospirosis", "FVRCP", "FeLV", "Other")
        "Deworming" -> listOf("Heartworm", "Roundworm", "Tapeworm", "Hookworm", "Whipworm", "General Dewormer")
        "Vet Visits" -> listOf("Annual Checkup", "Emergency", "Follow-up", "Surgery", "Dental Cleaning", "Consultation")
        "Medications" -> listOf("Antibiotics", "Pain Relief", "Supplements", "Eye Drops", "Ear Medication", "Other")
        else -> listOf("General", "Other")
    }

    // Reset selected type if it's not in the new category's options
    LaunchedEffect(category) {
        if (selectedType.isNotEmpty() && !options.contains(selectedType)) {
            onTypeSelected("")
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        TextField(
            value = selectedType,
            onValueChange = { onTypeSelected(it) },
            readOnly = false, // Allow typing if "Other" is selected or user wants to specify
            label = { Text("Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.menuAnchor().fillMaxWidth().clip(RoundedCornerShape(24.dp))
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onTypeSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
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
            readOnly = true, // Prevent manual input
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select Date",
                    modifier = Modifier.clickable { datePickerDialog.show() } // Handle click properly
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() } // Ensure DatePicker shows
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp)),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(RoundedCornerShape(24.dp)),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        label = { Text(label) },
        singleLine = singleLine,
        maxLines = maxLines
    )
}

@Composable
fun CustomTimePickerField(label: String, selectedTime: String, onTimeSelected: (String) -> Unit, context: Context) {
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            onTimeSelected(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = selectedTime,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true, // Prevent manual input
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange, // You can replace this with a clock icon
                    contentDescription = "Select Time",
                    modifier = Modifier.clickable { timePickerDialog.show() }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { timePickerDialog.show() }
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp)),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddHealthTrackerScreen() {
    PetSync1Theme(darkTheme = false) {
        AddHealthRecordContent(
            navController = rememberNavController(),
            petList = emptyList(),
            onSaveRecord = { _, _, _ -> },
            isDarkMode = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddHealthTrackerScreenDark() {
    PetSync1Theme(darkTheme = true) {
        AddHealthRecordContent(
            navController = rememberNavController(),
            petList = emptyList(),
            onSaveRecord = { _, _, _ -> },
            isDarkMode = true
        )
    }
}

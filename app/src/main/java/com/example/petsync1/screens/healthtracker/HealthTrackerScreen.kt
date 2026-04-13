package com.example.petsync1.screens.healthtracker

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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CheckCircle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.petsync1.R
import com.example.petsync1.navigation.BottomNavBar
import com.example.petsync1.viewmodels.HealthRecord
import com.example.petsync1.viewmodels.HealthRecordViewModel
import com.example.petsync1.viewmodels.Pet
import com.example.petsync1.viewmodels.PetViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTrackerScreen(
    navController: NavHostController,
    healthRecordViewModel: HealthRecordViewModel = viewModel(),
    petViewModel: PetViewModel = viewModel(),
    isDarkMode: Boolean = false
) {
    val petList by petViewModel.petList.collectAsState()
    val healthRecords by healthRecordViewModel.healthRecords.collectAsState()

    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            petViewModel.getAllPetsForUser(userId) {}
        }
    }

    HealthTrackerContent(
        navController = navController,
        petList = petList,
        healthRecords = healthRecords,
        onPetSelected = { petId -> healthRecordViewModel.fetchHealthRecords(petId) },
        onDeleteRecord = { petId, _, recordId -> healthRecordViewModel.deleteHealthRecord(petId, recordId) },
        onDeleteRecords = { petId, _, recordIds -> healthRecordViewModel.deleteHealthRecords(petId, recordIds) },
        onAddRecordClick = { navController.navigate("add_health_record_screen") },
        isDarkMode = isDarkMode
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTrackerContent(
    navController: NavHostController,
    petList: List<Pet>,
    healthRecords: Map<String, List<HealthRecord>>,
    onPetSelected: (String) -> Unit,
    onDeleteRecord: (String, String, String) -> Unit,
    onDeleteRecords: (String, String, Set<String>) -> Unit,
    onAddRecordClick: () -> Unit,
    isDarkMode: Boolean = false
) {
    var selectedPetId by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf("Vaccinations") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    var selectedRecordIds by remember { mutableStateOf(setOf<String>()) }
    val showDeleteBatchDialog = remember { mutableStateOf(false) }

    LaunchedEffect(petList) {
        if (selectedPetId == null && petList.isNotEmpty()) {
            selectedPetId = petList.first().id
        }
    }

    // Reset selection when pet or category changes
    LaunchedEffect(selectedPetId, selectedCategory) {
        selectedRecordIds = emptySet()
    }

    // Auto-fetch records when the pet is selected
    LaunchedEffect(selectedPetId) {
        selectedPetId?.let { onPetSelected(it) }
    }

    if (showDeleteBatchDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteBatchDialog.value = false },
            title = { Text("Delete Records") },
            text = { Text("Are you sure you want to delete ${selectedRecordIds.size} selected records?") },
            confirmButton = {
                TextButton(onClick = {
                    selectedPetId?.let { petId ->
                        onDeleteRecords(petId, selectedCategory, selectedRecordIds)
                    }
                    selectedRecordIds = emptySet()
                    showDeleteBatchDialog.value = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteBatchDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }


    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController, currentRoute = "health_tracker_screen")
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
                colorFilter = if (isDarkMode) ColorFilter.tint(
                    Color.Black.copy(alpha = 0.4f),
                    BlendMode.Darken
                ) else null
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    text = "Health Record",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Pet Selection Dropdown
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
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedCategory == category) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (selectedCategory == category) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(12.dp),
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

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedRecordIds.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedRecordIds.size} selected",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row {
                            TextButton(onClick = { selectedRecordIds = emptySet() }) {
                                Text("Clear", color = MaterialTheme.colorScheme.outline)
                            }
                            Button(
                                onClick = { showDeleteBatchDialog.value = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Delete", color = MaterialTheme.colorScheme.onError)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Display Health Records in themed container
                selectedPetId?.let { petId ->
                    val records = healthRecords[selectedCategory] ?: emptyList()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1.0f) 
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f), shape = RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (records.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No records found",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
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
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                records.forEachIndexed { index, record ->
                                    HealthRecordItem(
                                        index = index,
                                        record = record,
                                        category = selectedCategory,
                                        isSelected = selectedRecordIds.contains(record.id),
                                        onToggleSelection = {
                                            selectedRecordIds = if (selectedRecordIds.contains(record.id)) {
                                                selectedRecordIds - record.id
                                            } else {
                                                selectedRecordIds + record.id
                                            }
                                        },
                                        onDelete = {
                                            onDeleteRecord(petId, selectedCategory, record.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // **Add Record Button**
                Button(
                    onClick = { onAddRecordClick() },
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add Record", color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun HealthRecordItem(
    index: Int,
    record: HealthRecord,
    category: String,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onDelete: () -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Delete Record") },
            text = { Text("Are you sure you want to delete this health record?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDialog.value = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .clickable { onToggleSelection() }
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text(
                        text = "Record ${index + 1}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { showDialog.value = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            HealthInfoRow(label = "Type", value = record.type)
            HealthInfoRow(label = "Date", value = record.date)

            if (category == "Medications") {
                HealthInfoRow(label = "Start Time", value = record.startTime ?: "")
                HealthInfoRow(label = "Doses/Day", value = record.perDay ?: "")
                HealthInfoRow(label = "End Date", value = record.endDate ?: "")
            } else {
                HealthInfoRow(label = "Next Due", value = record.nextDueDate)
            }

            if (record.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: ${record.notes}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun HealthInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$label: ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHealthTrackerScreen() {
    com.example.petsync1.ui.theme.PetSync1Theme {
        HealthTrackerContent(
            navController = rememberNavController(),
            petList = listOf(Pet(id = "1", name = "Buddy")),
            healthRecords = emptyMap(),
            onPetSelected = {},
            onDeleteRecord = { _, _, _ -> },
            onDeleteRecords = { _, _, _ -> },
            onAddRecordClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHealthTrackerScreenDark() {
    com.example.petsync1.ui.theme.PetSync1Theme(darkTheme = true) {
        HealthTrackerContent(
            navController = rememberNavController(),
            petList = listOf(Pet(id = "1", name = "Buddy")),
            healthRecords = emptyMap(),
            onPetSelected = {},
            onDeleteRecord = { _, _, _ -> },
            onDeleteRecords = { _, _, _ -> },
            onAddRecordClick = {},
            isDarkMode = true
        )
    }
}

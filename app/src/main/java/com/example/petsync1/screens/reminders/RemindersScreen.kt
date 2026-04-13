package com.example.petsync1.screens.reminders

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.petsync1.R
import com.example.petsync1.models.Reminder
import com.example.petsync1.navigation.BottomNavBar
import com.example.petsync1.viewmodels.ReminderViewModel
import com.example.petsync1.ui.theme.PetSync1Theme

@Composable
fun RemindersScreen(
    navController: NavHostController,
    reminderViewModel: ReminderViewModel = viewModel(),
    isDarkMode: Boolean = false
) {
    // The ViewModel now handles its own real-time listener and sorting
    val reminders by reminderViewModel.reminders.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        reminderViewModel.startObservingReminders()
    }

    RemindersContent(
        navController = navController,
        reminders = reminders,
        onDeleteReminder = { reminder -> reminderViewModel.deleteReminder(context, reminder.id, reminder.petId) },
        onToggleCompletion = { reminder -> reminderViewModel.toggleReminderCompletion(context, reminder) },
        isDarkMode = isDarkMode
    )
}

@Composable
fun RemindersContent(
    navController: NavHostController,
    reminders: List<Reminder>,
    onDeleteReminder: (Reminder) -> Unit,
    onToggleCompletion: (Reminder) -> Unit,
    isDarkMode: Boolean = false
) {
    var selectedCategory by remember { mutableStateOf("General") }

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController, currentRoute = "Reminders_Screen")
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
                    text = "Reminders",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Category Selection Buttons
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

                // Reminder Records in themed container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f), shape = RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val filteredReminders = reminders.filter { it.category == selectedCategory }

                    if (filteredReminders.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No $selectedCategory reminders found",
                                fontSize = 20.sp,
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
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            filteredReminders.forEach { reminder ->
                                ReminderCard(
                                    reminder = reminder,
                                    onToggleCompletion = onToggleCompletion,
                                    onDeleteReminder = onDeleteReminder
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Add Reminder Button
                Button(
                    onClick = { navController.navigate("add_reminder_screen") },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .width(200.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add Reminder", color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    onToggleCompletion: (Reminder) -> Unit,
    onDeleteReminder: (Reminder) -> Unit
) {
    val status = reminder.getDisplayStatus()
    val statusColor = when (status) {
        "Completed" -> Color(0xFF4CAF50)
        "Overdue" -> Color(0xFFF44336)
        else -> Color(0xFFFFC107)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 4.dp)
            .clickable { onToggleCompletion(reminder) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completion Toggle
            IconButton(onClick = { onToggleCompletion(reminder) }) {
                Icon(
                    imageVector = if (reminder.completed) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = "Toggle Complete",
                    tint = if (reminder.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = reminder.type,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Pet: ${reminder.petName}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (reminder.category == "General") {
                    Text(
                        text = "Due: ${reminder.dueDate}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "${reminder.perDay} times per day",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ends: ${reminder.endDate}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                // Status Badge
                Surface(
                    color = statusColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                IconButton(onClick = { onDeleteReminder(reminder) }) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_delete),
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReminderScreen() {
    PetSync1Theme {
        RemindersContent(
            navController = rememberNavController(),
            reminders = listOf(
                Reminder(id = "1", petName = "Buddy", type = "Vaccination", dueDate = "12/12/2024", category = "General", completed = false),
                Reminder(id = "2", petName = "Max", type = "Insulin", perDay = "2", endDate = "12/12/2024", category = "Medications", completed = true)
            ),
            onDeleteReminder = {},
            onToggleCompletion = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReminderScreenDark() {
    PetSync1Theme(darkTheme = true) {
        RemindersContent(
            navController = rememberNavController(),
            reminders = listOf(
                Reminder(id = "1", petName = "Buddy", type = "Vaccination", dueDate = "12/12/2024", category = "General", completed = false),
                Reminder(id = "2", petName = "Max", type = "Insulin", perDay = "2", endDate = "12/12/2024", category = "Medications", completed = true)
            ),
            onDeleteReminder = {},
            onToggleCompletion = {},
            isDarkMode = true
        )
    }
}

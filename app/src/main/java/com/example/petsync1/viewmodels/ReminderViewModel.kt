package com.example.petsync1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.petsync1.models.Reminder
import kotlinx.coroutines.tasks.await
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel responsible for managing reminders across all pets.
 * Uses Firestore Collection Groups to query reminders efficiently.
 */
class ReminderViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var remindersListener: ListenerRegistration? = null

    // StateFlow to provide a reactive list of reminders to the UI
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders


    /**
     * Starts a real-time listener using a Firestore Collection Group.
     * This allows fetching all "reminders" sub-collections for a specific owner, 
     * regardless of which pet they belong to.
     */
    fun startObservingReminders() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("ReminderViewModel", "Cannot start observing: User is not logged in")
            return
        }
        val userId = currentUser.uid
        Log.d("ReminderViewModel", "Starting collectionGroup observer for user: $userId")
        
        remindersListener?.remove()
        
        // Collection Group query to find all "reminders" sub-collections
        remindersListener = db.collectionGroup("reminders")
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ReminderViewModel", "Firestore listen failed: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val fetchedReminders = snapshot.documents.mapNotNull { document ->
                        val reminder = document.toObject(Reminder::class.java)
                        reminder?.copy(id = document.id)
                    }
                    
                    // Sort reminders: Incomplete first, then by date
                    val sortedReminders = fetchedReminders.sortedWith(
                        compareBy<Reminder> { it.completed }
                            .thenBy { parseDate(it.dueDate.ifEmpty { it.endDate }) }
                    )
                    
                    _reminders.value = sortedReminders
                }
            }
    }

    /**
     * Utility to parse date strings for sorting purposes.
     */
    private fun parseDate(dateStr: String): Long {
        if (dateStr.isEmpty()) return Long.MAX_VALUE
        return try {
            val sdf = SimpleDateFormat("M/d/yyyy", Locale.getDefault())
            sdf.parse(dateStr)?.time ?: Long.MAX_VALUE
        } catch (e: Exception) {
            Log.e("ReminderViewModel", "Error parsing date: $dateStr", e)
            Long.MAX_VALUE
        }
    }

    /**
     * Adds a new reminder to a specific pet's sub-collection.
     * Also schedules a system alarm for the reminder.
     */
    fun addStandaloneReminder(context: android.content.Context, reminder: Reminder) {
        if (reminder.petId.isEmpty()) {
            Log.e("ReminderViewModel", "Cannot add reminder: Pet ID is empty")
            return
        }

        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val docRef = db.collection("pets").document(reminder.petId)
                    .collection("reminders").document()
                
                val finalId = docRef.id
                val reminderToSave = reminder.copy(id = finalId, ownerId = userId)

                docRef.set(reminderToSave).await()

                // Schedule the system notification
                com.example.petsync1.utils.AlarmManagerHelper.scheduleReminder(context, reminderToSave)
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error adding reminder: ${e.message}")
            }
        }
    }

    /**
     * Toggles the completion status of a reminder and updates the alarm accordingly.
     */
    fun toggleReminderCompletion(context: android.content.Context, reminder: Reminder) {
        if (reminder.id.isEmpty() || reminder.petId.isEmpty()) {
            Log.e("ReminderViewModel", "Cannot toggle completion: ID or PetID is empty")
            return
        }

        val newStatus = !reminder.completed
        
        // Optimistic UI update for immediate feedback
        val currentList = _reminders.value
        val updatedList = currentList.map {
            if (it.id == reminder.id) it.copy(completed = newStatus) else it
        }.sortedWith(
            compareBy<Reminder> { it.completed }
                .thenBy { parseDate(it.dueDate.ifEmpty { it.endDate }) }
        )
        _reminders.value = updatedList

        viewModelScope.launch {
            try {
                db.collection("pets").document(reminder.petId)
                    .collection("reminders").document(reminder.id)
                    .update("completed", newStatus)
                    .await()
                
                val updatedReminder = reminder.copy(completed = newStatus)
                if (newStatus) {
                    // Cancel alarm if marked completed
                    com.example.petsync1.utils.AlarmManagerHelper.cancelReminder(context, updatedReminder)
                } else {
                    // Reschedule if unmarked
                    com.example.petsync1.utils.AlarmManagerHelper.scheduleReminder(context, updatedReminder)
                }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error toggling completion: ${e.message}")
                startObservingReminders() // Re-sync on failure
            }
        }
    }

    /**
     * Deletes a reminder from Firestore and cancels any pending alarms.
     */
    fun deleteReminder(context: android.content.Context, reminderId: String, petId: String) {
        if (reminderId.isEmpty() || petId.isEmpty()) {
            Log.e("ReminderViewModel", "Cannot delete: ID or PetID is empty")
            return
        }

        viewModelScope.launch {
            try {
                val reminderToDelete = _reminders.value.find { it.id == reminderId }
                db.collection("pets").document(petId)
                    .collection("reminders").document(reminderId)
                    .delete().await()
                
                reminderToDelete?.let { 
                    com.example.petsync1.utils.AlarmManagerHelper.cancelReminder(context, it)
                }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error deleting reminder: ${e.message}")
            }
        }
    }

    /**
     * Clean up listeners when ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        remindersListener?.remove()
    }
    
}

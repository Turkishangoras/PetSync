package com.example.petsync1.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Data class representing a health record for a pet.
 * Supports different types of medical events like Vaccinations, Deworming, Vet Visits, and Medications.
 */
data class HealthRecord(
    val id: String = "",
    val ownerId: String = "",
    val petId: String = "",
    val category: String = "",
    val date: String = "",
    val type: String = "",
    val nextDueDate: String = "",
    val endDate: String? = null, // Specific for Medications
    val startTime: String? = null, // Specific for Medications
    val perDay: String? = null, // Specific for Medications
    val notes: String = ""
)

/**
 * ViewModel for managing health records. 
 * Handles adding, deleting, and fetching records from a sub-collection under each pet.
 */
class HealthRecordViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Grouped health records by category for efficient UI display
    private val _healthRecords = MutableStateFlow<Map<String, List<HealthRecord>>>(emptyMap())
    val healthRecords: StateFlow<Map<String, List<HealthRecord>>> = _healthRecords

    /**
     * Adds a new health record to the pet's sub-collection in Firestore.
     */
    fun addHealthRecord(petId: String, record: HealthRecord) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val ref = db.collection("pets").document(petId).collection("health_records").document()
                val recordWithId = record.copy(id = ref.id, ownerId = userId, petId = petId)
                ref.set(recordWithId).await()
                fetchHealthRecords(petId)  // Refresh the local state after adding
            } catch (e: Exception) {
                Log.e("HealthRecordViewModel", "Error adding health record: ${e.message}")
            }
        }
    }

    /**
     * Deletes a single health record.
     */
    fun deleteHealthRecord(petId: String, recordId: String) {
        viewModelScope.launch {
            try {
                Log.d("HealthRecordViewModel", "Deleting record: $recordId")
                db.collection("pets").document(petId).collection("health_records").document(recordId).delete().await()
                fetchHealthRecords(petId) // Refresh list
            } catch (e: Exception) {
                Log.e("HealthRecordViewModel", "Error deleting record: ${e.message}")
            }
        }
    }

    /**
     * Performs a batch delete of multiple health records.
     */
    fun deleteHealthRecords(petId: String, recordIds: Set<String>) {
        viewModelScope.launch {
            try {
                val batch = db.batch()
                recordIds.forEach { recordId ->
                    val ref = db.collection("pets").document(petId).collection("health_records").document(recordId)
                    batch.delete(ref)
                }
                batch.commit().await()
                fetchHealthRecords(petId) // Refresh list
            } catch (e: Exception) {
                Log.e("HealthRecordViewModel", "Error bulk deleting records: ${e.message}")
            }
        }
    }

    /**
     * Fetches all health records for a specific pet and groups them by category.
     */
    fun fetchHealthRecords(petId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                
                val result = db.collection("pets").document(petId).collection("health_records")
                    .whereEqualTo("ownerId", userId)
                    .get()
                    .await()

                val allRecords = result.documents.mapNotNull { doc ->
                    doc.toObject(HealthRecord::class.java)?.copy(id = doc.id)
                }

                // Transform the flat list into a map for category-based UI tabs
                val recordMap = allRecords.groupBy { it.category }
                _healthRecords.value = recordMap
            } catch (e: Exception) {
                Log.e("HealthRecordViewModel", "Error fetching health records: ${e.message}")
            }
        }
    }
}

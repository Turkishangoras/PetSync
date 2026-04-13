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

data class HealthRecord(
    val id: String = "",
    val ownerId: String = "",
    val petId: String = "",
    val category: String = "",
    val date: String = "",
    val type: String = "",
    val nextDueDate: String = "",
    val endDate: String? = null, // Needed for Medications
    val startTime: String? = null, // Needed for Medications
    val perDay: String? = null, // Needed for Medications
    val notes: String = ""
)

class HealthRecordViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _healthRecords = MutableStateFlow<Map<String, List<HealthRecord>>>(emptyMap())
    val healthRecords: StateFlow<Map<String, List<HealthRecord>>> = _healthRecords

    fun addHealthRecord(petId: String, record: HealthRecord) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val ref = db.collection("pets").document(petId).collection("health_records").document()
                val recordWithId = record.copy(id = ref.id, ownerId = userId, petId = petId)
                ref.set(recordWithId).await()
                fetchHealthRecords(petId)  // Auto-refresh records after adding
            } catch (e: Exception) {
                Log.e("HealthRecordViewModel", "Error adding health record: ${e.message}")
            }
        }
    }

    fun deleteHealthRecord(petId: String, recordId: String) {
        viewModelScope.launch {
            try {
                Log.d("HealthRecordViewModel", "Deleting record: $recordId from health_records for pet: $petId")
                db.collection("pets").document(petId).collection("health_records").document(recordId).delete().await()
                fetchHealthRecords(petId)
            } catch (e: Exception) {
                Log.e("HealthRecordViewModel", "Error deleting record: ${e.message}")
            }
        }
    }

    fun deleteHealthRecords(petId: String, recordIds: Set<String>) {
        viewModelScope.launch {
            try {
                Log.d("HealthRecordViewModel", "Bulk deleting ${recordIds.size} records from health_records")
                val batch = db.batch()
                recordIds.forEach { recordId ->
                    val ref = db.collection("pets").document(petId).collection("health_records").document(recordId)
                    batch.delete(ref)
                }
                batch.commit().await()
                fetchHealthRecords(petId)
            } catch (e: Exception) {
                Log.e("HealthRecordViewModel", "Error bulk deleting records: ${e.message}")
            }
        }
    }

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

                val recordMap = allRecords.groupBy { it.category }
                _healthRecords.value = recordMap
            } catch (e: Exception) {
                Log.e("HealthRecordViewModel", "Error fetching health records: ${e.message}")
            }
        }
    }
}
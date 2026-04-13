package com.example.petsync1.utils

import android.content.Context
import android.widget.Toast
import com.example.petsync1.models.Reminder
import com.example.petsync1.viewmodels.HealthRecord
import com.example.petsync1.viewmodels.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

/**
 * Utility class to populate Firestore with test data using the new consolidated structure.
 */
object TestDataHelper {

    fun populateTestData(context: Context) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        
        // 1. Create a Test Pet
        val petId = UUID.randomUUID().toString()
        val testPet = Pet(
            id = petId,
            name = "Test Buddy",
            age = "2 years",
            breed = "Golden Retriever",
            weight = "25kg",
            healthStatus = "Healthy",
            ownerId = userId
        )

        db.collection("pets").document(petId).set(testPet)
            .addOnSuccessListener {
                // 2. Add Health Records to pets/{petId}/health_records
                val healthRecordId = UUID.randomUUID().toString()
                val testRecord = HealthRecord(
                    id = healthRecordId,
                    petId = petId,
                    ownerId = userId,
                    category = "Vaccination",
                    type = "Rabies Shot",
                    date = "10/10/2023",
                    notes = "Annual booster"
                )
                
                db.collection("pets").document(petId)
                    .collection("health_records").document(healthRecordId)
                    .set(testRecord)

                // 3. Add Reminders to pets/{petId}/reminders
                val reminderId = UUID.randomUUID().toString()
                val testReminder = Reminder(
                    id = reminderId,
                    petId = petId,
                    petName = testPet.name,
                    type = "Grooming",
                    dueDate = "12/25/2023",
                    category = "General",
                    ownerId = userId
                )

                db.collection("pets").document(petId)
                    .collection("reminders").document(reminderId)
                    .set(testReminder)

                Toast.makeText(context, "Test data populated for Buddy!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to populate data: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}

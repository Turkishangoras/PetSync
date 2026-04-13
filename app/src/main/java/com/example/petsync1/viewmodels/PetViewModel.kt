package com.example.petsync1.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Pet(
    val id: String = "",
    val name: String = "",
    val age: String = "",
    val breed: String = "",
    val weight: String = "",
    val healthStatus: String = "",
    val imageUrl: String = "",
    val ownerId: String = ""
)

class PetViewModel : ViewModel() {
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private var petListener: ListenerRegistration? = null  // For real-time updates

    // **Save pet data to Firestore with image upload**
    fun addPetWithImage(
        pet: Pet,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        val ownerId = user?.uid ?: ""

        if (ownerId.isEmpty()) {
            onFailure(Exception("User not logged in"))
            return
        }

        viewModelScope.launch {
            try {
                val petRef = db.collection("pets").document()
                var imageUrl = ""

                if (imageUri != null) {
                    val imageRef = storage.reference.child("pet_images/${petRef.id}.jpg")
                    imageRef.putFile(imageUri).await()
                    imageUrl = imageRef.downloadUrl.await().toString()
                }

                val petWithId = pet.copy(id = petRef.id, imageUrl = imageUrl, ownerId = ownerId)
                petRef.set(petWithId).await()

                onSuccess()
            } catch (e: Exception) {
                Log.e("PetViewModel", "Error adding pet: ${e.message}")
                onFailure(e)
            }
        }
    }

    private val _petList = MutableStateFlow<List<Pet>>(emptyList())
    val petList: StateFlow<List<Pet>> = _petList

    /**
     * Starts a real-time listener for all pets belonging to the given owner.
     * Replaces manual fetching for better UI synchronization.
     */
    fun startObservingPets(ownerId: String) {
        if (ownerId.isEmpty()) return

        petListener?.remove()
        petListener = db.collection("pets")
            .whereEqualTo("ownerId", ownerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PetViewModel", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pets = snapshot.documents.mapNotNull { it.toObject(Pet::class.java) }
                    _petList.value = pets
                }
            }
    }

    // **Fetch All Pets for a Specific User**
    fun getAllPetsForUser(ownerId: String, onResult: (List<Pet>) -> Unit) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("pets")
                    .whereEqualTo("ownerId", ownerId)
                    .get()
                    .await()
                val pets = snapshot.documents.mapNotNull { it.toObject(Pet::class.java) }
                _petList.value = pets  // Update StateFlow
                onResult(pets)  // Ensure callback is always called
            } catch (e: Exception) {
                Log.e("PetViewModel", "Error fetching pets: ${e.message}")
                _petList.value = emptyList()
                onResult(emptyList())  // Call onResult to update UI
            }
        }
    }

    // **Update pet data in Firestore**
    fun updatePetHealthStatus(petId: String, newStatus: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                db.collection("pets").document(petId)
                    .update("healthStatus", newStatus)
                    .await()
                onSuccess()
            } catch (e: Exception) {
                Log.e("PetViewModel", "Error updating pet status: ${e.message}")
                onFailure(e)
            }
        }
    }

    // **Stop Listening to Updates**
    override fun onCleared() {
        super.onCleared()
        petListener?.remove()
    }
}

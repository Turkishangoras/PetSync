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

/**
 * Data class representing a Pet entity.
 * @property id Unique identifier for the pet in Firestore.
 * @property name Name of the pet.
 * @property age Age of the pet as a string.
 * @property breed Breed of the pet.
 * @property weight Weight of the pet.
 * @property healthStatus Current health status (e.g., "Healthy").
 * @property imageUrl URL of the pet's photo stored in Firebase Storage.
 * @property ownerId The UID of the user who owns this pet.
 */
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

/**
 * ViewModel responsible for managing pet profiles and their data synchronization with Firebase.
 */
class PetViewModel : ViewModel() {
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private var petListener: ListenerRegistration? = null  // Real-time listener registration

    /**
     * Saves pet data to Firestore and handles image upload to Firebase Storage.
     * @param pet The pet object to be saved.
     * @param imageUri The local URI of the image to be uploaded.
     * @param onSuccess Callback triggered after successful save.
     * @param onFailure Callback triggered if any error occurs.
     */
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
                // Generate a new document reference first to get a unique ID
                val petRef = db.collection("pets").document()
                var imageUrl = ""

                // Upload image if provided
                if (imageUri != null) {
                    val imageRef = storage.reference.child("pet_images/${petRef.id}.jpg")
                    imageRef.putFile(imageUri).await()
                    imageUrl = imageRef.downloadUrl.await().toString()
                }

                // Create the final pet object with the generated ID and image URL
                val petWithId = pet.copy(id = petRef.id, imageUrl = imageUrl, ownerId = ownerId)
                petRef.set(petWithId).await()

                onSuccess()
            } catch (e: Exception) {
                Log.e("PetViewModel", "Error adding pet: ${e.message}")
                onFailure(e)
            }
        }
    }

    // StateFlow containing the list of pets for reactive UI updates
    private val _petList = MutableStateFlow<List<Pet>>(emptyList())
    val petList: StateFlow<List<Pet>> = _petList

    /**
     * Starts a real-time listener for all pets belonging to a specific user.
     * This ensures the UI updates instantly when a pet is added or modified.
     */
    fun startObservingPets(ownerId: String) {
        if (ownerId.isEmpty()) return

        petListener?.remove() // Remove previous listener if exists
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

    /**
     * Fetches all pets for a user manually.
     * @param ownerId The UID of the owner.
     * @param onResult Callback with the list of pets.
     */
    fun getAllPetsForUser(ownerId: String, onResult: (List<Pet>) -> Unit) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("pets")
                    .whereEqualTo("ownerId", ownerId)
                    .get()
                    .await()
                val pets = snapshot.documents.mapNotNull { it.toObject(Pet::class.java) }
                _petList.value = pets  // Update StateFlow for UI reactivity
                onResult(pets)
            } catch (e: Exception) {
                Log.e("PetViewModel", "Error fetching pets: ${e.message}")
                _petList.value = emptyList()
                onResult(emptyList())
            }
        }
    }

    /**
     * Updates only the health status of a specific pet in Firestore.
     */
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

    /**
     * Cleanup when ViewModel is destroyed to prevent memory leaks.
     */
    override fun onCleared() {
        super.onCleared()
        petListener?.remove()
    }
}

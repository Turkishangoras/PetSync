# PetSync 🐾

**PetSync** is a modern, comprehensive pet health and lifestyle management application built for Android. It helps pet owners keep track of their furry friends' medical records, vaccinations, medications, and daily routines through an intuitive and beautiful interface.

---

## ✨ Features

- **🐕 Pet Profiles:** Create and manage detailed profiles for all your pets, including their breed, age, weight, and health status.
- **🏥 Health Tracker:** A consolidated health record system to log vaccinations, deworming, vet visits, and more.
- **⏰ Smart Reminders:** Never miss a pill or a vet appointment. Set one-time or recurring reminders for medications and general tasks.
- **🔄 Real-time Sync:** Powered by Firebase Firestore, your data stays in sync across devices instantly.
- **🎨 Modern UI/UX:** Built entirely with Jetpack Compose and Material 3, featuring a clean "Green-tone" theme with full Dark Mode support.
- **🔒 Secure Auth:** Firebase-powered authentication for secure sign-ups and profile management.
- **📂 Cloud Storage:** Safely store pet photos using Firebase Storage.

---

## 🛠️ Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Database:** [Firebase Firestore](https://firebase.google.com/docs/firestore)
- **Authentication:** [Firebase Auth](https://firebase.google.com/docs/auth)
- **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
- **Background Tasks:** [AlarmManager](https://developer.android.com/training/scheduling/alarms) for precise notifications.
- **Dependency:** Material 3, Navigation Compose, Coroutines, Flow.

---

## 🏗️ Architecture & Data Structure

The app follows a clean MVVM architecture to ensure scalability and maintainability. 

### Firestore Schema Optimization
Recently refactored to a more efficient, flat hierarchy:
- `users/` - User profile data (name, email).
- `pets/` - Top-level collection for all pets.
    - `pets/{petId}/health_records` - Consolidated sub-collection for all medical events (Category-based filtering).
    - `pets/{petId}/reminders` - Pet-specific reminders.

*Used **Firestore Collection Groups** for efficient querying of all reminders across multiple pets.*

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Iguana or newer.
- A Firebase project.

### Setup
1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/PetSync.git
   ```
2. **Add Firebase:**
   - Create a project in the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with the package name `com.example.petsync1`.
   - Download `google-services.json` and place it in the `app/` directory.
   - Enable **Email/Password Authentication**, **Firestore**, and **Firebase Storage**.
3. **Build & Run:**
   - Sync the project with Gradle files.
   - Run the app on an emulator or physical device.

---

## 🧪 Testing with Sample Data
To quickly see the app in action:
1. Go to the **Settings** screen.
2. Tap **"Populate Test Data"**.
3. This will instantly create a test pet with pre-filled health records and reminders for your account.

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author
**Nawwaf** - [Turkishangoras](https://github.com/Turkishangoras)

*Made with ❤️ for pet lovers.*

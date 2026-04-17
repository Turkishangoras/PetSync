package com.example.petsync1.viewmodels

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "settings")

/**
 * ViewModel responsible for managing and persisting the application's theme settings (Dark/Light mode).
 * Uses DataStore Preferences for lightweight local storage of user preferences.
 */
class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.dataStore
    private val darkModeKey = booleanPreferencesKey("dark_mode")

    /**
     * StateFlow providing the current dark mode status.
     * Maps the DataStore value and defaults to 'false' (Light Mode) if not set.
     */
    val isDarkMode: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[darkModeKey] ?: false
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    /**
     * Persists the user's dark mode preference to DataStore.
     * @param enabled True for Dark Mode, False for Light Mode.
     */
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[darkModeKey] = enabled
            }
        }
    }
}

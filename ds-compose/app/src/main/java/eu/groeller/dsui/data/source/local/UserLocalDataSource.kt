package eu.groeller.dsui.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import eu.groeller.datastreamui.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Local data source for user-related data.
 * This handles all local storage operations for user data.
 */
class UserLocalDataSource(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val USERNAME = stringPreferencesKey("username")
        val EMAIL = stringPreferencesKey("email")
        val TOKEN = stringPreferencesKey("token")
    }
    
    /**
     * Stream of user data from local storage.
     */
    val userFlow: Flow<LocalUserState> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val token = preferences[PreferencesKeys.TOKEN]
            val username = preferences[PreferencesKeys.USERNAME]
            val email = preferences[PreferencesKeys.EMAIL]
            
            if (token != null && username != null && email != null) {
                LocalUserState.Available(User(username, email, token))
            } else {
                LocalUserState.NotAvailable
            }
        }
    
    /**
     * Saves user data to local storage.
     * 
     * @param user The user to save
     */
    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERNAME] = user.username
            preferences[PreferencesKeys.EMAIL] = user.email
            preferences[PreferencesKeys.TOKEN] = user.token
        }
    }
    
    /**
     * Clears user data from local storage.
     */
    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USERNAME)
            preferences.remove(PreferencesKeys.EMAIL)
            preferences.remove(PreferencesKeys.TOKEN)
        }
    }
}

/**
 * Represents the state of user data in local storage.
 */
sealed class LocalUserState {
    /**
     * User data is available in local storage.
     */
    data class Available(val user: User) : LocalUserState()
    
    /**
     * User data is not available in local storage.
     */
    object NotAvailable : LocalUserState()
} 
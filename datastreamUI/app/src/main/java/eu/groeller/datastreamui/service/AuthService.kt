package eu.groeller.datastreamui.service

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthService(val context: Context, private val dataStore: DataStore<Preferences>) {

    suspend fun readToken(): String? {
        val tokenKey = stringPreferencesKey("auth_token")
        return dataStore.data.map { preferences -> preferences[tokenKey] }.first()
    }
}
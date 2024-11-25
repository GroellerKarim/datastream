package eu.groeller.datastreamui.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import eu.groeller.datastreamui.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalDataSource(private val dataStore: DataStore<Preferences>) {

    suspend fun readToken(): String? {
        val tokenKey = stringPreferencesKey("auth_token")
        return dataStore.data.map { preferences -> preferences[tokenKey] }.first()
    }

    suspend fun readUser(): User? {
        val tokenKey = stringPreferencesKey("auth_token")
        val tokenFlow =  dataStore.data.map { preferences -> preferences[tokenKey]}.first()

        if(tokenFlow == null) return null

        val usernameFlow = dataStore.data.map { preferences -> preferences[stringPreferencesKey("username")] }
        val emailFlow = dataStore.data.map { preferences -> preferences[stringPreferencesKey("email")] }

        return User(usernameFlow.first()!!, emailFlow.first()!!, tokenFlow)
    }
}
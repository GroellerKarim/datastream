package eu.groeller.datastreamui.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.user.LocalUserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalDataSource(private val dataStore: DataStore<Preferences>) {

    val tokenKey = stringPreferencesKey("auth_token")
    val usernameKey = stringPreferencesKey("username")
    val emailKey = stringPreferencesKey("email")

    val localUserStream: Flow<LocalUserState> = dataStore.data.map {
        val token = it[tokenKey]

        if (token == null)
            LocalUserState.LocalMissing
        else
            LocalUserState.Success(token)
    }

    suspend fun readToken(): String? {
        return dataStore.data.map { preferences -> preferences[tokenKey] }.first()
    }

    suspend fun readUser(): User? {
        val tokenFlow =  dataStore.data.map { preferences -> preferences[tokenKey]}.first()

        if(tokenFlow == null) return null

        val usernameFlow = dataStore.data.map { preferences -> preferences[usernameKey] }
        val emailFlow = dataStore.data.map { preferences -> preferences[emailKey] }

        return User(usernameFlow.first()!!, emailFlow.first()!!, tokenFlow)
    }

    suspend fun writeUser(user: User) {
        dataStore.edit { preferencesDataStore ->
            preferencesDataStore[tokenKey] = user.token
            preferencesDataStore[usernameKey] = user.username
            preferencesDataStore[emailKey] = user.email
        }
    }
}
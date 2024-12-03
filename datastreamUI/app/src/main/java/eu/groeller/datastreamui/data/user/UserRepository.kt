package eu.groeller.datastreamui.data.user

import eu.groeller.datastreamui.data.datasource.LocalDataSource
import eu.groeller.datastreamui.data.datasource.UserNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(private val localDataSource: LocalDataSource, private val userNetworkDataSource: UserNetworkDataSource) {

    val userState: Flow<UserState> = localDataSource.localUserStream.map { localUserState ->
        when (localUserState) {
            is LocalUserState.Loading -> UserState.Loading
            is LocalUserState.LocalMissing -> UserState.LocalMissing
            is LocalUserState.Success -> userNetworkDataSource.fetchUserWithToken(localUserState.token)
        }
    }


    suspend fun registerUser(username: String, email: String, password: String) {
        userNetworkDataSource.registerUser(username, email, password)
    }

    suspend fun loginUser(email: String, password: String) {
        val user = userNetworkDataSource.loginUser(email, password)
        localDataSource.writeUser(user)
    }
}
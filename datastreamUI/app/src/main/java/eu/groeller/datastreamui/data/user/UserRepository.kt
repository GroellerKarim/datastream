package eu.groeller.datastreamui.data

import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.datasource.LocalDataSource
import eu.groeller.datastreamui.data.datasource.UserNetworkDataSource
import kotlinx.coroutines.flow.Flow

class UserRepository(private val localDataSource: LocalDataSource, private val userNetworkDataSource: UserNetworkDataSource) {

    val userStream: Flow<User?> = localDataSource.userStream

    suspend fun registerUser(username: String, email: String, password: String) {
        userNetworkDataSource.registerUser(username, email, password)
    }

    suspend fun loginUser(email: String, password: String) {
        val user = userNetworkDataSource.loginUser(email, password)
        localDataSource.writeUser(user)
    }
}
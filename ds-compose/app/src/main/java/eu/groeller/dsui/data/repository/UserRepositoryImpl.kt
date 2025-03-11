package eu.groeller.dsui.data.repository

import eu.groeller.dsui.data.mapper.UserMapper.toDomain
import eu.groeller.dsui.data.source.local.LocalUserState
import eu.groeller.dsui.data.source.local.UserLocalDataSource
import eu.groeller.dsui.data.source.remote.UserRemoteDataSource
import eu.groeller.dsui.domain.model.User
import eu.groeller.dsui.domain.repository.IUserRepository
import eu.groeller.dsui.domain.repository.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of the IUserRepository interface.
 * This acts as a mediator between the domain layer and data sources.
 */
class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : IUserRepository {
    
    /**
     * Flow of user states that updates when user state changes.
     */
    override val userState: Flow<UserState> = userLocalDataSource.userFlow.map { localUserState ->
        when (localUserState) {
            is LocalUserState.Available -> {
                try {
                    val user = userRemoteDataSource.validateToken(localUserState.user.token)
                    UserState.LoggedIn(user.toDomain())
                } catch (e: Exception) {
                    // Token validation failed, clear local user data
                    userLocalDataSource.clearUser()
                    UserState.NotLoggedIn
                }
            }
            is LocalUserState.NotAvailable -> UserState.NotLoggedIn
        }
    }
    
    /**
     * Register a new user.
     */
    override suspend fun registerUser(username: String, email: String, password: String): Result<User> {
        return try {
            val user = userRemoteDataSource.registerUser(username, email, password)
            userLocalDataSource.saveUser(user)
            Result.success(user.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Login with email and password.
     */
    override suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val user = userRemoteDataSource.loginUser(email, password)
            userLocalDataSource.saveUser(user)
            Result.success(user.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Logout the current user.
     */
    override suspend fun logoutUser(): Result<Unit> {
        return try {
            userLocalDataSource.clearUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Validate the user's token.
     */
    override suspend fun validateToken(token: String): Result<User> {
        return try {
            val user = userRemoteDataSource.validateToken(token)
            Result.success(user.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 
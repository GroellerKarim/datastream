package eu.groeller.dsui.domain.repository

import eu.groeller.dsui.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user-related operations.
 */
interface IUserRepository {
    
    /**
     * Get the current user state as a flow.
     * The flow will emit updates when the user state changes.
     * 
     * @return Flow of User state
     */
    val userState: Flow<UserState>
    
    /**
     * Register a new user.
     * 
     * @param username The username
     * @param email The email address
     * @param password The password
     * @return Result containing User if successful, error otherwise
     */
    suspend fun registerUser(username: String, email: String, password: String): Result<User>
    
    /**
     * Login with email and password.
     * 
     * @param email The email address
     * @param password The password
     * @return Result containing User if successful, error otherwise
     */
    suspend fun loginUser(email: String, password: String): Result<User>
    
    /**
     * Logout the current user.
     * 
     * @return Result indicating success or error
     */
    suspend fun logoutUser(): Result<Unit>
    
    /**
     * Validate the user's token.
     * 
     * @param token The auth token to validate
     * @return Result containing User if valid, error otherwise
     */
    suspend fun validateToken(token: String): Result<User>
}

/**
 * Represents the possible states of user authentication.
 */
sealed class UserState {
    /**
     * Loading state when fetching user information
     */
    object Loading : UserState()
    
    /**
     * State when no user is logged in
     */
    object NotLoggedIn : UserState()
    
    /**
     * State when user is successfully authenticated
     */
    data class LoggedIn(val user: User) : UserState()
    
    /**
     * State when authentication failed or token is invalid
     */
    data class Error(val message: String) : UserState()
} 
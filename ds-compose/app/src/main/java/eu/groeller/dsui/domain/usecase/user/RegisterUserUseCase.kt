package eu.groeller.dsui.domain.usecase.user

import eu.groeller.dsui.domain.model.User
import eu.groeller.dsui.domain.repository.IUserRepository

/**
 * Use case for registering a new user.
 */
class RegisterUserUseCase(private val userRepository: IUserRepository) {
    
    /**
     * Registers a new user with the given username, email, and password.
     * 
     * @param username The username for the new user.
     * @param email The email address for the new user.
     * @param password The password for the new user.
     * @return Result containing the registered user or an error.
     */
    suspend operator fun invoke(username: String, email: String, password: String): Result<User> {
        // Validation logic
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Username, email, and password cannot be blank"))
        }
        
        if (username.length < 3) {
            return Result.failure(IllegalArgumentException("Username must be at least 3 characters"))
        }
        
        if (!email.contains("@")) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }
        
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }
        
        return userRepository.registerUser(username, email, password)
    }
} 
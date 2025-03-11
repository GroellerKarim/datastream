package eu.groeller.dsui.domain.usecase.user

import eu.groeller.dsui.domain.model.User
import eu.groeller.dsui.domain.repository.IUserRepository
import java.util.regex.Pattern

/**
 * Use case for user login.
 */
class LoginUserUseCase(private val userRepository: IUserRepository) {
    
    // Email validation pattern
    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
        "@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
    )
    
    /**
     * Execute the use case to login a user.
     *
     * @param email The user's email
     * @param password The user's password
     * @return Result containing the logged-in user if successful, error otherwise
     */
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Validation logic
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be blank"))
        }
        
        if (!emailPattern.matcher(email).matches()) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }
        
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be blank"))
        }
        
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }
        
        return userRepository.loginUser(email, password)
    }
} 
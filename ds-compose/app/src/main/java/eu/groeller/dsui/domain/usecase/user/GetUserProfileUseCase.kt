package eu.groeller.dsui.domain.usecase.user

import eu.groeller.dsui.domain.model.User
import eu.groeller.dsui.domain.repository.IUserRepository

/**
 * Use case for retrieving the current user's profile.
 */
class GetUserProfileUseCase(private val userRepository: IUserRepository) {
    
    /**
     * Retrieves the current user's profile.
     * 
     * @return Result containing the user profile if authenticated, or null if not authenticated.
     */
    suspend operator fun invoke(): Result<User?> {
        return userRepository.getCurrentUser()
    }
} 
package eu.groeller.dsui.presentation.mapper

import eu.groeller.dsui.domain.model.User
import eu.groeller.dsui.presentation.model.UserUI

/**
 * Mapper for converting User domain model to UserUI presentation model.
 */
object UserUIMapper {
    
    /**
     * Converts a domain User to a UI model.
     */
    fun User.toUI(
        isLoggedIn: Boolean = true,
        displayName: String? = null,
        profileImageUrl: String? = null
    ): UserUI {
        return UserUI(
            username = username,
            email = email,
            isLoggedIn = isLoggedIn,
            displayName = displayName ?: username,  // Default to username if display name not provided
            profileImageUrl = profileImageUrl
        )
    }
    
    /**
     * Creates a new UserUI with updated login state.
     */
    fun UserUI.withLoginState(isLoggedIn: Boolean): UserUI {
        return this.copy(isLoggedIn = isLoggedIn)
    }
    
    /**
     * Creates a new UserUI with updated display name.
     */
    fun UserUI.withDisplayName(displayName: String): UserUI {
        return this.copy(displayName = displayName)
    }
    
    /**
     * Creates a new UserUI with updated profile image URL.
     */
    fun UserUI.withProfileImage(profileImageUrl: String?): UserUI {
        return this.copy(profileImageUrl = profileImageUrl)
    }
    
    /**
     * Creates a logout (unauthenticated) version of the user.
     */
    fun UserUI.toLoggedOut(): UserUI {
        return this.copy(isLoggedIn = false)
    }
} 
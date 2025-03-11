package eu.groeller.dsui.presentation.model

/**
 * UI model for representing a user in the user interface.
 */
data class UserUI(
    val username: String,
    val email: String,
    val isLoggedIn: Boolean = false,
    
    // Additional UI-specific properties that might be useful
    val displayName: String? = null,
    val profileImageUrl: String? = null
) 
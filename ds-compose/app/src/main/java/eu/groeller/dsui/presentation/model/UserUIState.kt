package eu.groeller.dsui.presentation.model

/**
 * UI state for user-related screens (login, registration, profile).
 * Represents different states the UI can be in when dealing with user authentication and profile.
 */
sealed class UserUIState {
    /**
     * Initial state when no action has been taken.
     */
    object Initial : UserUIState()
    
    /**
     * Loading state during authentication or user data fetching.
     */
    object Loading : UserUIState()
    
    /**
     * Authenticated state with user data.
     */
    data class Authenticated(val user: UserUI) : UserUIState()
    
    /**
     * Unauthenticated state when user is not logged in or session expired.
     */
    object Unauthenticated : UserUIState()
    
    /**
     * Error state during authentication or data fetching.
     */
    data class Error(val message: String) : UserUIState()
    
    /**
     * State representing successful registration but not yet authenticated.
     */
    object RegistrationSuccess : UserUIState()
} 
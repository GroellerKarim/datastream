package eu.groeller.dsui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.groeller.dsui.domain.usecase.user.LoginUserUseCase
import eu.groeller.dsui.domain.usecase.user.RegisterUserUseCase
import eu.groeller.dsui.domain.usecase.user.GetUserProfileUseCase
import eu.groeller.dsui.presentation.mapper.UserUIMapper.toUI
import eu.groeller.dsui.presentation.model.UserUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the dashboard screen.
 * Handles user authentication and profile management.
 */
class DashViewModel(
    private val loginUserUseCase: LoginUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserUIState>(UserUIState.Initial)
    val uiState: StateFlow<UserUIState> = _uiState.asStateFlow()

    init {
        checkUserAuthentication()
    }

    /**
     * Checks if the user is authenticated and loads profile if they are.
     */
    private fun checkUserAuthentication() {
        viewModelScope.launch {
            _uiState.value = UserUIState.Loading
            
            getUserProfileUseCase().fold(
                onSuccess = { user ->
                    if (user != null) {
                        _uiState.value = UserUIState.Authenticated(user.toUI())
                    } else {
                        _uiState.value = UserUIState.Unauthenticated
                    }
                },
                onFailure = { error ->
                    _uiState.value = UserUIState.Unauthenticated
                }
            )
        }
    }

    /**
     * Logs in a user with the provided credentials.
     */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = UserUIState.Error("Email and password cannot be empty")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = UserUIState.Loading
            
            loginUserUseCase(email, password).fold(
                onSuccess = { user ->
                    _uiState.value = UserUIState.Authenticated(user.toUI())
                },
                onFailure = { error ->
                    _uiState.value = UserUIState.Error(
                        error.message ?: "Login failed. Please check your credentials."
                    )
                }
            )
        }
    }

    /**
     * Registers a new user with the provided details.
     */
    fun register(username: String, email: String, password: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = UserUIState.Error("All fields are required")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = UserUIState.Loading
            
            registerUserUseCase(username, email, password).fold(
                onSuccess = { user ->
                    _uiState.value = UserUIState.RegistrationSuccess
                },
                onFailure = { error ->
                    _uiState.value = UserUIState.Error(
                        error.message ?: "Registration failed. Please try again."
                    )
                }
            )
        }
    }

    /**
     * Logs out the current user.
     */
    fun logout() {
        viewModelScope.launch {
            _uiState.value = UserUIState.Loading
            
            // A logout use case could be added here if needed
            _uiState.value = UserUIState.Unauthenticated
        }
    }

    /**
     * Clears any error state.
     */
    fun clearError() {
        val currentState = _uiState.value
        if (currentState is UserUIState.Error) {
            _uiState.value = UserUIState.Unauthenticated
        }
    }
}
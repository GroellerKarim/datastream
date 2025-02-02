package eu.groeller.datastreamui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.ErrorResponse
import eu.groeller.datastreamui.data.user.UserRepository
import eu.groeller.datastreamui.data.user.UserState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashViewModel(private val userRepository: UserRepository): ViewModel() {

    val userState = userRepository.userState

    val uiState: StateFlow<UserState> = userState.stateIn(
        scope = viewModelScope,
        initialValue = UserState.Loading,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                userRepository.loginUser(email, password)
                println("State changed successfully [${userState.first()}]")
            } catch (e: Exception) {
                println("Error changing state: ${e.message}")
            }
        }
    }

}
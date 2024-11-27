package eu.groeller.datastreamui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashViewModel(private val userRepository: UserRepository): ViewModel() {

    val user = userRepository.userStream

}
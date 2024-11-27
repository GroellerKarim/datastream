package eu.groeller.datastreamui.viewmodel

import eu.groeller.datastreamui.User

sealed interface DashViewState {
    data object Loading: DashViewState
    data class LoggedIn(val user: User): DashViewState
    data class Failure(val message: String): DashViewState
}
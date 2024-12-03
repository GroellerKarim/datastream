package eu.groeller.datastreamui.data.user

sealed interface LocalUserState {
    data object Loading: LocalUserState
    data object LocalMissing: LocalUserState
    data class Success(val token: String): LocalUserState
}
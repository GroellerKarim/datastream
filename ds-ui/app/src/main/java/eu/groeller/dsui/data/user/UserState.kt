package eu.groeller.datastreamui.data.user

import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.ErrorResponse

sealed interface UserState {
    data object Loading: UserState
    data object LocalMissing: UserState
    data class NetworkFailed(val err: ErrorResponse): UserState
    data class Success(val user: User): UserState // -> After Backend / Network has confirmed token
}
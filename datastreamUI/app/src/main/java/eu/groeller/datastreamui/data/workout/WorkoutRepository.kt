package eu.groeller.datastreamui.data.workout

import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.V1_PATH
import eu.groeller.datastreamui.data.model.WorkoutResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WorkoutRepository(private val httpClient: HttpClient, private val user: User) {

    val fetchWorkoutState: Flow<WorkoutState> = flow { emit(getWorkouts(user)) }

    suspend fun getWorkouts(user: User): WorkoutState {
        val response = httpClient.get("${V1_PATH}/workouts") {
            bearerAuth(user.token)
        }

        if (response.status.isSuccess()) {
            val body: List<WorkoutResponse> =  response.body()
            return WorkoutState.Success(body)
        }

        return WorkoutState.Error(response.body())
    }
}
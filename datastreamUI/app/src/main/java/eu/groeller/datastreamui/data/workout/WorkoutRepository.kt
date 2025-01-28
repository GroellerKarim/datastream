package eu.groeller.datastreamui.data.workout

import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.V1_PATH
import eu.groeller.datastreamui.data.model.WorkoutResponse
import eu.groeller.datastreamui.data.model.WorkoutType
import eu.groeller.datastreamui.model.Slice
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WorkoutRepository(private val httpClient: HttpClient, private val user: User) {

    val fetchWorkoutState: Flow<WorkoutState> = flow { emit(getWorkouts(user)) }

    suspend fun getWorkouts(user: User): WorkoutState {
        val response = httpClient.get("${V1_PATH}/workouts") {
            url {
                parameters.append("page", "0")
                parameters.append("size", "5")
            }
            bearerAuth(user.token)
        }

        if (response.status.isSuccess()) {
            val body: Slice<WorkoutResponse> =  response.body()
            return WorkoutState.Success(body.content)
        }

        return WorkoutState.Error(response.body())
    }

    suspend fun addWorkoutType(workoutType: String): WorkoutType? {
        val response = httpClient.post("${V1_PATH}/workouts/workout-type") {
            bearerAuth(user.token)
            setBody(mapOf("name" to workoutType))
        }

        if(response.status.isSuccess()) {
            return response.body()
        }

        return null
    }
}
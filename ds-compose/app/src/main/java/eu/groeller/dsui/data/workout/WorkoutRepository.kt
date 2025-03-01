package eu.groeller.datastreamui.data.workout

import android.util.Log
import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.V1_PATH
import eu.groeller.datastreamui.data.model.CreateWorkoutRequest
import eu.groeller.datastreamui.data.model.WorkoutResponse
import eu.groeller.datastreamui.data.model.WorkoutType
import eu.groeller.datastreamui.model.Slice
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WorkoutRepository(private val httpClient: HttpClient, private val user: User) {
    companion object {
        private const val TAG = "WorkoutRepository"  // Tag for filtering logs
    }

    val fetchWorkoutState: Flow<WorkoutState> = flow { emit(getWorkouts(user)) }

    suspend fun createWorkout(workoutRequest: CreateWorkoutRequest): WorkoutResponse {
        val response = httpClient.post("${V1_PATH}/workouts") {
            bearerAuth(user.token)
            contentType(ContentType.Application.Json)
            setBody(workoutRequest, TypeInfo(workoutRequest::class))
        }

        return response.body()
    }

    suspend fun getWorkouts(user: User): WorkoutState {
        val response = httpClient.get("${V1_PATH}/workouts") {
            url {
                parameters.append("page", "0")
                parameters.append("size", "5")
            }
            bearerAuth(user.token)
        }

        if (response.status.isSuccess()) {
            val body: Slice<WorkoutResponse> = response.body()
            return WorkoutState.Success(body.content)
        }

        return WorkoutState.Error(response.body())
    }

    suspend fun addWorkoutType(workoutType: String): WorkoutType? {
        Log.d(TAG, "Adding new workout type: $workoutType")

        try {
            val response = httpClient.post("${V1_PATH}/workouts/workout-type") {
                contentType(ContentType.Application.Json)
                bearerAuth(user.token)
                setBody(mapOf("name" to workoutType))
            }

            if (response.status.isSuccess()) {
                val result: WorkoutType = response.body()
                Log.d(TAG, "Successfully added workout type: ${result.name}")
                return result
            }

            Log.e(TAG, "Failed to add workout type. Status: ${response.status}")
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Error adding workout type", e)
            return null
        }
    }
}
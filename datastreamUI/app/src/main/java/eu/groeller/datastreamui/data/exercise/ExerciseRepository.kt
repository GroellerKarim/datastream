package eu.groeller.datastreamui.data.exercise

import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.V1_PATH
import eu.groeller.datastreamui.data.model.ExerciseDefinition
import eu.groeller.datastreamui.data.model.WorkoutType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.isSuccess

class ExerciseRepository(
    private val httpClient: HttpClient,
    private val user: User
) {
    suspend fun getAllExercises(): List<ExerciseDefinition> {
        val response = httpClient.get("${V1_PATH}/workouts/exercises") {
            bearerAuth(user.token)
        }

        if (response.status.isSuccess()) {
            return response.body()
        }

        return emptyList()
    }

    suspend fun getWorkoutTypes(): List<WorkoutType> {
        val response = httpClient.get("${V1_PATH}/workouts/types") {
            bearerAuth(user.token)
        }

        if (response.status.isSuccess()) {
            return response.body()
        }

        return emptyList()
    }

    suspend fun getRecentExercisesForType(workoutType: WorkoutType, limit: Int = 5): List<ExerciseDefinition> {
        val response = httpClient.get("${V1_PATH}/exercises/recent/${workoutType.id}") {
            url {
                parameters.append("limit", limit.toString())
            }
            bearerAuth(user.token)
        }

        if (response.status.isSuccess()) {
            return response.body()
        }

        return emptyList()
    }
} 
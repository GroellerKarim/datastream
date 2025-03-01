package eu.groeller.datastreamui.data.exercise

import android.util.Log
import eu.groeller.datastreamui.DS_TAG
import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.V1_PATH
import eu.groeller.datastreamui.data.model.ExerciseDefinition
import eu.groeller.datastreamui.data.model.ExerciseType
import eu.groeller.datastreamui.data.model.WorkoutType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class ExerciseRepository(
    private val httpClient: HttpClient,
    private val user: User
) {
    suspend fun getAllExercises(): List<ExerciseDefinition> {
        val response = httpClient.get("${V1_PATH}/exercises/all") {
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

    suspend fun createExercise(name: String, type: ExerciseType): ExerciseDefinition? {
        Log.d(DS_TAG, "Sending createExercise Request to Server")
        try {
            val response = httpClient.post("${V1_PATH}/exercises/create") {
                contentType(ContentType.Application.Json)
                bearerAuth(user.token)
                setBody(mapOf(
                    "name" to name,
                    "type" to type.name
                ))
            }

            // TODO: Add proper exception handling when "already exists" status code is returned
            if (response.status.isSuccess()) {
                return response.body()
            }

            Log.e(DS_TAG, "Failed to create exercise definition. Status: ${response.status}")
            return null
        } catch (e: Exception) {
            Log.e(DS_TAG, "Unexpected Error when creating exerciseDefinition type", e)
            return null
        }

    }
} 
package eu.groeller.dsui.data.source.remote

import eu.groeller.datastreamui.data.V1_PATH
import eu.groeller.datastreamui.data.model.CreateExerciseDefinitionRequest
import eu.groeller.datastreamui.data.model.CreateExerciseRequest
import eu.groeller.datastreamui.data.model.ExerciseDefinition
import eu.groeller.datastreamui.data.model.ExerciseRecordResponse
import eu.groeller.datastreamui.data.model.ExerciseType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

/**
 * Remote data source for exercise-related API operations.
 * This handles all network communication with the exercise endpoints.
 */
class ExerciseRemoteDataSource(
    private val httpClient: HttpClient
) {
    /**
     * Fetches exercise definitions from the API.
     *
     * @param token The authentication token
     * @param type Optional exercise type to filter by
     * @return A list of exercise definitions
     */
    suspend fun getExerciseDefinitions(token: String, type: ExerciseType? = null): List<ExerciseDefinition> {
        val response = httpClient.get("$V1_PATH/exercises") {
            bearerAuth(token)
            type?.let {
                url {
                    parameters.append("type", it.name)
                }
            }
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to get exercise definitions: ${response.status}")
        }
        
        return response.body()
    }
    
    /**
     * Fetches a specific exercise definition by ID.
     *
     * @param token The authentication token
     * @param id The exercise definition ID
     * @return The exercise definition
     */
    suspend fun getExerciseDefinitionById(token: String, id: Long): ExerciseDefinition {
        val response = httpClient.get("$V1_PATH/exercises/$id") {
            bearerAuth(token)
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to get exercise definition: ${response.status}")
        }
        
        return response.body()
    }
    
    /**
     * Creates a new exercise definition.
     *
     * @param token The authentication token
     * @param name The name of the exercise
     * @param type The type of the exercise
     * @return The created exercise definition
     */
    suspend fun createExerciseDefinition(token: String, name: String, type: ExerciseType): ExerciseDefinition {
        val request = CreateExerciseDefinitionRequest(name, type)
        val response = httpClient.post("$V1_PATH/exercises") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to create exercise definition: ${response.status}")
        }
        
        return response.body()
    }
    
    /**
     * Creates a new exercise record.
     *
     * @param token The authentication token
     * @param workoutId The ID of the workout this exercise belongs to
     * @param createExerciseRequest The exercise creation request
     * @return The created exercise record
     */
    suspend fun createExercise(token: String, workoutId: Long, createExerciseRequest: CreateExerciseRequest): ExerciseRecordResponse {
        val response = httpClient.post("$V1_PATH/workouts/$workoutId/exercises") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(createExerciseRequest)
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to create exercise: ${response.status}")
        }
        
        return response.body()
    }
} 
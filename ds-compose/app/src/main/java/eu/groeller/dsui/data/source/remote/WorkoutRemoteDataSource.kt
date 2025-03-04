package eu.groeller.dsui.data.source.remote

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

/**
 * Remote data source for workout-related API operations.
 * This handles all network communication with the workout endpoints.
 */
class WorkoutRemoteDataSource(
    private val httpClient: HttpClient
) {
    /**
     * Fetches a paginated list of workouts from the API.
     *
     * @param token The authentication token
     * @param page The page number (0-based)
     * @param size The number of items per page
     * @return A Slice containing workouts
     */
    suspend fun getWorkouts(token: String, page: Int, size: Int): Slice<WorkoutResponse> {
        val response = httpClient.get("$V1_PATH/workouts") {
            url {
                parameters.append("page", page.toString())
                parameters.append("size", size.toString())
            }
            bearerAuth(token)
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to get workouts: ${response.status}")
        }
        
        return response.body()
    }
    
    /**
     * Fetches a specific workout by ID.
     *
     * @param token The authentication token
     * @param id The workout ID
     * @return The workout
     */
    suspend fun getWorkoutById(token: String, id: Long): WorkoutResponse {
        val response = httpClient.get("$V1_PATH/workouts/$id") {
            bearerAuth(token)
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to get workout: ${response.status}")
        }
        
        return response.body()
    }
    
    /**
     * Creates a new workout.
     *
     * @param token The authentication token
     * @param createWorkoutRequest The workout creation request
     * @return The created workout
     */
    suspend fun createWorkout(token: String, createWorkoutRequest: CreateWorkoutRequest): WorkoutResponse {
        val response = httpClient.post("$V1_PATH/workouts") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(createWorkoutRequest)
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to create workout: ${response.status}")
        }
        
        return response.body()
    }
    
    /**
     * Fetches all available workout types.
     *
     * @param token The authentication token
     * @return A list of workout types
     */
    suspend fun getWorkoutTypes(token: String): List<WorkoutType> {
        val response = httpClient.get("$V1_PATH/workouts/workout-type") {
            bearerAuth(token)
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to get workout types: ${response.status}")
        }
        
        return response.body()
    }
    
    /**
     * Creates a new workout type.
     *
     * @param token The authentication token
     * @param name The name of the workout type
     * @return The created workout type
     */
    suspend fun createWorkoutType(token: String, name: String): WorkoutType {
        val response = httpClient.post("$V1_PATH/workouts/workout-type") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name))
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to create workout type: ${response.status}")
        }
        
        return response.body()
    }
} 
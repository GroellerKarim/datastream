package eu.groeller.ds.presentation.api.exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.groeller.ds.config.TestContainersConfig;
import eu.groeller.ds.domain.exercise.ExerciseType;
import eu.groeller.ds.presentation.request.exercise.*;
import eu.groeller.ds.presentation.request.user.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
class WorkoutControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create a test user and get authentication token
        UserRequest createRequest = new UserRequest("testuser", "test@workout.com", "password123");
        MvcResult result = mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn();

        if (result.getResponse().getStatus() != 201) {
            System.out.println("Registration failed with status: " + result.getResponse().getStatus());
            System.out.println("Response body: " + result.getResponse().getContentAsString());
        }

        // Login and get token
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new eu.groeller.ds.presentation.request.user.UserLoginRequest("test@example.com", "password123"))))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String response = mvcResult.getResponse().getContentAsString();
                    authToken = objectMapper.readTree(response).get("token").asText();
                });
    }

    @Test
    void createWorkoutType_ShouldCreateNewWorkoutType() throws Exception {
        // Arrange
        String uniqueId = UUID.randomUUID().toString();
        CreateWorkoutTypeRequest request = new CreateWorkoutTypeRequest("Push Day " + uniqueId);

        // Act & Assert
        mockMvc.perform(post("/api/v1/workouts/workout-type")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Push Day " + uniqueId))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createWorkout_ShouldCreateNewWorkout() throws Exception {
        // Arrange
        // First create a workout type
        String uniqueId = UUID.randomUUID().toString();
        CreateWorkoutTypeRequest typeRequest = new CreateWorkoutTypeRequest("Pull Day " + uniqueId);
        MvcResult workoutTypeResult = mockMvc.perform(post("/api/v1/workouts/workout-type")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Get the workout type name from the response
        String workoutTypeName = objectMapper.readTree(workoutTypeResult.getResponse().getContentAsString())
                .get("name").asText();

        // Create an exercise definition
        CreateExerciseDefinitionRequest exerciseDefRequest = new CreateExerciseDefinitionRequest(
                "Bench Press " + uniqueId,
                ExerciseType.SETS_REPS
        );
        MvcResult exerciseDefResult = mockMvc.perform(post("/api/v1/exercises/create")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseDefRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Get the exercise definition ID from the response
        Long exerciseDefId = objectMapper.readTree(exerciseDefResult.getResponse().getContentAsString())
                .get("id").asLong();

        OffsetDateTime now = OffsetDateTime.now();
        CreateWorkoutRequest request = new CreateWorkoutRequest(
                List.of(new ExerciseRecordRequest(
                        exerciseDefId,
                        now,
                        now.plusMinutes(30),
                        new ExerciseRecordDetailsRequest(
                                null, // distance
                                null, // distanceUnit
                                null, // distancePerUnit
                                List.of(new ExerciseSetRequest(
                                        now,
                                        now.plusMinutes(1),
                                        false,
                                        12,
                                        0,
                                        60.0,
                                        1
                                )),
                                60.0 // weightKg
                        ),
                        1
                )),
                workoutTypeName,
                now,
                now.plusHours(1)
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/workouts")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workoutId").exists())
                .andExpect(jsonPath("$.workoutType").value(workoutTypeName));
    }

    @Test
    void getWorkouts_ShouldReturnWorkouts() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/workouts")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getWorkoutTypes_ShouldReturnWorkoutTypes() throws Exception {
        // Arrange
        // Create a workout type first
        String uniqueId = UUID.randomUUID().toString();
        CreateWorkoutTypeRequest request = new CreateWorkoutTypeRequest("Legs Day " + uniqueId);
        mockMvc.perform(post("/api/v1/workouts/workout-type")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Act & Assert
        mockMvc.perform(get("/api/v1/workouts/types")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").exists());
    }

}
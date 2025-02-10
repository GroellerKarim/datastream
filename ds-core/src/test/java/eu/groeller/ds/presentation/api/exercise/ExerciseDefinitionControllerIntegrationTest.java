package eu.groeller.ds.presentation.api.exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.groeller.ds.config.TestContainersConfig;
import eu.groeller.ds.domain.exercise.ExerciseType;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseDefinitionRequest;
import eu.groeller.ds.presentation.request.exercise.CreateWorkoutTypeRequest;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
class ExerciseDefinitionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create a test user with unique username and email
        String uniqueId = UUID.randomUUID().toString();
        UserRequest createRequest = new UserRequest(
                "testuser_" + uniqueId,
                "test_" + uniqueId + "@exerciseDefinition.com",
                "password123"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        // Login and get token
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new eu.groeller.ds.presentation.request.user.UserLoginRequest(createRequest.email(), createRequest.password()))))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String response = mvcResult.getResponse().getContentAsString();
                    authToken = objectMapper.readTree(response).get("token").asText();
                });
    }

    @Test
    void createExerciseDefinition_ShouldCreateNewExercise() throws Exception {
        // Arrange
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        CreateExerciseDefinitionRequest request = new CreateExerciseDefinitionRequest(
                "Bench Press " + uniqueId,
                ExerciseType.SETS_REPS
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/exercises/create")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bench Press " + uniqueId))
                .andExpect(jsonPath("$.type").value("SETS_REPS"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createExerciseDefinition_WhenDuplicate_ShouldReturnError() throws Exception {
        // Arrange
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        CreateExerciseDefinitionRequest request = new CreateExerciseDefinitionRequest(
                "Squats " + uniqueId,
                ExerciseType.SETS_REPS
        );

        // First creation should succeed
        mockMvc.perform(post("/api/v1/exercises/create")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second creation should fail
        mockMvc.perform(post("/api/v1/exercises/create")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_ShouldReturnAllExercises() throws Exception {
        // Arrange
        // Create a few exercises first
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        CreateExerciseDefinitionRequest request1 = new CreateExerciseDefinitionRequest("Deadlift " + uniqueId, ExerciseType.SETS_REPS);
        CreateExerciseDefinitionRequest request2 = new CreateExerciseDefinitionRequest("Running " + uniqueId, ExerciseType.DISTANCE);

        mockMvc.perform(post("/api/v1/exercises/create")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/exercises/create")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // Act & Assert
        mockMvc.perform(get("/api/v1/exercises/all")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name").exists())
                .andExpect(jsonPath("$[*].type").exists());
    }

    @Test
    void getRecentExercisesForType_ShouldReturnExercises() throws Exception {
        // Arrange
        // First create a workout type
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        CreateWorkoutTypeRequest typeRequest = new CreateWorkoutTypeRequest("Push Day " + uniqueId);
        MvcResult workoutTypeResult = mockMvc.perform(post("/api/v1/workouts/workout-type")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long workoutTypeId = objectMapper.readTree(workoutTypeResult.getResponse().getContentAsString())
                .get("id").asLong();

        // Create some exercises
        CreateExerciseDefinitionRequest request = new CreateExerciseDefinitionRequest(
                "Push-ups " + uniqueId,
                ExerciseType.SETS_REPS
        );

        mockMvc.perform(post("/api/v1/exercises/create")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Act & Assert
        mockMvc.perform(get("/api/v1/exercises/recent/" + workoutTypeId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getRecentExercisesForType_WhenTypeDoesNotExist_ShouldReturnError() throws Exception {
        mockMvc.perform(get("/api/v1/exercises/recent/999")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }
} 
package eu.groeller.ds.presentation.api.exercise;

import tools.jackson.databind.ObjectMapper;
import eu.groeller.ds.config.TestContainersConfig;
import eu.groeller.ds.domain.exercise.DistanceUnit;
import eu.groeller.ds.domain.exercise.ExerciseType;
import eu.groeller.ds.presentation.request.exercise.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        @Test
        void fullWorkoutLifecycle_startAddEntriesComplete() throws Exception {
                String uniqueId = UUID.randomUUID().toString().substring(0, 8);
                OffsetDateTime now = OffsetDateTime.now();
                String kcSubject = "kc-workout-" + uniqueId;

                // 1. Create a workout type
                mockMvc.perform(post("/api/v1/exercises/workout-types")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new CreateWorkoutTypeRequest("Push Day " + uniqueId))))
                                .andExpect(status().isCreated());

                // 2. Create exercise definitions
                MvcResult benchResult = mockMvc.perform(post("/api/v1/exercises")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new CreateExerciseDefinitionRequest(
                                                "Bench Press " + uniqueId, ExerciseType.WEIGHT_REPS))))
                                .andExpect(status().isCreated())
                                .andReturn();
                Long benchId = objectMapper.readTree(benchResult.getResponse().getContentAsString()).get("id").asLong();

                // 3. Start a workout
                MvcResult workoutResult = mockMvc.perform(post("/api/v1/workouts")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new StartWorkoutRequest("Push Day " + uniqueId, now))))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status").value("ACTIVE"))
                                .andExpect(jsonPath("$.entries").isEmpty())
                                .andReturn();
                Long workoutId = objectMapper.readTree(workoutResult.getResponse().getContentAsString()).get("id")
                                .asLong();

                // 4. Add exercise entries (3 sets of bench press)
                for (int i = 0; i < 3; i++) {
                        mockMvc.perform(post("/api/v1/workouts/" + workoutId + "/entries")
                                        .with(jwt().jwt(j -> j.subject(kcSubject)
                                                        .claim("preferred_username", "testuser_" + uniqueId)
                                                        .claim("email", "test_" + uniqueId + "@workout.com")))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(new CreateExerciseEntryRequest(
                                                        benchId, i + 1, now.plusMinutes(i * 5),
                                                        now.plusMinutes(i * 5 + 2),
                                                        80.0, 10, false, null, null, null, null))))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.exerciseName").value("Bench Press " + uniqueId))
                                        .andExpect(jsonPath("$.exerciseType").value("WEIGHT_REPS"))
                                        .andExpect(jsonPath("$.reps").value(10))
                                        .andExpect(jsonPath("$.weightKg").value(80.0));
                }

                // 5. Complete the workout
                mockMvc.perform(patch("/api/v1/workouts/" + workoutId + "/complete")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new CompleteWorkoutRequest(now.plusHours(1)))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("COMPLETED"))
                                .andExpect(jsonPath("$.entries").isArray())
                                .andExpect(jsonPath("$.entries.length()").value(3));

                // 6. Get the workout
                mockMvc.perform(get("/api/v1/workouts/" + workoutId)
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("COMPLETED"))
                                .andExpect(jsonPath("$.entries.length()").value(3));
        }

        @Test
        void cancelWorkout_shouldSucceed() throws Exception {
                String uniqueId = UUID.randomUUID().toString().substring(0, 8);
                OffsetDateTime now = OffsetDateTime.now();
                String kcSubject = "kc-cancel-" + uniqueId;

                // Create workout type
                mockMvc.perform(post("/api/v1/exercises/workout-types")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new CreateWorkoutTypeRequest("Cancel Test " + uniqueId))))
                                .andExpect(status().isCreated());

                // Start workout
                MvcResult workoutResult = mockMvc.perform(post("/api/v1/workouts")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new StartWorkoutRequest("Cancel Test " + uniqueId, now))))
                                .andExpect(status().isCreated())
                                .andReturn();
                Long workoutId = objectMapper.readTree(workoutResult.getResponse().getContentAsString()).get("id")
                                .asLong();

                // Cancel it
                mockMvc.perform(patch("/api/v1/workouts/" + workoutId + "/cancel")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("CANCELLED"));
        }

        @Test
        void getWorkouts_shouldReturnPaginatedList() throws Exception {
                String uniqueId = UUID.randomUUID().toString().substring(0, 8);

                mockMvc.perform(get("/api/v1/workouts")
                                .with(jwt().jwt(j -> j.subject("kc-list-" + uniqueId)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        void distanceExerciseEntry_shouldSucceed() throws Exception {
                String uniqueId = UUID.randomUUID().toString().substring(0, 8);
                OffsetDateTime now = OffsetDateTime.now();
                String kcSubject = "kc-distance-" + uniqueId;

                // Create workout type + exercise definition
                mockMvc.perform(post("/api/v1/exercises/workout-types")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new CreateWorkoutTypeRequest("Cardio " + uniqueId))))
                                .andExpect(status().isCreated());

                MvcResult runDefResult = mockMvc.perform(post("/api/v1/exercises")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new CreateExerciseDefinitionRequest(
                                                "Running " + uniqueId, ExerciseType.DISTANCE))))
                                .andExpect(status().isCreated())
                                .andReturn();
                Long runId = objectMapper.readTree(runDefResult.getResponse().getContentAsString()).get("id").asLong();

                // Start workout
                MvcResult workoutResult = mockMvc.perform(post("/api/v1/workouts")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new StartWorkoutRequest("Cardio " + uniqueId, now))))
                                .andExpect(status().isCreated())
                                .andReturn();
                Long workoutId = objectMapper.readTree(workoutResult.getResponse().getContentAsString()).get("id")
                                .asLong();

                // Add distance entry
                mockMvc.perform(post("/api/v1/workouts/" + workoutId + "/entries")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new CreateExerciseEntryRequest(
                                                runId, 1, now, now.plusHours(1),
                                                null, null, null, null, 10.0, DistanceUnit.KILOMETERS, 3600L))))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.exerciseType").value("DISTANCE"))
                                .andExpect(jsonPath("$.distance").value(10.0))
                                .andExpect(jsonPath("$.distanceUnit").value("KILOMETERS"))
                                .andExpect(jsonPath("$.durationSeconds").value(3600));
        }
}
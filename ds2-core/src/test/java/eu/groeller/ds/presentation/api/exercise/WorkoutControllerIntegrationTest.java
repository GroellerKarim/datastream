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
import java.util.ArrayList;
import java.util.List;
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
                UUID benchId = idOf(benchResult);

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
                UUID workoutId = idOf(workoutResult);

                // 4. Add one bench press entry carrying its three sets
                List<CreateExerciseSetRequest> benchSets = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                        benchSets.add(new CreateExerciseSetRequest(
                                        i + 1, now.plusMinutes(i * 5), now.plusMinutes(i * 5 + 2),
                                        80.0, 10, false, null, null, null, null));
                }

                mockMvc.perform(post("/api/v1/workouts/" + workoutId + "/entries")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new CreateExerciseEntryRequest(benchId, 1, benchSets))))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.exerciseName").value("Bench Press " + uniqueId))
                                .andExpect(jsonPath("$.exerciseType").value("WEIGHT_REPS"))
                                .andExpect(jsonPath("$.sets.length()").value(3))
                                .andExpect(jsonPath("$.sets[0].reps").value(10))
                                .andExpect(jsonPath("$.sets[0].weightKg").value(80.0))
                                // Rest is derived from the set timestamps, never sent by the client.
                                .andExpect(jsonPath("$.sets[0].restBeforeMs").doesNotExist())
                                .andExpect(jsonPath("$.sets[1].restBeforeMs").value(180000))
                                .andExpect(jsonPath("$.averageRestMs").value(180000));

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
                                .andExpect(jsonPath("$.entries.length()").value(1))
                                .andExpect(jsonPath("$.entries[0].sets.length()").value(3));

                // 6. Get the workout
                mockMvc.perform(get("/api/v1/workouts/" + workoutId)
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("COMPLETED"))
                                .andExpect(jsonPath("$.entries.length()").value(1))
                                .andExpect(jsonPath("$.entries[0].sets.length()").value(3))
                                // Entry start/end span its sets rather than being stored.
                                .andExpect(jsonPath("$.entries[0].startTime").exists())
                                .andExpect(jsonPath("$.entries[0].endTime").exists());
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
                UUID workoutId = idOf(workoutResult);

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
                UUID runId = idOf(runDefResult);

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
                UUID workoutId = idOf(workoutResult);

                // Add distance entry
                mockMvc.perform(post("/api/v1/workouts/" + workoutId + "/entries")
                                .with(jwt().jwt(j -> j.subject(kcSubject)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@workout.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new CreateExerciseEntryRequest(
                                                runId, 1, List.of(new CreateExerciseSetRequest(
                                                                1, now, now.plusHours(1), null, null, null, null,
                                                                10.0, DistanceUnit.KILOMETERS, 3600L))))))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.exerciseType").value("DISTANCE"))
                                .andExpect(jsonPath("$.sets.length()").value(1))
                                .andExpect(jsonPath("$.sets[0].distance").value(10.0))
                                .andExpect(jsonPath("$.sets[0].distanceUnit").value("KILOMETERS"))
                                .andExpect(jsonPath("$.sets[0].durationSeconds").value(3600))
                                .andExpect(jsonPath("$.averageRestMs").doesNotExist());
        }

        /** Ids are UUIDs assigned by the domain, so they arrive on the wire as strings. */
        private UUID idOf(MvcResult result) throws Exception {
                return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString())
                                .get("id").asString());
        }
}

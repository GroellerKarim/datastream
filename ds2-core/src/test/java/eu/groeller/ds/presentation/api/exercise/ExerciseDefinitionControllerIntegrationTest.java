package eu.groeller.ds.presentation.api.exercise;

import tools.jackson.databind.ObjectMapper;
import eu.groeller.ds.config.TestContainersConfig;
import eu.groeller.ds.domain.exercise.ExerciseType;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseDefinitionRequest;
import eu.groeller.ds.presentation.request.exercise.CreateWorkoutTypeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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

        @Test
        void createExerciseDefinition_shouldCreate() throws Exception {
                String uniqueId = UUID.randomUUID().toString().substring(0, 8);

                mockMvc.perform(post("/api/v1/exercises")
                                .with(jwt().jwt(j -> j.subject("kc-exdef-" + uniqueId)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@exdef.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new CreateExerciseDefinitionRequest(
                                                "Bench Press " + uniqueId, ExerciseType.WEIGHT_REPS))))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name").value("Bench Press " + uniqueId))
                                .andExpect(jsonPath("$.type").value("WEIGHT_REPS"))
                                .andExpect(jsonPath("$.id").exists());
        }

        @Test
        void createExerciseDefinition_whenDuplicate_shouldReturnError() throws Exception {
                String uniqueId = UUID.randomUUID().toString().substring(0, 8);
                var request = new CreateExerciseDefinitionRequest("Squats " + uniqueId, ExerciseType.WEIGHT_REPS);

                mockMvc.perform(post("/api/v1/exercises")
                                .with(jwt().jwt(j -> j.subject("kc-dup-" + uniqueId)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@exdef.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/api/v1/exercises")
                                .with(jwt().jwt(j -> j.subject("kc-dup-" + uniqueId)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@exdef.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void getAllExerciseDefinitions_shouldReturnList() throws Exception {
                String uniqueId = UUID.randomUUID().toString().substring(0, 8);

                mockMvc.perform(post("/api/v1/exercises")
                                .with(jwt().jwt(j -> j.subject("kc-list-" + uniqueId)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@exdef.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new CreateExerciseDefinitionRequest(
                                                "Deadlift " + uniqueId, ExerciseType.WEIGHT_REPS))))
                                .andExpect(status().isCreated());

                mockMvc.perform(get("/api/v1/exercises")
                                .with(jwt().jwt(j -> j.subject("kc-list-" + uniqueId)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@exdef.com"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[*].name").exists());
        }

        @Test
        void createWorkoutType_shouldCreate() throws Exception {
                String uniqueId = UUID.randomUUID().toString().substring(0, 8);

                mockMvc.perform(post("/api/v1/exercises/workout-types")
                                .with(jwt().jwt(j -> j.subject("kc-wt-" + uniqueId)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@exdef.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new CreateWorkoutTypeRequest("Push Day " + uniqueId))))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name").value("Push Day " + uniqueId))
                                .andExpect(jsonPath("$.id").exists());
        }

        @Test
        void getAllWorkoutTypes_shouldReturnList() throws Exception {
                String uniqueId = UUID.randomUUID().toString().substring(0, 8);

                mockMvc.perform(post("/api/v1/exercises/workout-types")
                                .with(jwt().jwt(j -> j.subject("kc-wt-list-" + uniqueId)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@exdef.com")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new CreateWorkoutTypeRequest("Legs Day " + uniqueId))))
                                .andExpect(status().isCreated());

                mockMvc.perform(get("/api/v1/exercises/workout-types")
                                .with(jwt().jwt(j -> j.subject("kc-wt-list-" + uniqueId)
                                                .claim("preferred_username", "testuser_" + uniqueId)
                                                .claim("email", "test_" + uniqueId + "@exdef.com"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[*].name").exists());
        }
}
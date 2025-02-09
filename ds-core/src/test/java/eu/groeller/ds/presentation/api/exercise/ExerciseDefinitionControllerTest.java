package eu.groeller.ds.presentation.api.exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.groeller.ds.configuration.SecuredWebMvcTest;
import eu.groeller.ds.domain.exercise.ExerciseDefinition;
import eu.groeller.ds.domain.exercise.ExerciseType;
import eu.groeller.ds.persistence.user.UserRepository;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseDefinitionRequest;
import eu.groeller.ds.service.exercise.ExerciseDefinitionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecuredWebMvcTest(ExerciseDefinitionController.class)
class ExerciseDefinitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExerciseDefinitionService exerciseDefinitionService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void createExerciseDefinition_WhenNotAuthenticated_Returns401() throws Exception {
        CreateExerciseDefinitionRequest request = new CreateExerciseDefinitionRequest(
                "Bench Press",
                ExerciseType.SETS_REPS
        );

        mockMvc.perform(post("/api/v1/exercise/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void createExerciseDefinition_WhenAuthenticated_ReturnsCreatedExercise() throws Exception {
        CreateExerciseDefinitionRequest request = new CreateExerciseDefinitionRequest(
                "Bench Press",
                ExerciseType.SETS_REPS
        );

        ExerciseDefinition expectedResponse = new ExerciseDefinition("Bench Press", ExerciseType.SETS_REPS);
        when(exerciseDefinitionService.createExerciseDefinition(any())).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/exercises/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bench Press"))
                .andExpect(jsonPath("$.type").value("SETS_REPS"));
    }
}

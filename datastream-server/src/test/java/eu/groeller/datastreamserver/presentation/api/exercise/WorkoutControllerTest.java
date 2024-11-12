package eu.groeller.datastreamserver.presentation.api.exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.groeller.datastreamserver.configuration.SecuredWebMvcTest;
import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.domain.exercise.ExerciseRecord;
import eu.groeller.datastreamserver.domain.exercise.Workout;
import eu.groeller.datastreamserver.persistence.user.UserRepository;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateWorkoutRequest;
import eu.groeller.datastreamserver.presentation.request.exercise.ExerciseRecordRequest;
import eu.groeller.datastreamserver.service.exercise.WorkoutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecuredWebMvcTest(WorkoutController.class)
class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WorkoutService workoutService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void createWorkout_WhenNotAuthenticated_Returns401() throws Exception {
        CreateWorkoutRequest request = new CreateWorkoutRequest(
            OffsetDateTime.now(),
            List.of(new ExerciseRecordRequest(1L, OffsetDateTime.now(), OffsetDateTime.now().plusMinutes(30), null, 1))
        );

        mockMvc.perform(post("/api/v1/workout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(workoutService, never()).createWorkout(any(), any());
    }

    @Test
    @WithMockUser
    void createWorkout_WhenAuthenticated_ReturnsCreatedWorkout() throws Exception {
        // Arrange
        OffsetDateTime now = OffsetDateTime.now();
        CreateWorkoutRequest request = new CreateWorkoutRequest(
            now,
            List.of(new ExerciseRecordRequest(1L, now, now.plusMinutes(30), null, 1))
        );

        User mockUser = new User("testuser", "test@example.com", "password");
        ExerciseRecord mockExerciseRecord = mock(ExerciseRecord.class);
        when(mockExerciseRecord.getStartTime()).thenReturn(now);
        when(mockExerciseRecord.getEndTime()).thenReturn(now.plusMinutes(30));
        
        Workout expectedWorkout = new Workout(mockUser, now, List.of(mockExerciseRecord));
        when(workoutService.createWorkout(any(User.class), any(CreateWorkoutRequest.class))).thenReturn(expectedWorkout);

        // Act & Assert
        mockMvc.perform(post("/api/v1/workout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(workoutService).createWorkout(any(), any(CreateWorkoutRequest.class));
    }
}

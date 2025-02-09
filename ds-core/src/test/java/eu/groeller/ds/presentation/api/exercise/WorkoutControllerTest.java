package eu.groeller.ds.presentation.api.exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.groeller.ds.configuration.SecuredWebMvcTest;
import eu.groeller.ds.configuration.security.CustomUserDetails;
import eu.groeller.ds.domain.User;
import eu.groeller.ds.domain.exercise.*;
import eu.groeller.ds.persistence.user.UserRepository;
import eu.groeller.ds.presentation.request.exercise.CreateWorkoutRequest;
import eu.groeller.ds.presentation.request.exercise.ExerciseRecordRequest;
import eu.groeller.ds.service.exercise.WorkoutService;
import lombok.val;
import org.junit.jupiter.api.Disabled;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
        val now = OffsetDateTime.now();
        CreateWorkoutRequest request = new CreateWorkoutRequest(
                List.of(new ExerciseRecordRequest(1L, OffsetDateTime.now(), OffsetDateTime.now().plusMinutes(30), null, 1)),
                "Pull-Day",
                now,
                now.plusHours(1)
        );

        mockMvc.perform(post("/api/v1/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(workoutService, never()).createWorkout(any(), any());
    }

    @Test
    @Disabled("Disabled until workout domain work is completed")
    @WithMockUser
    void createWorkout_WhenAuthenticated_ReturnsCreatedWorkout() throws Exception {
        // Arrange
        OffsetDateTime now = OffsetDateTime.now();
        CreateWorkoutRequest request = new CreateWorkoutRequest(
                List.of(new ExerciseRecordRequest(1L, now, now.plusMinutes(30), null, 1)),
                "Pull",
                now,
                now.plusHours(1)
        );

        User mockUser = new User("testuser", "test@example.com", "password");
        ExerciseRecord mockExerciseRecord = mock(ExerciseRecord.class);
        ExerciseDefinition ex = new ExerciseDefinition("Kek", ExerciseType.SETS_REPS);
        ex.setId(1L);
        when(mockExerciseRecord.getStartTime()).thenReturn(now);
        when(mockExerciseRecord.getEndTime()).thenReturn(now.plusMinutes(30));
        when(mockExerciseRecord.getExerciseDefinition()).thenReturn(ex);

        WorkoutType type = new WorkoutType("Pull");
        Workout expectedWorkout = new Workout(mockUser, request.startTime(), request.endTime(), List.of(mockExerciseRecord), type);
        when(workoutService.createWorkout(eq(mockUser), any(CreateWorkoutRequest.class))).thenReturn(expectedWorkout);

        // Act & Assert
        mockMvc.perform(post("/api/v1/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(new CustomUserDetails(mockUser)))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(workoutService).createWorkout(any(), any(CreateWorkoutRequest.class));
    }
}

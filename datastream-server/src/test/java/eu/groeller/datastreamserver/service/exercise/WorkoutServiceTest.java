package eu.groeller.datastreamserver.service.exercise;

import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.domain.exercise.ExerciseRecord;
import eu.groeller.datastreamserver.domain.exercise.Workout;
import eu.groeller.datastreamserver.persistence.exercise.WorkoutRepository;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateWorkoutRequest;
import eu.groeller.datastreamserver.presentation.request.exercise.ExerciseRecordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private ExerciseRecordService exerciseRecordService;

    private WorkoutService workoutService;
    private User testUser;
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        workoutService = new WorkoutService(workoutRepository, exerciseRecordService);
        testUser = new User("testuser", "test@example.com", "password");
        now = OffsetDateTime.now();
    }

    @Test
    void createWorkout_WhenRequestIsNull_ThrowsNullPointerException() {
        assertThatThrownBy(() -> workoutService.createWorkout(testUser, null))
                .isInstanceOf(NullPointerException.class);
        
        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }

    @Test
    void createWorkout_WhenUserIsNull_ThrowsNullPointerException() {
        CreateWorkoutRequest request = new CreateWorkoutRequest(now, List.of());

        assertThatThrownBy(() -> workoutService.createWorkout(null, request))
                .isInstanceOf(NullPointerException.class);
        
        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }

    @Test
    void createWorkout_WhenDateIsNull_ThrowsNullPointerException() {
        CreateWorkoutRequest request = new CreateWorkoutRequest(null, List.of());

        assertThatThrownBy(() -> workoutService.createWorkout(testUser, request))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("date");
        
        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }

    @Test
    void createWorkout_WhenExercisesIsNull_ThrowsNullPointerException() {
        CreateWorkoutRequest request = new CreateWorkoutRequest(now, null);

        assertThatThrownBy(() -> workoutService.createWorkout(testUser, request))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("exercises");
        
        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }

    @Test
    void createWorkout_WhenValidRequest_CreatesWorkout() {
        // Arrange
        ExerciseRecordRequest exerciseRequest = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), null, 1);
        CreateWorkoutRequest request = new CreateWorkoutRequest(now, List.of(exerciseRequest));
        
        ExerciseRecord mockExerciseRecord = mock(ExerciseRecord.class);
        when(mockExerciseRecord.getStartTime()).thenReturn(now);
        when(mockExerciseRecord.getEndTime()).thenReturn(now.plusMinutes(30));

        Workout expectedWorkout = new Workout(testUser, now, List.of(mockExerciseRecord));
        
        when(exerciseRecordService.createExerciseRecord(exerciseRequest)).thenReturn(mockExerciseRecord);
        when(workoutRepository.save(any(Workout.class))).thenReturn(expectedWorkout);

        // Act
        Workout result = workoutService.createWorkout(testUser, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getDate()).isEqualTo(now);
        assertThat(result.getExercises()).hasSize(1);
        
        verify(exerciseRecordService).createExerciseRecord(exerciseRequest);
        verify(workoutRepository).save(any(Workout.class));
    }


    @Test
    void createWorkout_WhenExerciseListIsEmpty_ThrowsIllegalArgumentException() {
        // Arrange
        CreateWorkoutRequest request = new CreateWorkoutRequest(now, List.of());

        // Act & Assert
        assertThatThrownBy(() -> workoutService.createWorkout(testUser, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Exercises must not be empty");
        
        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }
}

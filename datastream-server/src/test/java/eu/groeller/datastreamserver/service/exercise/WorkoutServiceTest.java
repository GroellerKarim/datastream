package eu.groeller.datastreamserver.service.exercise;

import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.domain.exercise.ExerciseRecord;
import eu.groeller.datastreamserver.domain.exercise.Workout;
import eu.groeller.datastreamserver.domain.exercise.WorkoutType;
import eu.groeller.datastreamserver.persistence.exercise.WorkoutRepository;
import eu.groeller.datastreamserver.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateWorkoutRequest;
import eu.groeller.datastreamserver.presentation.request.exercise.ExerciseRecordRequest;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private ExerciseRecordService exerciseRecordService;

    @Mock
    private WorkoutTypeRepository workoutTypeRepository;

    @Mock
    private Clock clock;

    private WorkoutService workoutService;
    private User testUser;


    @BeforeEach
    void setUp() {
        workoutService = new WorkoutService(workoutRepository, workoutTypeRepository, exerciseRecordService, clock);
        testUser = new User("testuser", "test@example.com", "password");
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
        CreateWorkoutRequest request = new CreateWorkoutRequest(List.of(), "Pull-Day");

        assertThatThrownBy(() -> workoutService.createWorkout(null, request))
                .isInstanceOf(NullPointerException.class);
        
        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }

    @Test
    void createWorkout_WhenExercisesIsNull_ThrowsNullPointerException() {
        CreateWorkoutRequest request = new CreateWorkoutRequest(null, "Pull-Day");

        assertThatThrownBy(() -> workoutService.createWorkout(testUser, request))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("exercises");
        
        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }

    @Test
    void createWorkout_WhenValidRequest_CreatesWorkout() {
        // Arrange
        val now = OffsetDateTime.now();
        when(clock.instant()).thenReturn(now.toInstant());
        when(clock.getZone()).thenReturn(now.toZonedDateTime().getZone());

        val workoutType = new WorkoutType("Pull-Day");
        ExerciseRecordRequest exerciseRequest = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), null, 1);
        CreateWorkoutRequest request = new CreateWorkoutRequest(List.of(exerciseRequest), workoutType.getName());

        when(workoutTypeRepository.findByName(workoutType.getName())).thenReturn(Optional.of(workoutType));

        ExerciseRecord mockExerciseRecord = mock(ExerciseRecord.class);
        when(mockExerciseRecord.getStartTime()).thenReturn(now);
        when(mockExerciseRecord.getEndTime()).thenReturn(now.plusMinutes(30));

        Workout expectedWorkout = new Workout(testUser, now, List.of(mockExerciseRecord), workoutType);
        
        when(exerciseRecordService.createExerciseRecord(exerciseRequest)).thenReturn(mockExerciseRecord);
        when(workoutRepository.save(any(Workout.class))).thenReturn(expectedWorkout);

        // Act
        Workout result = workoutService.createWorkout(testUser, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getDate()).isEqualTo(now);
        assertThat(result.getWorkoutType()).isEqualTo(workoutType);
        assertThat(result.getExercises()).hasSize(1);
        
        verify(exerciseRecordService).createExerciseRecord(exerciseRequest);
        verify(workoutRepository).save(any(Workout.class));
    }


    @Test
    void createWorkout_WhenExerciseListIsEmpty_ThrowsIllegalArgumentException() {
        // Arrange
        CreateWorkoutRequest request = new CreateWorkoutRequest(List.of(), "Pull-Day");

        // Act & Assert
        assertThatThrownBy(() -> workoutService.createWorkout(testUser, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Exercises must not be empty");
        
        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }
}

package eu.groeller.datastreamserver.service.exercise;

import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.domain.exercise.ExerciseRecord;
import eu.groeller.datastreamserver.domain.exercise.Workout;
import eu.groeller.datastreamserver.domain.exercise.WorkoutType;
import eu.groeller.datastreamserver.persistence.exercise.WorkoutRepository;
import eu.groeller.datastreamserver.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateWorkoutRequest;
import eu.groeller.datastreamserver.presentation.request.exercise.ExerciseRecordRequest;
import eu.groeller.datastreamserver.service.exceptions.DSIllegalArgumentException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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

    @Mock
    private ExerciseDefinitionService exerciseDefinitionService;

    @Mock
    private WorkoutTypeRepository workoutTypeRepository;

    @Mock
    private Clock clock;

    private WorkoutService workoutService;
    private User testUser;


    @BeforeEach
    void setUp() {
        workoutService = new WorkoutService(workoutRepository, workoutTypeRepository, exerciseRecordService, exerciseDefinitionService, clock);
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
        CreateWorkoutRequest request = new CreateWorkoutRequest(List.of(), "Pull-Day", OffsetDateTime.now(), OffsetDateTime.now());

        assertThatThrownBy(() -> workoutService.createWorkout(null, request))
                .isInstanceOf(NullPointerException.class);

        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }

    @Test
    void createWorkout_WhenExercisesIsNull_ThrowsNullPointerException() {
        CreateWorkoutRequest request = new CreateWorkoutRequest(null, "Pull-Day", OffsetDateTime.now(), OffsetDateTime.now());

        assertThatThrownBy(() -> workoutService.createWorkout(testUser, request))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("exercises");

        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }

    @Test
    void createWorkout_WhenTypeIsNull_ThrowsNullPointerException() {
        CreateWorkoutRequest request = new CreateWorkoutRequest(List.of(), null, OffsetDateTime.now(), OffsetDateTime.now());

        assertThatThrownBy(() -> workoutService.createWorkout(testUser, request))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("type");

        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }

    @Test
    void createWorkout_WhenStartTimeIsNull_ThrowsNullPointerException() {
        CreateWorkoutRequest request = new CreateWorkoutRequest(List.of(), "Pull Day", null, OffsetDateTime.now());

        assertThatThrownBy(() -> workoutService.createWorkout(testUser, request))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("startTime");

        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }

    @Test
    void createWorkout_WhenEndTimeIsNull_ThrowsNullPointerException() {
        CreateWorkoutRequest request = new CreateWorkoutRequest(List.of(), "Pull Day", OffsetDateTime.now(), null);

        assertThatThrownBy(() -> workoutService.createWorkout(testUser, request))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("endTime");

        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }

    @Test
    void createWorkout_WhenValidRequest_CreatesWorkout() {
        // Arrange
        val now = OffsetDateTime.now();

        val workoutType = new WorkoutType("Pull-Day");
        ExerciseRecordRequest exerciseRequest = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), null, 1);
        CreateWorkoutRequest request = new CreateWorkoutRequest(List.of(exerciseRequest), workoutType.getName(), now, now.plusHours(1));

        when(workoutTypeRepository.findByName(workoutType.getName())).thenReturn(Optional.of(workoutType));

        ExerciseRecord mockExerciseRecord = mock(ExerciseRecord.class);
        when(mockExerciseRecord.getEndTime()).thenReturn(now.plusMinutes(10));

        Workout expectedWorkout = new Workout(testUser, request.startTime(), request.endTime(), List.of(mockExerciseRecord), workoutType);

        when(exerciseRecordService.createExerciseRecord(exerciseRequest)).thenReturn(mockExerciseRecord);
        when(workoutRepository.save(any(Workout.class))).thenReturn(expectedWorkout);

        // Act
        Workout result = workoutService.createWorkout(testUser, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getStartTime()).isEqualTo(request.startTime());
        assertThat(result.getEndTime()).isEqualTo(request.endTime());
        assertThat(result.getWorkoutType()).isEqualTo(workoutType);
        assertThat(result.getExercises()).hasSize(1);

        verify(exerciseRecordService).createExerciseRecord(exerciseRequest);
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    void createWorkout_WhenLastRecordEndTimeAfterEndTime_CreatesWorkoutWithEndTimeEqualToLastRecordEndTime() {
        // Arrange
        val now = OffsetDateTime.now();

        val workoutType = new WorkoutType("Pull-Day");
        ExerciseRecordRequest exerciseRequest = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), null, 1);
        CreateWorkoutRequest request = new CreateWorkoutRequest(List.of(exerciseRequest), workoutType.getName(), now, now.plusHours(1));

        when(workoutTypeRepository.findByName(workoutType.getName())).thenReturn(Optional.of(workoutType));

        ExerciseRecord mockExerciseRecord = mock(ExerciseRecord.class);
        when(mockExerciseRecord.getEndTime()).thenReturn(now.plusHours(2));

        Workout expectedWorkout = new Workout(testUser, request.startTime(), request.endTime(), List.of(mockExerciseRecord), workoutType);

        when(exerciseRecordService.createExerciseRecord(exerciseRequest)).thenReturn(mockExerciseRecord);
        when(workoutRepository.save(any(Workout.class))).thenReturn(expectedWorkout);

        // Act
        Workout result = workoutService.createWorkout(testUser, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getStartTime()).isEqualTo(request.startTime());
        assertThat(result.getEndTime()).isEqualTo(mockExerciseRecord.getEndTime());
        assertThat(result.getWorkoutType()).isEqualTo(workoutType);
        assertThat(result.getExercises()).hasSize(1);

        verify(exerciseRecordService).createExerciseRecord(exerciseRequest);
        verify(workoutRepository).save(any(Workout.class));
    }


    @Test
    void createWorkout_WhenExerciseListIsEmpty_ThrowsIllegalArgumentException() {
        // Arrange
        CreateWorkoutRequest request = new CreateWorkoutRequest(List.of(), "Pull-Day", OffsetDateTime.now(), OffsetDateTime.now());

        // Act & Assert
        assertThatThrownBy(() -> workoutService.createWorkout(testUser, request))
                .isInstanceOf(DSIllegalArgumentException.class)
                .hasMessageContaining("Exercises must not be empty");

        verify(workoutRepository, never()).save(any());
        verify(exerciseRecordService, never()).createExerciseRecord(any());
    }
}

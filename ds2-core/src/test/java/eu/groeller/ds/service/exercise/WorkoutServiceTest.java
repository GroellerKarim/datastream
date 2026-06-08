package eu.groeller.ds.service.exercise;

import eu.groeller.ds.domain.exercise.WorkoutStatus;
import eu.groeller.ds.persistence.entity.UserEntity;
import eu.groeller.ds.persistence.entity.exercise.ExerciseDefinitionEntity;
import eu.groeller.ds.persistence.entity.exercise.ExerciseEntryEntity;
import eu.groeller.ds.persistence.entity.exercise.WorkoutEntity;
import eu.groeller.ds.persistence.entity.exercise.WorkoutTypeEntity;
import eu.groeller.ds.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.ds.persistence.exercise.WorkoutRepository;
import eu.groeller.ds.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.ds.domain.exercise.ExerciseType;
import eu.groeller.ds.presentation.request.exercise.CompleteWorkoutRequest;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseEntryRequest;
import eu.groeller.ds.presentation.request.exercise.StartWorkoutRequest;
import eu.groeller.ds.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;
    @Mock
    private WorkoutTypeRepository workoutTypeRepository;
    @Mock
    private ExerciseDefinitionRepository exerciseDefinitionRepository;

    private WorkoutService workoutService;
    private UserEntity testUser;
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        workoutService = new WorkoutService(workoutRepository, workoutTypeRepository, exerciseDefinitionRepository);
        testUser = new UserEntity("testuser", "test@example.com", "kc-test-123");
        now = OffsetDateTime.now();
    }

    @Nested
    class StartWorkout {

        @Test
        void createsActiveWorkout() {
            var workoutType = new WorkoutTypeEntity("Push Day");
            var request = new StartWorkoutRequest("Push Day", now);

            when(workoutTypeRepository.findByName("Push Day")).thenReturn(Optional.of(workoutType));
            when(workoutRepository.save(any(WorkoutEntity.class))).thenAnswer(i -> i.getArgument(0));

            var result = workoutService.startWorkout(testUser, request);

            assertThat(result.getStatus()).isEqualTo(WorkoutStatus.ACTIVE);
            assertThat(result.getUser()).isEqualTo(testUser);
            assertThat(result.getStartTime()).isEqualTo(now);
            verify(workoutRepository).save(any(WorkoutEntity.class));
        }

        @Test
        void whenRequestIsNull_throwsNullPointerException() {
            assertThatThrownBy(() -> workoutService.startWorkout(testUser, null))
                    .isInstanceOf(NullPointerException.class);
            verify(workoutRepository, never()).save(any());
        }

        @Test
        void whenUserIsNull_throwsNullPointerException() {
            var request = new StartWorkoutRequest("Push Day", now);
            assertThatThrownBy(() -> workoutService.startWorkout(null, request))
                    .isInstanceOf(NullPointerException.class);
            verify(workoutRepository, never()).save(any());
        }

        @Test
        void whenWorkoutTypeNotFound_throwsNotFoundException() {
            var request = new StartWorkoutRequest("Nonexistent", now);
            when(workoutTypeRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workoutService.startWorkout(testUser, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Nonexistent");
            verify(workoutRepository, never()).save(any());
        }
    }

    @Nested
    class AddExerciseEntry {

        @Test
        void addsEntryToActiveWorkout() {
            var workoutEntity = buildActiveWorkoutEntity();
            var defEntity = new ExerciseDefinitionEntity("Bench Press", ExerciseType.WEIGHT_REPS);
            var request = new CreateExerciseEntryRequest(1L, 1, now, now.plusMinutes(5),
                    80.0, 10, false, null, null, null, null);

            when(workoutRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(workoutEntity));
            when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(defEntity));
            when(workoutRepository.save(any(WorkoutEntity.class))).thenAnswer(i -> i.getArgument(0));

            var result = workoutService.addExerciseEntry(testUser, 1L, request);

            assertThat(result.getReps()).isEqualTo(10);
            assertThat(result.getWeightKg()).isEqualTo(80.0);
            assertThat(workoutEntity.getEntries()).hasSize(1);
        }

        @Test
        void whenWorkoutNotFound_throwsNotFoundException() {
            var request = new CreateExerciseEntryRequest(1L, 1, now, now.plusMinutes(5),
                    80.0, 10, false, null, null, null, null);
            when(workoutRepository.findByIdAndUser(99L, testUser)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workoutService.addExerciseEntry(testUser, 99L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void whenExerciseDefinitionNotFound_throwsNotFoundException() {
            var workoutEntity = buildActiveWorkoutEntity();
            var request = new CreateExerciseEntryRequest(99L, 1, now, now.plusMinutes(5),
                    80.0, 10, false, null, null, null, null);

            when(workoutRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(workoutEntity));
            when(exerciseDefinitionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workoutService.addExerciseEntry(testUser, 1L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class CompleteWorkout {

        @Test
        void completesActiveWorkout() {
            var workoutEntity = buildActiveWorkoutEntity();
            var defEntity = new ExerciseDefinitionEntity("Bench Press", ExerciseType.WEIGHT_REPS);
            var entryEntity = new ExerciseEntryEntity();
            entryEntity.setExerciseDefinition(defEntity);
            entryEntity.setOrderIndex(1);
            entryEntity.setStartTime(now);
            entryEntity.setEndTime(now.plusMinutes(5));
            entryEntity.setReps(10);
            workoutEntity.getEntries().add(entryEntity);

            var request = new CompleteWorkoutRequest(now.plusHours(1));

            when(workoutRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(workoutEntity));
            when(workoutRepository.save(any(WorkoutEntity.class))).thenAnswer(i -> i.getArgument(0));

            var result = workoutService.completeWorkout(testUser, 1L, request);

            assertThat(result.getStatus()).isEqualTo(WorkoutStatus.COMPLETED);
            assertThat(result.getEndTime()).isEqualTo(now.plusHours(1));
        }

        @Test
        void whenWorkoutNotFound_throwsNotFoundException() {
            var request = new CompleteWorkoutRequest(now.plusHours(1));
            when(workoutRepository.findByIdAndUser(99L, testUser)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workoutService.completeWorkout(testUser, 99L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class CancelWorkout {

        @Test
        void cancelsActiveWorkout() {
            var workoutEntity = buildActiveWorkoutEntity();

            when(workoutRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(workoutEntity));
            when(workoutRepository.save(any(WorkoutEntity.class))).thenAnswer(i -> i.getArgument(0));

            var result = workoutService.cancelWorkout(testUser, 1L);

            assertThat(result.getStatus()).isEqualTo(WorkoutStatus.CANCELLED);
        }
    }

    private WorkoutEntity buildActiveWorkoutEntity() {
        var workoutTypeEntity = new WorkoutTypeEntity("Push Day");
        var entity = new WorkoutEntity();
        entity.setUser(testUser);
        entity.setWorkoutType(workoutTypeEntity);
        entity.setStatus(WorkoutStatus.ACTIVE);
        entity.setStartTime(now);
        return entity;
    }
}

package eu.groeller.ds.service.exercise;

import eu.groeller.ds.domain.exception.InvalidExerciseEntryException;
import eu.groeller.ds.domain.exercise.DistanceUnit;
import eu.groeller.ds.domain.exercise.ExerciseType;
import eu.groeller.ds.domain.exercise.WorkoutStatus;
import eu.groeller.ds.persistence.entity.UserEntity;
import eu.groeller.ds.persistence.entity.exercise.ExerciseDefinitionEntity;
import eu.groeller.ds.persistence.entity.exercise.ExerciseEntryEntity;
import eu.groeller.ds.persistence.entity.exercise.ExerciseSetEntity;
import eu.groeller.ds.persistence.entity.exercise.WorkoutEntity;
import eu.groeller.ds.persistence.entity.exercise.WorkoutTypeEntity;
import eu.groeller.ds.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.ds.persistence.exercise.WorkoutRepository;
import eu.groeller.ds.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.ds.presentation.request.exercise.CompleteWorkoutRequest;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseEntryRequest;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseSetRequest;
import eu.groeller.ds.presentation.request.exercise.StartWorkoutRequest;
import eu.groeller.ds.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
        now = OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);
        var clock = Clock.fixed(now.toInstant(), now.getOffset());
        workoutService = new WorkoutService(workoutRepository, workoutTypeRepository, exerciseDefinitionRepository, clock);
        testUser = new UserEntity("testuser", "test@example.com", "kc-test-123");
    }

    private CreateExerciseSetRequest weightRepsSet(int order, int startMinute) {
        return new CreateExerciseSetRequest(order, now.plusMinutes(startMinute), now.plusMinutes(startMinute + 1),
                80.0, 10, false, null, null, null, null);
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
        void addsEntryWithAllItsSets() {
            var workoutEntity = buildActiveWorkoutEntity();
            var defEntity = new ExerciseDefinitionEntity("Bench Press", ExerciseType.WEIGHT_REPS);
            var request = new CreateExerciseEntryRequest(1L, 1,
                    List.of(weightRepsSet(1, 0), weightRepsSet(2, 2), weightRepsSet(3, 4)));

            when(workoutRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(workoutEntity));
            when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(defEntity));
            when(workoutRepository.save(any(WorkoutEntity.class))).thenAnswer(i -> i.getArgument(0));

            var result = workoutService.addExerciseEntry(testUser, 1L, request);

            assertThat(result.getSets()).hasSize(3);
            assertThat(result.getSets()).extracting(ExerciseSetEntity::getOrderIndex).containsExactly(1, 2, 3);
            assertThat(result.getSets()).allSatisfy(set -> {
                assertThat(set.getReps()).isEqualTo(10);
                assertThat(set.getWeightKg()).isEqualTo(80.0);
                assertThat(set.getEntry()).isSameAs(result);
            });
            assertThat(workoutEntity.getEntries()).hasSize(1);
            assertThat(result.getWorkout()).isSameAs(workoutEntity);
        }

        @Test
        void derivesEntryTimesAndRestFromItsSets() {
            var workoutEntity = buildActiveWorkoutEntity();
            var defEntity = new ExerciseDefinitionEntity("Bench Press", ExerciseType.WEIGHT_REPS);
            var request = new CreateExerciseEntryRequest(1L, 1,
                    List.of(weightRepsSet(1, 0), weightRepsSet(2, 2), weightRepsSet(3, 4)));

            when(workoutRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(workoutEntity));
            when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(defEntity));
            when(workoutRepository.save(any(WorkoutEntity.class))).thenAnswer(i -> i.getArgument(0));

            var result = workoutService.addExerciseEntry(testUser, 1L, request);

            assertThat(result.getStartTime()).isEqualTo(now);
            assertThat(result.getEndTime()).isEqualTo(now.plusMinutes(5));
            assertThat(result.getRestPeriodsMs()).containsExactly(60_000L, 60_000L);
            assertThat(result.getAverageRestMs()).isEqualTo(60_000L);
        }

        @Test
        void narrowsMetricsToTheExerciseType() {
            var workoutEntity = buildActiveWorkoutEntity();
            var defEntity = new ExerciseDefinitionEntity("Running", ExerciseType.DISTANCE);
            // Client sends reps as well; they are not meaningful for a DISTANCE exercise.
            var request = new CreateExerciseEntryRequest(1L, 1, List.of(
                    new CreateExerciseSetRequest(1, now, now.plusHours(1),
                            null, 5, null, null, 10.0, DistanceUnit.KILOMETERS, 3600L)));

            when(workoutRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(workoutEntity));
            when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(defEntity));
            when(workoutRepository.save(any(WorkoutEntity.class))).thenAnswer(i -> i.getArgument(0));

            var result = workoutService.addExerciseEntry(testUser, 1L, request);

            var set = result.getSets().getFirst();
            assertThat(set.getDistance()).isEqualTo(10.0);
            assertThat(set.getDistanceUnit()).isEqualTo(DistanceUnit.KILOMETERS);
            assertThat(set.getDurationSeconds()).isEqualTo(3600L);
            assertThat(set.getReps()).isNull();
        }

        @Test
        void whenSetsAreEmpty_throwsDomainException() {
            var workoutEntity = buildActiveWorkoutEntity();
            var defEntity = new ExerciseDefinitionEntity("Bench Press", ExerciseType.WEIGHT_REPS);
            var request = new CreateExerciseEntryRequest(1L, 1, List.of());

            when(workoutRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(workoutEntity));
            when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(defEntity));

            assertThatThrownBy(() -> workoutService.addExerciseEntry(testUser, 1L, request))
                    .isInstanceOf(InvalidExerciseEntryException.class)
                    .hasMessageContaining("at least one set");
            verify(workoutRepository, never()).save(any());
        }

        @Test
        void whenWorkoutNotFound_throwsNotFoundException() {
            var request = new CreateExerciseEntryRequest(1L, 1, List.of(weightRepsSet(1, 0)));
            when(workoutRepository.findByIdAndUser(99L, testUser)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workoutService.addExerciseEntry(testUser, 99L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void whenExerciseDefinitionNotFound_throwsNotFoundException() {
            var workoutEntity = buildActiveWorkoutEntity();
            var request = new CreateExerciseEntryRequest(99L, 1, List.of(weightRepsSet(1, 0)));

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
            workoutEntity.addEntry(buildEntryEntity());

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
        void cancelsActiveWorkoutUsingTheInjectedClock() {
            var workoutEntity = buildActiveWorkoutEntity();

            when(workoutRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(workoutEntity));
            when(workoutRepository.save(any(WorkoutEntity.class))).thenAnswer(i -> i.getArgument(0));

            var result = workoutService.cancelWorkout(testUser, 1L);

            assertThat(result.getStatus()).isEqualTo(WorkoutStatus.CANCELLED);
            assertThat(result.getEndTime()).isEqualTo(now);
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

    private ExerciseEntryEntity buildEntryEntity() {
        var entryEntity = new ExerciseEntryEntity();
        entryEntity.setExerciseDefinition(new ExerciseDefinitionEntity("Bench Press", ExerciseType.WEIGHT_REPS));
        entryEntity.setOrderIndex(1);

        var setEntity = new ExerciseSetEntity();
        setEntity.setOrderIndex(1);
        setEntity.setStartTime(now);
        setEntity.setEndTime(now.plusMinutes(5));
        setEntity.setReps(10);
        setEntity.setWeightKg(80.0);
        entryEntity.addSet(setEntity);

        return entryEntity;
    }
}

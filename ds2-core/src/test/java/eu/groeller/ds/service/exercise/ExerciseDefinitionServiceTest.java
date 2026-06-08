package eu.groeller.ds.service.exercise;

import eu.groeller.ds.domain.exercise.ExerciseType;
import eu.groeller.ds.persistence.entity.exercise.ExerciseDefinitionEntity;
import eu.groeller.ds.persistence.entity.exercise.WorkoutTypeEntity;
import eu.groeller.ds.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.ds.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.ds.presentation.request.exercise.CreateExerciseDefinitionRequest;
import eu.groeller.ds.presentation.request.exercise.CreateWorkoutTypeRequest;
import eu.groeller.ds.service.exception.DuplicateResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseDefinitionServiceTest {

    @Mock
    private ExerciseDefinitionRepository exerciseDefinitionRepository;
    @Mock
    private WorkoutTypeRepository workoutTypeRepository;

    private ExerciseDefinitionService service;

    @BeforeEach
    void setUp() {
        service = new ExerciseDefinitionService(exerciseDefinitionRepository, workoutTypeRepository);
    }

    @Nested
    class CreateExerciseDefinition {

        @Test
        void createsSuccessfully() {
            var request = new CreateExerciseDefinitionRequest("Bench Press", ExerciseType.WEIGHT_REPS);
            var expected = new ExerciseDefinitionEntity("Bench Press", ExerciseType.WEIGHT_REPS);

            when(exerciseDefinitionRepository.existsByNameAndType("Bench Press", ExerciseType.WEIGHT_REPS))
                    .thenReturn(false);
            when(exerciseDefinitionRepository.save(any(ExerciseDefinitionEntity.class))).thenReturn(expected);

            var result = service.createExerciseDefinition(request);

            assertThat(result.getName()).isEqualTo("Bench Press");
            assertThat(result.getType()).isEqualTo(ExerciseType.WEIGHT_REPS);
            verify(exerciseDefinitionRepository).save(any(ExerciseDefinitionEntity.class));
        }

        @Test
        void whenDuplicate_throwsException() {
            var request = new CreateExerciseDefinitionRequest("Bench Press", ExerciseType.WEIGHT_REPS);
            when(exerciseDefinitionRepository.existsByNameAndType("Bench Press", ExerciseType.WEIGHT_REPS))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.createExerciseDefinition(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("already exists");
            verify(exerciseDefinitionRepository, never()).save(any());
        }

        @Test
        void whenNullRequest_throwsException() {
            assertThatThrownBy(() -> service.createExerciseDefinition(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void whenNullName_throwsException() {
            var request = new CreateExerciseDefinitionRequest(null, ExerciseType.WEIGHT_REPS);
            assertThatThrownBy(() -> service.createExerciseDefinition(request))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name");
        }

        @Test
        void whenBlankName_throwsException() {
            var request = new CreateExerciseDefinitionRequest("   ", ExerciseType.WEIGHT_REPS);
            assertThatThrownBy(() -> service.createExerciseDefinition(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name");
        }
    }

    @Nested
    class CreateWorkoutType {

        @Test
        void createsSuccessfully() {
            var request = new CreateWorkoutTypeRequest("Push Day");
            var expected = new WorkoutTypeEntity("Push Day");

            when(workoutTypeRepository.existsByName("Push Day")).thenReturn(false);
            when(workoutTypeRepository.save(any(WorkoutTypeEntity.class))).thenReturn(expected);

            var result = service.createWorkoutType(request);

            assertThat(result.getName()).isEqualTo("Push Day");
            verify(workoutTypeRepository).save(any(WorkoutTypeEntity.class));
        }

        @Test
        void whenDuplicate_throwsException() {
            var request = new CreateWorkoutTypeRequest("Push Day");
            when(workoutTypeRepository.existsByName("Push Day")).thenReturn(true);

            assertThatThrownBy(() -> service.createWorkoutType(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("already exists");
        }

        @Test
        void whenNullRequest_throwsException() {
            assertThatThrownBy(() -> service.createWorkoutType(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}

package eu.groeller.datastreamserver.service.exercise;

import eu.groeller.datastreamserver.domain.exercise.ExerciseDefinition;
import eu.groeller.datastreamserver.domain.exercise.ExerciseType;
import eu.groeller.datastreamserver.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.datastreamserver.persistence.exercise.WorkoutTypeRepository;
import eu.groeller.datastreamserver.presentation.request.exercise.CreateExerciseDefinitionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseDefinitionServiceTest {

    @Mock
    private ExerciseDefinitionRepository exerciseDefinitionRepository;

    @Mock
    private WorkoutTypeRepository workoutTypeRepository;

    private ExerciseDefinitionService exerciseDefinitionService;

    @BeforeEach
    void setUp() {
        exerciseDefinitionService = new ExerciseDefinitionService(exerciseDefinitionRepository, workoutTypeRepository);
    }

    @Test
    void createExerciseDefinition_Success() {
        // Arrange
        CreateExerciseDefinitionRequest request = new CreateExerciseDefinitionRequest("Bench Press", ExerciseType.SETS_REPS);
        ExerciseDefinition expectedDefinition = new ExerciseDefinition("Bench Press", ExerciseType.SETS_REPS);
        
        when(exerciseDefinitionRepository.existsByName("Bench Press")).thenReturn(false);
        when(exerciseDefinitionRepository.save(any(ExerciseDefinition.class))).thenReturn(expectedDefinition);

        // Act
        ExerciseDefinition result = exerciseDefinitionService.createExerciseDefinition(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Bench Press");
        assertThat(result.getType()).isEqualTo(ExerciseType.SETS_REPS);
        
        verify(exerciseDefinitionRepository).existsByName("Bench Press");
        verify(exerciseDefinitionRepository).save(any(ExerciseDefinition.class));
    }

    @Test
    void createExerciseDefinition_NullRequest_ThrowsException() {
        // Act & Assert
        assertThatThrownBy(() -> exerciseDefinitionService.createExerciseDefinition(null))
                .isInstanceOf(NullPointerException.class);
        
        verify(exerciseDefinitionRepository, never()).existsByName(any());
        verify(exerciseDefinitionRepository, never()).save(any());
    }

    @Test
    void createExerciseDefinition_NullName_ThrowsException() {
        // Arrange
        CreateExerciseDefinitionRequest request = new CreateExerciseDefinitionRequest(null, ExerciseType.SETS_REPS);

        // Act & Assert
        assertThatThrownBy(() -> exerciseDefinitionService.createExerciseDefinition(request))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("name");
        
        verify(exerciseDefinitionRepository, never()).existsByName(any());
        verify(exerciseDefinitionRepository, never()).save(any());
    }

    @Test
    void createExerciseDefinition_BlankName_ThrowsException() {
        // Arrange
        CreateExerciseDefinitionRequest request = new CreateExerciseDefinitionRequest("   ", ExerciseType.SETS_REPS);

        // Act & Assert
        assertThatThrownBy(() -> exerciseDefinitionService.createExerciseDefinition(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
        
        verify(exerciseDefinitionRepository, never()).existsByName(any());
        verify(exerciseDefinitionRepository, never()).save(any());
    }

    @Test
    void createExerciseDefinition_NullType_ThrowsException() {
        // Arrange
        CreateExerciseDefinitionRequest request = new CreateExerciseDefinitionRequest("Bench Press", null);

        // Act & Assert
        assertThatThrownBy(() -> exerciseDefinitionService.createExerciseDefinition(request))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("type");
        
        verify(exerciseDefinitionRepository, never()).existsByName(any());
        verify(exerciseDefinitionRepository, never()).save(any());
    }

    @Test
    void createExerciseDefinition_DuplicateName_ThrowsException() {
        // Arrange
        CreateExerciseDefinitionRequest request = new CreateExerciseDefinitionRequest("Bench Press", ExerciseType.SETS_REPS);
        when(exerciseDefinitionRepository.existsByName("Bench Press")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> exerciseDefinitionService.createExerciseDefinition(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
        
        verify(exerciseDefinitionRepository).existsByName("Bench Press");
        verify(exerciseDefinitionRepository, never()).save(any());
    }
}

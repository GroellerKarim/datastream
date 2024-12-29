package eu.groeller.datastreamserver.service.exercise;

import eu.groeller.datastreamserver.domain.exercise.*;
import eu.groeller.datastreamserver.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.datastreamserver.persistence.exercise.ExerciseRecordRepository;
import eu.groeller.datastreamserver.presentation.request.exercise.ExerciseRecordDetailsRequest;
import eu.groeller.datastreamserver.presentation.request.exercise.ExerciseRecordRequest;
import eu.groeller.datastreamserver.presentation.request.exercise.ExerciseSetRequest;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExerciseRecordServiceTest {

    @Mock
    private ExerciseDefinitionRepository exerciseDefinitionRepository;

    @Mock
    private ExerciseRecordRepository exerciseRecordRepository;

    private ExerciseRecordService exerciseRecordService;

    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        exerciseRecordService = new ExerciseRecordService(exerciseDefinitionRepository, exerciseRecordRepository);
        now = OffsetDateTime.now();
    }

    @Test
    void createExerciseRecord_WhenRequestIsNull_ThrowsNullPointerException() {
        assertThatThrownBy(() -> exerciseRecordService.createExerciseRecord(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void createExerciseRecord_WhenExerciseDefinitionNotFound_ThrowsIllegalArgumentException() {
        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), null, 0);
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseRecordService.createExerciseRecord(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Exercise definition not found");
    }

    @Test
    void createExerciseRecord_WhenDistanceExercise_CreatesDistanceRecord() {
        // Arrange
        ExerciseDefinition definition = new ExerciseDefinition("Running", ExerciseType.DISTANCE);
        val details = new ExerciseRecordDetailsRequest(
                75.0,  // distance
                DistanceUnit.KILOMETERS,
                null,  // distancePerUnit
                null,  // sets
                10.0   // weightKg
        );
        
        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), details, 1);
        
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        // Act
        ExerciseRecord result = exerciseRecordService.createExerciseRecord(request);

        // Assert
        assertThat(result).isInstanceOf(DistanceExerciseRecord.class);
        DistanceExerciseRecord distanceRecord = (DistanceExerciseRecord) result;
        assertThat(distanceRecord.getDistance()).isEqualTo(75.0);
        assertThat(distanceRecord.getDistanceUnit()).isEqualTo(DistanceUnit.KILOMETERS);
        assertThat(distanceRecord.getWeightKg()).isEqualTo(10.0);
    }

    @Test
    void createExerciseRecord_WhenSetsRepsExercise_CreatesSetBasedRecord() {
        // Arrange
        ExerciseDefinition definition = new ExerciseDefinition("Bench Press", ExerciseType.SETS_REPS);
        List<ExerciseSetRequest> setRequests = List.of(
            new ExerciseSetRequest(now, now.plusMinutes(1), false, 12, 1.0, 0)
        );
        
        val details = new ExerciseRecordDetailsRequest(
            null,  // distance
            null,  // distanceUnit
            null,  // distancePerUnit
            setRequests,
            75.0   // weightKg
        );
        
        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), details, 1);
        
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        // Act
        ExerciseRecord result = exerciseRecordService.createExerciseRecord(request);

        // Assert
        assertThat(result).isInstanceOf(SetBasedExerciseRecord.class);
        SetBasedExerciseRecord setRecord = (SetBasedExerciseRecord) result;
        assertThat(setRecord.getSets().getFirst()).isEqualTo(75.0);
        assertThat(setRecord.getSets()).hasSize(1);
        assertThat(setRecord.getSets().getFirst().getRepetitions()).isEqualTo(12);
    }

    @Test
    void createExerciseRecord_WhenDistanceExerciseWithMissingDetails_ThrowsNullPointerException() {
        ExerciseDefinition definition = new ExerciseDefinition("Running", ExerciseType.DISTANCE);
        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), null, 1);
        
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        assertThatThrownBy(() -> exerciseRecordService.createExerciseRecord(request))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void createExerciseRecord_WhenDistanceExerciseWithMissingRequiredFields_ThrowsNullPointerException() {
        ExerciseDefinition definition = new ExerciseDefinition("Running", ExerciseType.DISTANCE);
        val details = new ExerciseRecordDetailsRequest(
            null,  // distance - required but missing
            null,  // distanceUnit - required but missing
            null,  // distancePerUnit
            null,  // sets
            75.0   // weightKg
        );
        
        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), details, 1);
        
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        assertThatThrownBy(() -> exerciseRecordService.createExerciseRecord(request))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void createExerciseRecord_WhenSetBasedExerciseWithInvalidSet_ThrowsNullPointerException() {
        ExerciseDefinition definition = new ExerciseDefinition("Bench Press", ExerciseType.SETS_REPS);
        List<ExerciseSetRequest> setRequests = List.of(
            new ExerciseSetRequest(now, now.plusMinutes(1), null, null,0.0, 0) // missing required fields
        );
        
        val details = new ExerciseRecordDetailsRequest(
            null,  // distance
            null,  // distanceUnit
            null,  // distancePerUnit
            setRequests,
            75.0   // weightKg
        );
        
        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), details, 1);
        
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        assertThatThrownBy(() -> exerciseRecordService.createExerciseRecord(request))
                .isInstanceOf(NullPointerException.class);
    }
}

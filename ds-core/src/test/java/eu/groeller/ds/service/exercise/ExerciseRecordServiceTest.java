package eu.groeller.ds.service.exercise;

import eu.groeller.ds.domain.exercise.*;
import eu.groeller.ds.persistence.exercise.ExerciseDefinitionRepository;
import eu.groeller.ds.persistence.exercise.ExerciseRecordRepository;
import eu.groeller.ds.presentation.request.exercise.ExerciseRecordDetailsRequest;
import eu.groeller.ds.presentation.request.exercise.ExerciseRecordRequest;
import eu.groeller.ds.presentation.request.exercise.ExerciseSetRequest;
import eu.groeller.ds.service.exceptions.DSIllegalArgumentException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
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
                .isInstanceOf(DSIllegalArgumentException.class)
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
                10.0   // weight
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
                new ExerciseSetRequest(now, now.plusMinutes(1), false, 12, 0, 50.0, 0)
        );

        val details = new ExerciseRecordDetailsRequest(
                null,  // distance
                null,  // distanceUnit
                null,  // distancePerUnit
                setRequests,
                75.0   // weight
        );

        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), details, 1);

        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        // Act
        ExerciseRecord result = exerciseRecordService.createExerciseRecord(request);

        // Assert
        assertThat(result).isInstanceOf(SetBasedExerciseRecord.class);
        SetBasedExerciseRecord setRecord = (SetBasedExerciseRecord) result;
        assertThat(setRecord.getSets().getFirst().getWeightKg()).isEqualTo(50.0);
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
                75.0   // weight
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
                new ExerciseSetRequest(now, now.plusMinutes(1), null, null, 0, 0.0, 0) // missing required fields
        );

        val details = new ExerciseRecordDetailsRequest(
                null,  // distance
                null,  // distanceUnit
                null,  // distancePerUnit
                setRequests,
                75.0   // weight
        );

        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), details, 1);

        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        assertThatThrownBy(() -> exerciseRecordService.createExerciseRecord(request))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void createExerciseRecord_WhenFailureFalseAndPartialsGreaterThanZero_SetsPartialsToNull() {
        // Arrange
        ExerciseDefinition definition = new ExerciseDefinition("Bench Press", ExerciseType.SETS_REPS);
        List<ExerciseSetRequest> setRequests = List.of(
                new ExerciseSetRequest(now, now.plusMinutes(1), false, 12, 2, 50.0, 0)
        );

        val details = new ExerciseRecordDetailsRequest(
                null, null, null, setRequests, 75.0
        );
        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), details, 1);
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        // Act
        ExerciseRecord result = exerciseRecordService.createExerciseRecord(request);

        // Assert
        assertThat(result).isInstanceOf(SetBasedExerciseRecord.class);
        SetBasedExerciseRecord setRecord = (SetBasedExerciseRecord) result;
        assertThat(setRecord.getSets().getFirst().getPartialRepetitions()).isNull();
    }

    @Test
    void createExerciseRecord_WhenFailureFalseAndPartialsNull_KeepsPartialsNull() {
        ExerciseDefinition definition = new ExerciseDefinition("Bench Press", ExerciseType.SETS_REPS);
        List<ExerciseSetRequest> setRequests = List.of(
                new ExerciseSetRequest(now, now.plusMinutes(1), false, 12, null, 50.0, 0)
        );

        val details = new ExerciseRecordDetailsRequest(
                null, null, null, setRequests, 75.0
        );
        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), details, 1);
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        ExerciseRecord result = exerciseRecordService.createExerciseRecord(request);

        assertThat(result).isInstanceOf(SetBasedExerciseRecord.class);
        SetBasedExerciseRecord setRecord = (SetBasedExerciseRecord) result;
        assertThat(setRecord.getSets().getFirst().getPartialRepetitions()).isNull();
    }

    @Test
    void createExerciseRecord_WhenFailureFalseAndPartialsZero_KeepsPartialsNull() {
        ExerciseDefinition definition = new ExerciseDefinition("Bench Press", ExerciseType.SETS_REPS);
        List<ExerciseSetRequest> setRequests = List.of(
                new ExerciseSetRequest(now, now.plusMinutes(1), false, 12, 0, 50.0, 0)
        );

        val details = new ExerciseRecordDetailsRequest(
                null, null, null, setRequests, 75.0
        );
        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), details, 1);
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        ExerciseRecord result = exerciseRecordService.createExerciseRecord(request);

        assertThat(result).isInstanceOf(SetBasedExerciseRecord.class);
        SetBasedExerciseRecord setRecord = (SetBasedExerciseRecord) result;
        assertThat(setRecord.getSets().getFirst().getPartialRepetitions()).isNull();
    }

    @Test
    void createExerciseRecord_WhenFailureTrueAndPartialsNull_KeepsPartialsNull() {
        ExerciseDefinition definition = new ExerciseDefinition("Bench Press", ExerciseType.SETS_REPS);
        List<ExerciseSetRequest> setRequests = List.of(
                new ExerciseSetRequest(now, now.plusMinutes(1), true, 12, null, 50.0, 0)
        );

        val details = new ExerciseRecordDetailsRequest(
                null, null, null, setRequests, 75.0
        );
        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), details, 1);
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        ExerciseRecord result = exerciseRecordService.createExerciseRecord(request);

        assertThat(result).isInstanceOf(SetBasedExerciseRecord.class);
        SetBasedExerciseRecord setRecord = (SetBasedExerciseRecord) result;
        assertThat(setRecord.getSets().getFirst().getPartialRepetitions()).isNull();
    }

    @Test
    void createExerciseRecord_WhenFailureTrueAndPartialsGreaterThanZero_KeepsPartials() {
        ExerciseDefinition definition = new ExerciseDefinition("Bench Press", ExerciseType.SETS_REPS);
        List<ExerciseSetRequest> setRequests = List.of(
                new ExerciseSetRequest(now, now.plusMinutes(1), true, 12, 2, 50.0, 0)
        );

        val details = new ExerciseRecordDetailsRequest(
                null, null, null, setRequests, 75.0
        );
        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), details, 1);
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        ExerciseRecord result = exerciseRecordService.createExerciseRecord(request);

        assertThat(result).isInstanceOf(SetBasedExerciseRecord.class);
        SetBasedExerciseRecord setRecord = (SetBasedExerciseRecord) result;
        assertThat(setRecord.getSets().getFirst().getPartialRepetitions()).isEqualTo(2);
    }

    @Test
    void createExerciseRecord_WhenMultipleSets_OrdersSetsAndSetsCorrectStartEndTimes() {
        // Arrange
        ExerciseDefinition definition = new ExerciseDefinition("Bench Press", ExerciseType.SETS_REPS);

        // Create sets in random order
        List<ExerciseSetRequest> setRequests = List.of(
                new ExerciseSetRequest(now.plusMinutes(2), now.plusMinutes(3), false, 12, null, 50.0, 2),
                new ExerciseSetRequest(now, now.plusMinutes(1), false, 12, null, 50.0, 0),
                new ExerciseSetRequest(now.plusMinutes(4), now.plusMinutes(5), false, 12, null, 50.0, 3),
                new ExerciseSetRequest(now.plusMinutes(1), now.plusMinutes(2), false, 12, null, 50.0, 1)
        );

        val details = new ExerciseRecordDetailsRequest(
                null, null, null, setRequests, 75.0
        );
        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, now, now.plusMinutes(30), details, 1);
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        // Act
        ExerciseRecord result = exerciseRecordService.createExerciseRecord(request);

        // Assert
        assertThat(result).isInstanceOf(SetBasedExerciseRecord.class);
        SetBasedExerciseRecord setRecord = (SetBasedExerciseRecord) result;

        // Verify sets are ordered correctly
        List<ExerciseSet> orderedSets = setRecord.getSets();
        assertThat(orderedSets).hasSize(4);
        for (int i = 0; i < orderedSets.size(); i++) {
            assertThat(orderedSets.get(i).getOrderIndex()).isEqualTo(i);
        }

        // Verify start time is from first set
        assertThat(setRecord.getStartTime()).isEqualTo(now);

        // Verify end time is from last set
        assertThat(setRecord.getEndTime()).isEqualTo(now.plusMinutes(5));
    }

    @Test
    void createExerciseRecord_WhenDistanceExercise_SetsCorrectStartEndTimes() {
        // Arrange
        ExerciseDefinition definition = new ExerciseDefinition("Running", ExerciseType.DISTANCE);
        val startTime = now;
        val endTime = now.plusHours(1);  // 1 hour run

        val details = new ExerciseRecordDetailsRequest(
                10.0,  // distance
                DistanceUnit.KILOMETERS,
                null,  // distancePerUnit
                null,  // sets
                null   // weight
        );

        ExerciseRecordRequest request = new ExerciseRecordRequest(1L, startTime, endTime, details, 1);
        when(exerciseDefinitionRepository.findById(1L)).thenReturn(Optional.of(definition));

        // Act
        ExerciseRecord result = exerciseRecordService.createExerciseRecord(request);

        // Assert
        assertThat(result).isInstanceOf(DistanceExerciseRecord.class);
        DistanceExerciseRecord distanceRecord = (DistanceExerciseRecord) result;

        // Verify start and end times are set correctly
        assertThat(distanceRecord.getStartTime()).isEqualTo(startTime);
        assertThat(distanceRecord.getEndTime()).isEqualTo(endTime);

        // Verify duration is calculated correctly (in milliseconds)
        assertThat(distanceRecord.getDuration()).isEqualTo(Duration.between(startTime, endTime).toMillis());
    }
}

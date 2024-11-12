package eu.groeller.datastreamserver.presentation.response.exercise;

import eu.groeller.datastreamserver.domain.exercise.DistanceExerciseRecord;
import eu.groeller.datastreamserver.domain.exercise.DistanceUnit;
import eu.groeller.datastreamserver.domain.exercise.ExerciseRecord;
import eu.groeller.datastreamserver.domain.exercise.SetBasedExerciseRecord;
import lombok.Getter;

import java.util.List;

@Getter
public class ExerciseRecordDetailsResponse {
    // For DistanceExerciseRecord
    private Double distance = null;
    private DistanceUnit distanceUnit = null;

    // For SetBasedExerciseRecord
    private List<ExerciseSetResponse> sets = null;

    // Common for both
    private Double weightKg = null;

    public ExerciseRecordDetailsResponse(ExerciseRecord record) {
        switch (record) {
            case DistanceExerciseRecord distanceRecord -> {
                this.distance = distanceRecord.getDistance();
                this.distanceUnit = distanceRecord.getDistanceUnit();
                this.weightKg = distanceRecord.getWeightKg();
            }
            case SetBasedExerciseRecord setBasedRecord -> {
                this.weightKg = setBasedRecord.getWeightKg();
                this.sets = setBasedRecord.getSets().stream().map(ExerciseSetResponse::new).toList();
            }
            default -> throw new IllegalStateException("Unexpected exercise class type: " + record.getClass());
        }
    }
}

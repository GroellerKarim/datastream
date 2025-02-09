package eu.groeller.ds.presentation.response.exercise;

import eu.groeller.ds.domain.exercise.DistanceExerciseRecord;
import eu.groeller.ds.domain.exercise.DistanceUnit;
import eu.groeller.ds.domain.exercise.ExerciseRecord;
import eu.groeller.ds.domain.exercise.SetBasedExerciseRecord;
import lombok.Getter;

import java.util.List;

@Getter
public class ExerciseRecordDetailsResponse {
    // For DistanceExerciseRecord
    private Double distance = null;
    private DistanceUnit distanceUnit = null;

    // For SetBasedExerciseRecord
    private List<ExerciseSetResponse> sets = null;

    // Only for DistanceExerciseRecord
    private Double weightKg = null;

    public ExerciseRecordDetailsResponse(ExerciseRecord record) {
        switch (record) {
            case DistanceExerciseRecord distanceRecord -> {
                this.distance = distanceRecord.getDistance();
                this.distanceUnit = distanceRecord.getDistanceUnit();
                this.weightKg = distanceRecord.getWeightKg();
            }
            case SetBasedExerciseRecord setBasedRecord -> {
                this.sets = setBasedRecord.getSets().stream()
                        .map(ExerciseSetResponse::new)
                        .toList();
            }
            default -> throw new IllegalStateException("Unexpected exercise class type: " + record.getClass());
        }
    }
}

package eu.groeller.ds.domain.exercise;

import eu.groeller.ds.domain.exception.InvalidExerciseEntryException;

/**
 * What was actually measured during a single set.
 * <p>
 * The three variants are exhaustive over {@link ExerciseType}, so the compiler — not a
 * hand-maintained {@code switch} — enforces that every exercise type is handled. Each variant
 * declares exactly the fields that are meaningful for it, plus {@code weightKg}, which is
 * optional for all three (a weighted plank, a weighted carry, a loaded barbell).
 * <p>
 * The optional accessors below default to {@code null}; a variant that declares the matching
 * record component overrides its accessor automatically. That is what lets a set be flattened
 * into database columns and wire fields without any type dispatch at the boundary — the single
 * dispatch lives in {@link #of}.
 */
public sealed interface SetMetrics {

    /** The exercise type these metrics belong to. */
    ExerciseType type();

    /** Optional for every variant: external load carried during the set. */
    Double weightKg();

    default Integer reps() {
        return null;
    }

    default Boolean failure() {
        return null;
    }

    default Integer partialReps() {
        return null;
    }

    default Double distance() {
        return null;
    }

    default DistanceUnit distanceUnit() {
        return null;
    }

    default Long durationSeconds() {
        return null;
    }

    record WeightReps(Double weightKg, Integer reps, Boolean failure, Integer partialReps) implements SetMetrics {

        public WeightReps {
            if (reps == null) {
                throw InvalidExerciseEntryException.missingRequiredField("reps", ExerciseType.WEIGHT_REPS);
            }
            // Partial reps only mean anything for a set taken to failure. Normalising rather than
            // rejecting keeps a client that always sends the field from being punished for it.
            partialReps = (Boolean.TRUE.equals(failure) && partialReps != null && partialReps > 0)
                    ? partialReps : null;
        }

        @Override
        public ExerciseType type() {
            return ExerciseType.WEIGHT_REPS;
        }
    }

    /**
     * {@code durationSeconds} is kept because pace is intrinsic to a distance effort. Reps are not
     * modelled here: repeated efforts (5x400m) are separate sets of the same entry.
     */
    record Distance(Double weightKg, Double distance, DistanceUnit distanceUnit,
                    Long durationSeconds) implements SetMetrics {

        public Distance {
            if (distance == null) {
                throw InvalidExerciseEntryException.missingRequiredField("distance", ExerciseType.DISTANCE);
            }
            if (distanceUnit == null) {
                throw InvalidExerciseEntryException.missingRequiredField("distanceUnit", ExerciseType.DISTANCE);
            }
        }

        @Override
        public ExerciseType type() {
            return ExerciseType.DISTANCE;
        }
    }

    record Timed(Double weightKg, Long durationSeconds) implements SetMetrics {

        public Timed {
            if (durationSeconds == null) {
                throw InvalidExerciseEntryException.missingRequiredField("durationSeconds", ExerciseType.TIMED);
            }
        }

        @Override
        public ExerciseType type() {
            return ExerciseType.TIMED;
        }
    }

    /**
     * The single point where the flat field set used on the wire and in the database is narrowed
     * to the variant for {@code type}. Fields that are not meaningful for the type are dropped.
     */
    static SetMetrics of(ExerciseType type,
                         Double weightKg,
                         Integer reps,
                         Boolean failure,
                         Integer partialReps,
                         Double distance,
                         DistanceUnit distanceUnit,
                         Long durationSeconds) {
        return switch (type) {
            case WEIGHT_REPS -> new WeightReps(weightKg, reps, failure, partialReps);
            case DISTANCE -> new Distance(weightKg, distance, distanceUnit, durationSeconds);
            case TIMED -> new Timed(weightKg, durationSeconds);
        };
    }

    static SetMetrics fromData(ExerciseSetData data, ExerciseType type) {
        return of(type,
                data.getWeightKg(),
                data.getReps(),
                data.getFailure(),
                data.getPartialReps(),
                data.getDistance(),
                data.getDistanceUnit(),
                data.getDurationSeconds());
    }
}

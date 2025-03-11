package eu.groeller.dsui.data.mapper

import eu.groeller.datastreamui.data.model.ExerciseRecordResponse
import eu.groeller.datastreamui.data.model.ExerciseDefinition as DataExerciseDefinition
import eu.groeller.datastreamui.data.model.ExerciseRecordDetailsResponse
import eu.groeller.datastreamui.data.model.ExerciseSetResponse
import eu.groeller.datastreamui.data.model.ExerciseType as DataExerciseType
import eu.groeller.datastreamui.data.model.DistanceUnit as DataDistanceUnit
import eu.groeller.datastreamui.data.model.CreateExerciseRequest
import eu.groeller.datastreamui.data.model.CreateExerciseDetailsRequest
import eu.groeller.datastreamui.data.model.CreateExerciseSetRequest
import eu.groeller.dsui.domain.model.Exercise
import eu.groeller.dsui.domain.model.ExerciseDefinition
import eu.groeller.dsui.domain.model.ExerciseDetails
import eu.groeller.dsui.domain.model.ExerciseSet
import eu.groeller.dsui.domain.model.ExerciseTypeEnum
import eu.groeller.dsui.domain.model.DistanceUnitEnum

/**
 * Mapper for converting between domain Exercise models and data layer exercise models.
 */
object ExerciseMapper {
    
    /**
     * Converts a data layer ExerciseRecordResponse to a domain Exercise model.
     */
    fun ExerciseRecordResponse.toDomain(): Exercise {
        return Exercise(
            id = exerciseRecordId,
            exerciseDefinitionId = exerciseDefinitionId,
            exerciseName = exerciseName,
            type = type.toDomain(),
            startTime = startTime,
            endTime = endTime,
            details = details.toDomain(),
            orderIndex = orderIndex
        )
    }
    
    /**
     * Converts a data layer ExerciseDefinition to a domain ExerciseDefinition model.
     */
    fun DataExerciseDefinition.toDomain(): ExerciseDefinition {
        return ExerciseDefinition(
            id = id,
            name = name,
            type = type.toDomain()
        )
    }
    
    /**
     * Converts a data layer ExerciseRecordDetailsResponse to a domain ExerciseDetails model.
     */
    fun ExerciseRecordDetailsResponse.toDomain(): ExerciseDetails {
        return ExerciseDetails(
            distance = distance,
            distanceUnit = distanceUnit?.toDomain(),
            sets = sets?.map { it.toDomain() },
            weightKg = weightKg
        )
    }
    
    /**
     * Converts a data layer ExerciseSetResponse to a domain ExerciseSet model.
     */
    fun ExerciseSetResponse.toDomain(): ExerciseSet {
        return ExerciseSet(
            startTime = startTime,
            endTime = endTime,
            failure = failure,
            weightKg = weightKg,
            repetitions = repetitions
        )
    }
    
    /**
     * Converts a data layer ExerciseType to a domain ExerciseTypeEnum.
     */
    fun DataExerciseType.toDomain(): ExerciseTypeEnum {
        return when (this) {
            DataExerciseType.SETS_REPS -> ExerciseTypeEnum.SETS_REPS
            DataExerciseType.DISTANCE -> ExerciseTypeEnum.DISTANCE
            DataExerciseType.SETS_TIME -> ExerciseTypeEnum.SETS_TIME
        }
    }
    
    /**
     * Converts a data layer DistanceUnit to a domain DistanceUnitEnum.
     */
    fun DataDistanceUnit.toDomain(): DistanceUnitEnum {
        return when (this) {
            DataDistanceUnit.METERS -> DistanceUnitEnum.METERS
            DataDistanceUnit.KILOMETERS -> DistanceUnitEnum.KILOMETERS
            DataDistanceUnit.MILES -> DistanceUnitEnum.MILES
        }
    }
    
    /**
     * Converts a domain ExerciseTypeEnum to a data layer ExerciseType.
     */
    fun ExerciseTypeEnum.toData(): DataExerciseType {
        return when (this) {
            ExerciseTypeEnum.SETS_REPS -> DataExerciseType.SETS_REPS
            ExerciseTypeEnum.DISTANCE -> DataExerciseType.DISTANCE
            ExerciseTypeEnum.SETS_TIME -> DataExerciseType.SETS_TIME
        }
    }
    
    /**
     * Converts a domain DistanceUnitEnum to a data layer DistanceUnit.
     */
    fun DistanceUnitEnum.toData(): DataDistanceUnit {
        return when (this) {
            DistanceUnitEnum.METERS -> DataDistanceUnit.METERS
            DistanceUnitEnum.KILOMETERS -> DataDistanceUnit.KILOMETERS
            DistanceUnitEnum.MILES -> DataDistanceUnit.MILES
        }
    }
    
    /**
     * Converts a domain Exercise to a CreateExerciseRequest for the data layer.
     */
    fun Exercise.toCreateRequestExercise(): CreateExerciseRequest {
        return CreateExerciseRequest(
            exerciseDefinitionId = exerciseDefinitionId,
            startTime = startTime,
            endTime = endTime,
            details = toCreateExerciseDetailsRequest(),
            orderIndex = orderIndex
        )
    }
    
    /**
     * Converts a domain Exercise's details to a CreateExerciseDetailsRequest.
     */
    private fun Exercise.toCreateExerciseDetailsRequest(): CreateExerciseDetailsRequest {
        return CreateExerciseDetailsRequest(
            distance = details.distance,
            distanceUnit = details.distanceUnit?.toData(),
            sets = details.sets?.map { it.toCreateExerciseSetRequest() },
            weightKg = details.weightKg
        )
    }
    
    /**
     * Converts a domain ExerciseSet to a CreateExerciseSetRequest.
     */
    fun ExerciseSet.toCreateExerciseSetRequest(): CreateExerciseSetRequest {
        return CreateExerciseSetRequest(
            startTime = startTime,
            endTime = endTime,
            failure = failure,
            weightKg = weightKg,
            repetitions = repetitions
        )
    }
} 
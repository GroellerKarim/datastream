package eu.groeller.dsui.domain.model

/**
 * Represents the unit used for distance measurements in exercises.
 * This corresponds to the backend's DistanceUnit enum.
 */
enum class DistanceUnitEnum(val abbreviation: String) {
    /**
     * Distance measured in meters
     */
    METERS("m"),
    
    /**
     * Distance measured in kilometers
     */
    KILOMETERS("km"),
    
    /**
     * Distance measured in miles
     */
    MILES("mi")
} 
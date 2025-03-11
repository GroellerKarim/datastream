package eu.groeller.dsui.presentation.mapper

import eu.groeller.dsui.domain.model.DistanceUnitEnum
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Utility object with common formatting functions for UI presentation.
 */
object UIFormatterUtils {
    
    // Common formatters
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
    
    /**
     * Format duration in milliseconds to human-readable format.
     */
    fun formatDuration(durationMs: Long): String {
        val seconds = durationMs / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return when {
            hours > 0 -> String.format("%dh %02dm", hours, minutes % 60)
            minutes > 0 -> String.format("%dm %02ds", minutes, seconds % 60)
            else -> String.format("%ds", seconds)
        }
    }
    
    /**
     * Format duration between two times.
     */
    fun formatDuration(startTime: OffsetDateTime, endTime: OffsetDateTime): String {
        val durationMs = Duration.between(startTime, endTime).toMillis()
        return formatDuration(durationMs)
    }
    
    /**
     * Format date for display.
     */
    fun formatDate(dateTime: OffsetDateTime): String {
        return dateTime.format(dateFormatter)
    }
    
    /**
     * Format time for display.
     */
    fun formatTime(dateTime: OffsetDateTime): String {
        return dateTime.format(timeFormatter)
    }
    
    /**
     * Format date and time for display.
     */
    fun formatDateTime(dateTime: OffsetDateTime): String {
        return dateTime.format(dateTimeFormatter)
    }
    
    /**
     * Format weight value for display.
     */
    fun formatWeight(weight: Double): String {
        return if (weight % 1.0 == 0.0) {
            "${weight.toInt()} kg"
        } else {
            "%.1f kg".format(weight)
        }
    }
    
    /**
     * Format distance value for display based on unit.
     */
    fun formatDistance(distance: Double, unit: DistanceUnitEnum): String {
        return if (distance % 1.0 == 0.0) {
            "${distance.toInt()} ${unit.abbreviation}"
        } else {
            "%.2f ${unit.abbreviation}".format(distance)
        }
    }
    
    /**
     * Format pace (min/km, min/mile, etc.) based on distance and duration.
     */
    fun formatPace(
        distance: Double,
        unit: DistanceUnitEnum,
        durationMinutes: Long
    ): String? {
        // If distance is zero, can't calculate pace
        if (distance <= 0 || durationMinutes <= 0) return null
        
        val pacePerUnit = durationMinutes.toDouble() / distance
        val minutes = pacePerUnit.toInt()
        val seconds = ((pacePerUnit - minutes) * 60).toInt()
        
        return String.format("%d:%02d min/%s", minutes, seconds, unit.abbreviation)
    }
    
    /**
     * Format a duration in seconds to a clock-like display (MM:SS).
     */
    fun formatClockTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
} 
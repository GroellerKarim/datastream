package eu.groeller.datastreamui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class WorkoutType(
    val id: Long,
    val name: String
) : Parcelable 
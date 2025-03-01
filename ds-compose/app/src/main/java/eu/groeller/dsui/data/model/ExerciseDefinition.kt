package eu.groeller.datastreamui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import eu.groeller.datastreamui.data.model.ExerciseType

@Parcelize
@Serializable
data class ExerciseDefinition(
    val id: Long,
    val name: String,
    val type: ExerciseType
) : Parcelable

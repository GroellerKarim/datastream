package eu.groeller.datastreamui.model

import kotlinx.serialization.Serializable

@Serializable
data class Slice<T>(
    val content: List<T>,
    val size: Int,
    val number: Int,
    val numberOfElements: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean,
    val pageable: Pageable
)


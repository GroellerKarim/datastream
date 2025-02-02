package eu.groeller.datastreamui

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val email: String,
    val token: String
)

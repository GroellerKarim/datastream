package eu.groeller.dsui.domain.model

/**
 * Domain model representing a user of the application.
 */
data class User(
    val username: String,
    val email: String,
    val token: String
) 
package eu.groeller.dsui.data.mapper

import eu.groeller.datastreamui.User as DataUser
import eu.groeller.dsui.domain.model.User

/**
 * Mapper for converting between domain User models and data layer user models.
 */
object UserMapper {
    
    /**
     * Converts a data layer User to a domain User model.
     */
    fun DataUser.toDomain(): User {
        return User(
            username = username,
            email = email,
            token = token
        )
    }
    
    /**
     * Converts a domain User model to a data layer User.
     */
    fun User.toData(): DataUser {
        return DataUser(
            username = username,
            email = email,
            token = token
        )
    }
} 
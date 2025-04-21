package com.example.androidcleanarchitecture.data.mapper

import com.example.androidcleanarchitecture.data.model.UserModel
import com.example.androidcleanarchitecture.domain.entity.User

/**
 * Mapper class to convert between User domain entity and UserModel data model.
 */
class UserMapper {
    
    /**
     * Maps a UserModel from the data layer to a User entity in the domain layer.
     * @param userModel The UserModel to map
     * @return The mapped User entity
     */
    fun mapToDomain(userModel: UserModel): User {
        return User(
            id = userModel.id,
            name = userModel.name,
            email = userModel.email
        )
    }
    
    /**
     * Maps a User entity from the domain layer to a UserModel in the data layer.
     * @param user The User entity to map
     * @return The mapped UserModel
     */
    fun mapToData(user: User): UserModel {
        return UserModel(
            id = user.id,
            name = user.name,
            email = user.email,
        )
    }
}

package com.example.androidcleanarchitecture.data.model

/**
 * User data model in the data layer.
 * This class represents the user data as it exists in the data sources.
 */
data class UserModel(
    val id: Int,
    val name: String,
    val email: String
)

package com.example.androidcleanarchitecture.domain.entity

/**
 * User entity in the domain layer.
 * This is a simple data class that represents a user in the system.
 */
data class User(
    val id: Int,
    val name: String,
    val email: String
)

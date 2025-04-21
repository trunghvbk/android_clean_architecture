package com.example.androidcleanarchitecture.data.model

import com.google.gson.annotations.SerializedName

/**
 * Network model representing a user as received from the API.
 * This class is specifically for network operations and follows the API response structure.
 */
data class UserNetworkModel(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("username")
    val username: String
)

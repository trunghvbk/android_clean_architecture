package com.example.androidcleanarchitecture.di

import android.app.Application
import com.example.androidcleanarchitecture.data.datasource.UserDataSource
import com.example.androidcleanarchitecture.data.datasource.UserLocalDataSource
import com.example.androidcleanarchitecture.data.datasource.UserNetworkDataSource
import com.example.androidcleanarchitecture.data.mapper.UserMapper
import com.example.androidcleanarchitecture.data.mapper.UserNetworkMapper
import com.example.androidcleanarchitecture.data.network.ApiServiceFactory
import com.example.androidcleanarchitecture.data.repository.UserRepositoryImpl
import com.example.androidcleanarchitecture.domain.repository.UserRepository

/**
 * Dependency provider class that handles the creation and provision of dependencies.
 * This is a simple implementation of dependency injection using Kotlin's object for singleton pattern.
 * Updated to include network components.
 */
object ServiceLocator {
    
    private lateinit var application: Application
    
    // Repositories
    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            userLocalDataSource = userLocalDataSource,
            userNetworkDataSource = userNetworkDataSource,
            userMapper = userMapper
        )
    }
    
    // Data sources
    private val userLocalDataSource: UserDataSource by lazy {
        UserLocalDataSource()
    }
    
    private val userNetworkDataSource: UserDataSource by lazy {
        UserNetworkDataSource(
            userApiService = ApiServiceFactory.createUserApiService(),
            userNetworkMapper = userNetworkMapper
        )
    }
    
    // Mappers
    private val userMapper: UserMapper by lazy {
        UserMapper()
    }
    
    private val userNetworkMapper: UserNetworkMapper by lazy {
        UserNetworkMapper()
    }
    
    /**
     * Initialize the ServiceLocator with the application context
     * @param app The application context
     */
    fun initialize(app: Application) {
        application = app
    }
}

package com.example.androidcleanarchitecture

import android.app.Application
import com.example.androidcleanarchitecture.di.ServiceLocator
import com.example.androidcleanarchitecture.di.ViewModelFactory

/**
 * Custom Application class for the Clean Architecture sample app with Kotlin and Jetpack Compose.
 * This class initializes the dependency injection system.
 */
class CleanArchitectureApplication : Application() {
    
    lateinit var viewModelFactory: ViewModelFactory
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize the dependency injection system
        ServiceLocator.initialize(this)
        viewModelFactory = ViewModelFactory.getInstance(this)
    }
}

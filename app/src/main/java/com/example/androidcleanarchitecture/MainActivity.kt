package com.example.androidcleanarchitecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.androidcleanarchitecture.presentation.navigation.AppNavigation
import com.example.androidcleanarchitecture.presentation.ui.theme.CleanArchitectureTheme

/**
 * Main entry point for the application using Kotlin and Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get ViewModel factory from application
        val viewModelFactory = (application as CleanArchitectureApplication).viewModelFactory
        
        setContent {
            CleanArchitectureTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModelFactory = viewModelFactory)
                }
            }
        }
    }
}

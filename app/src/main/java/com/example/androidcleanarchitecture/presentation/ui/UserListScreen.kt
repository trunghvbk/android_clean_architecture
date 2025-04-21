package com.example.androidcleanarchitecture.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.androidcleanarchitecture.domain.entity.User
import com.example.androidcleanarchitecture.domain.util.Resource
import com.example.androidcleanarchitecture.presentation.viewmodel.UserListUiState
import com.example.androidcleanarchitecture.presentation.viewmodel.UserListViewModel

/**
 * Composable for the user list screen.
 * Updated to handle detailed error states.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserListViewModel,
    onUserClick: (User) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Users") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is UserListUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UserListUiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No users found",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadUsers() }) {
                            Text("Refresh")
                        }
                    }
                }
                is UserListUiState.Success -> {
                    LazyColumn {
                        items(state.users) { user ->
                            UserItem(user = user, onClick = { onUserClick(user) })
                        }
                    }
                }
                is UserListUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        errorType = state.errorType,
                        isRetryable = state.isRetryable,
                        onRetry = { viewModel.loadUsers() }
                    )
                }
            }
        }
    }
}

/**
 * Composable for displaying a user item in the list.
 */
@Composable
fun UserItem(
    user: User,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Composable for displaying error states with appropriate UI based on error type.
 */
@Composable
fun ErrorView(
    message: String,
    errorType: Resource.ErrorType,
    isRetryable: Boolean,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error icon based on type
        val icon = when (errorType) {
            Resource.ErrorType.NETWORK_ERROR -> "🌐"
            Resource.ErrorType.SERVER_ERROR -> "🖥️"
            Resource.ErrorType.CLIENT_ERROR -> "⚠️"
            Resource.ErrorType.VALIDATION_ERROR -> "📝"
            Resource.ErrorType.NOT_FOUND -> "🔍"
            Resource.ErrorType.UNAUTHORIZED -> "🔒"
            Resource.ErrorType.FORBIDDEN -> "⛔"
            Resource.ErrorType.LOCAL_ERROR -> "📱"
            Resource.ErrorType.UNKNOWN_ERROR -> "❓"
        }
        
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Additional context based on error type
        val helpText = when (errorType) {
            Resource.ErrorType.NETWORK_ERROR -> "Check your internet connection and try again."
            Resource.ErrorType.SERVER_ERROR -> "Our servers are experiencing issues. Please try again later."
            Resource.ErrorType.CLIENT_ERROR -> "There was a problem with your request."
            Resource.ErrorType.VALIDATION_ERROR -> "Please check your input and try again."
            Resource.ErrorType.NOT_FOUND -> "The requested resource could not be found."
            Resource.ErrorType.UNAUTHORIZED -> "Please log in to access this resource."
            Resource.ErrorType.FORBIDDEN -> "You don't have permission to access this resource."
            Resource.ErrorType.LOCAL_ERROR -> "There was a problem with the local data storage."
            Resource.ErrorType.UNKNOWN_ERROR -> "An unexpected error occurred."
        }
        
        Text(
            text = helpText,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isRetryable) {
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

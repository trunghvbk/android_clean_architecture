package com.example.androidcleanarchitecture.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.androidcleanarchitecture.domain.entity.User
import com.example.androidcleanarchitecture.presentation.viewmodel.OperationState
import com.example.androidcleanarchitecture.presentation.viewmodel.UserDetailUiState
import com.example.androidcleanarchitecture.presentation.viewmodel.UserDetailViewModel

/**
 * Composable for the user detail screen.
 * Updated to handle detailed error states.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    viewModel: UserDetailViewModel,
    userId: Int?,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()
    
    // Load user when the screen is first displayed
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }
    
    // Handle successful delete operation
    LaunchedEffect(operationState) {
        if (operationState is OperationState.Success && 
            (operationState as OperationState.Success).message.contains("deleted")) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Details") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is UserDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UserDetailUiState.Success -> {
                    UserDetailContent(
                        user = state.user,
                        operationState = operationState,
                        onDeleteUser = { viewModel.deleteUser(userId) },
                        onDismissOperationMessage = { viewModel.resetOperationState() }
                    )
                }
                is UserDetailUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        errorType = state.errorType,
                        isRetryable = state.isRetryable,
                        onRetry = { viewModel.loadUser(userId) }
                    )
                }
            }
        }
    }
}

/**
 * Composable for displaying user details content.
 */
@Composable
fun UserDetailContent(
    user: User,
    operationState: OperationState,
    onDeleteUser: () -> Unit,
    onDismissOperationMessage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // User details
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "ID: ${user.id}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Name: ${user.name}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Email: ${user.email}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Delete button
        Button(
            onClick = onDeleteUser,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Delete User")
        }
        
        // Operation state handling
        when (val state = operationState) {
            is OperationState.Loading -> {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
            is OperationState.Success -> {
                AlertDialog(
                    onDismissRequest = onDismissOperationMessage,
                    title = { Text("Success") },
                    text = { Text(state.message) },
                    confirmButton = {
                        Button(onClick = onDismissOperationMessage) {
                            Text("OK")
                        }
                    }
                )
            }
            is OperationState.Error -> {
                AlertDialog(
                    onDismissRequest = onDismissOperationMessage,
                    title = { Text("Error") },
                    text = {
                        Column {
                            Text(state.message)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = getHelpTextForErrorType(state.errorType),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = onDismissOperationMessage) {
                            Text("OK")
                        }
                    }
                )
            }
            else -> { /* Idle state, nothing to show */ }
        }
    }
}

/**
 * Helper function to get context-specific help text based on error type.
 */
@Composable
fun getHelpTextForErrorType(errorType: com.example.androidcleanarchitecture.domain.util.Resource.ErrorType): String {
    return when (errorType) {
        com.example.androidcleanarchitecture.domain.util.Resource.ErrorType.NETWORK_ERROR -> 
            "Check your internet connection and try again."
        com.example.androidcleanarchitecture.domain.util.Resource.ErrorType.SERVER_ERROR -> 
            "Our servers are experiencing issues. Please try again later."
        com.example.androidcleanarchitecture.domain.util.Resource.ErrorType.CLIENT_ERROR -> 
            "There was a problem with your request."
        com.example.androidcleanarchitecture.domain.util.Resource.ErrorType.VALIDATION_ERROR -> 
            "Please check your input and try again."
        com.example.androidcleanarchitecture.domain.util.Resource.ErrorType.NOT_FOUND -> 
            "The requested resource could not be found."
        com.example.androidcleanarchitecture.domain.util.Resource.ErrorType.UNAUTHORIZED -> 
            "Please log in to access this resource."
        com.example.androidcleanarchitecture.domain.util.Resource.ErrorType.FORBIDDEN -> 
            "You don't have permission to access this resource."
        com.example.androidcleanarchitecture.domain.util.Resource.ErrorType.LOCAL_ERROR -> 
            "There was a problem with the local data storage."
        com.example.androidcleanarchitecture.domain.util.Resource.ErrorType.UNKNOWN_ERROR -> 
            "An unexpected error occurred."
    }
}

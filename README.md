# Enhanced Network Error Handling for Android Clean Architecture

This project demonstrates an improved network error handling implementation for Android Clean Architecture. The implementation provides comprehensive error categorization, context, propagation, and handling across all layers of the application.

## Key Features

- **Detailed Error Categorization**: Different types of network errors (connectivity, timeout, client errors, server errors) are properly categorized
- **Rich Error Context**: Error messages include both user-friendly messages and technical details
- **Proper Error Propagation**: Errors flow consistently through all architectural layers
- **Graceful Fallbacks**: Network-first strategy with local fallbacks for offline support
- **Type-Safe Error Handling**: Using Kotlin's sealed classes and Result type
- **User-Friendly Error UI**: Contextual error messages with appropriate actions based on error type

## Implementation Overview

The error handling implementation follows clean architecture principles with proper separation of concerns across data, domain, and presentation layers:

### Data Layer

- **NetworkError**: Sealed class hierarchy for different error types
- **ErrorResponse**: Model for structured API error responses
- **NetworkException**: Custom exception for network errors
- **NetworkResult**: Wrapper for network responses with enhanced error handling
- **UserDataSource**: Updated to use Result<T> for better error propagation
- **UserNetworkDataSource**: Implements error mapping from network to domain

### Domain Layer

- **Resource**: Enhanced wrapper with detailed error types for domain operations
- **UserRepository**: Updated interface for consistent error handling

### Repository Layer

- **UserRepositoryImpl**: Implements network-first strategy with local fallbacks and proper error mapping

### Presentation Layer

- **ViewModels**: Handle different error types and provide appropriate UI states
- **UI Components**: Display contextual error messages with retry options when appropriate

## Error Types

The implementation handles the following error types:

- **Network Connectivity**: No internet connection
- **Timeout**: Request took too long to complete
- **Unknown Host**: Could not resolve the host name
- **Client Errors**: 4xx HTTP status codes
  - 401: Unauthorized
  - 403: Forbidden
  - 404: Not Found
  - 422: Validation Error
- **Server Errors**: 5xx HTTP status codes
- **Parse Errors**: Response could not be parsed
- **Unknown Errors**: Any other unexpected errors

## Usage

The error handling system is fully integrated with the existing clean architecture components and requires no special handling from the UI layer. The presentation layer has been updated to handle network states automatically.

### Example: Handling Errors in ViewModels

```kotlin
viewModelScope.launch {
    when (val result = getUserByIdUseCase(userId)) {
        is Resource.Success -> {
            // Handle success case
        }
        is Resource.Error -> {
            // Handle error based on type
            when (result.errorType) {
                Resource.ErrorType.NETWORK_ERROR -> {
                    // Show network error UI with retry option
                }
                Resource.ErrorType.SERVER_ERROR -> {
                    // Show server error UI
                }
                Resource.ErrorType.NOT_FOUND -> {
                    // Show not found UI
                }
                // Handle other error types...
            }
        }
        is Resource.Loading -> {
            // Show loading UI
        }
    }
}
```

### Example: Displaying Errors in UI

The UI components have been updated to display appropriate error messages based on the error type:

```kotlin
@Composable
fun ErrorView(
    message: String,
    errorType: Resource.ErrorType,
    isRetryable: Boolean,
    onRetry: () -> Unit
) {
    // Display error UI with context-specific message and actions
    // See UserListScreen.kt for full implementation
}
```

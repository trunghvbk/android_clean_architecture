# Android Clean Architecture with Kotlin

This project demonstrates the implementation of Clean Architecture principles in an Android application using Kotlin. The UI is built with Jetpack Compose, providing a modern, declarative approach to building Android UIs.

## Project Structure

The project follows Clean Architecture principles with a clear separation of concerns across three main layers:

### 1. Presentation Layer
- **UI Components**: Built with Jetpack Compose
- **ViewModels**: Handle UI-related data and state management
- **Navigation**: Handles screen navigation using Jetpack Navigation Compose

```
com.example.androidcleanarchitecture.presentation
├── navigation       # Navigation components and screen routes
├── ui               # Compose UI screens and components
│   ├── theme        # Theme definitions (colors, typography, shapes)
│   ├── UserListScreen.kt
│   └── UserDetailScreen.kt
└── viewmodel        # ViewModels for each screen
    ├── UserListViewModel.kt
    └── UserDetailViewModel.kt
```

### 2. Domain Layer
- **Entities**: Core business models
- **Use Cases**: Business logic operations
- **Repository Interfaces**: Define data operation contracts

```
com.example.androidcleanarchitecture.domain
├── entity           # Business models
│   └── User.kt
├── repository       # Repository interfaces
│   └── UserRepository.kt
├── usecase          # Business logic use cases
│   ├── DeleteUserUseCase.kt
│   ├── GetAllUsersUseCase.kt
│   ├── GetUserByIdUseCase.kt
│   └── SaveUserUseCase.kt
└── util             # Domain utilities
    └── Resource.kt  # Operation state wrapper
```

### 3. Data Layer
- **Repositories**: Implementation of domain repositories
- **Data Sources**: Local and network data providers
- **Models**: Data transfer objects
- **Mappers**: Convert between data and domain models
- **Network**: API services and network utilities

```
com.example.androidcleanarchitecture.data
├── datasource       # Data source implementations
│   ├── UserDataSource.kt        # Common interface
│   ├── UserLocalDataSource.kt   # Local storage implementation
│   └── UserNetworkDataSource.kt # Network implementation
├── mapper           # Data mappers
│   ├── UserMapper.kt
│   └── UserNetworkMapper.kt
├── model            # Data models
│   ├── UserModel.kt
│   └── UserNetworkModel.kt
├── network          # Network components
│   ├── ApiServiceFactory.kt
│   ├── NetworkConnectivityChecker.kt
│   ├── NetworkResult.kt
│   └── UserApiService.kt
└── repository       # Repository implementations
    └── UserRepositoryImpl.kt
```

### 4. Dependency Injection
- **ServiceLocator**: Simple DI implementation
- **ViewModelFactory**: Factory for creating ViewModels with dependencies

```
com.example.androidcleanarchitecture.di
├── ServiceLocator.kt
└── ViewModelFactory.kt
```

## Clean Architecture Explained

This project implements Clean Architecture, a software design philosophy that separates concerns into distinct layers:

### Core Principles

1. **Independence of Frameworks**: The business logic doesn't depend on UI, database, or external frameworks.
2. **Testability**: Business rules can be tested without UI, database, or external dependencies.
3. **Independence of UI**: The UI can change without changing the business rules.
4. **Independence of Database**: Business rules don't depend on the database implementation.
5. **Independence of External Agencies**: Business rules don't know anything about the outside world.

### Layer Dependencies

The dependencies flow from outer layers to inner layers:
- **Presentation Layer** → depends on → **Domain Layer**
- **Data Layer** → depends on → **Domain Layer**
- **Domain Layer** depends on nothing (it's the core)

This ensures that inner layers (domain) remain independent of implementation details in outer layers (data, presentation).

### Data Flow

1. **UI** triggers an action
2. **ViewModel** processes the action and calls appropriate **Use Case**
3. **Use Case** executes business logic using **Repository** interface
4. **Repository Implementation** coordinates data from **Data Sources**
5. **Data Sources** interact with local storage or network API
6. Data flows back up through the layers, with each layer mapping to its own models

## Jetpack Compose UI

This project uses Jetpack Compose, Android's modern toolkit for building native UI:

### Key Features Used

- **Declarative UI**: UI components are defined as functions that transform state into UI
- **State Management**: Using StateFlow with collectAsState() for reactive UI updates
- **Composable Functions**: Reusable UI components built with @Composable functions
- **Material Design Components**: Using Material3 design system
- **Navigation**: Implemented with Navigation Compose

### UI Components

- **UserListScreen**: Displays a list of users with loading, error, and empty states
- **UserDetailScreen**: Shows user details and provides editing capabilities
- **Theme**: Custom theme with colors, typography, and shapes

## Network Layer Implementation

The network layer has been implemented following clean architecture principles with proper separation of concerns:

### Key Components

- **UserNetworkModel**: Represents user data as received from the API
- **UserApiService**: Defines API endpoints using Retrofit annotations
- **ApiServiceFactory**: Creates and configures Retrofit and OkHttp clients
- **UserNetworkDataSource**: Implements UserDataSource interface for network operations
- **NetworkResult**: Sealed class for handling network responses (Success, Error, Loading)
- **Resource**: Domain-level wrapper for operation states (Success, Error, Loading)

### Network Strategy

The implementation uses a network-first strategy:
1. Try to fetch data from the network first
2. If successful, cache the data locally
3. If network fails, fall back to local data
4. For save/delete operations, attempt both network and local operations

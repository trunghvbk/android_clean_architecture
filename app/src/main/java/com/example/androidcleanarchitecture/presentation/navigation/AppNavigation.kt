package com.example.androidcleanarchitecture.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidcleanarchitecture.di.ViewModelFactory
import com.example.androidcleanarchitecture.domain.entity.User
import com.example.androidcleanarchitecture.presentation.ui.UserDetailScreen
import com.example.androidcleanarchitecture.presentation.ui.UserListScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidcleanarchitecture.presentation.viewmodel.UserDetailViewModel
import com.example.androidcleanarchitecture.presentation.viewmodel.UserListViewModel

/**
 * Navigation component for the app using Jetpack Compose Navigation.
 */
@Composable
fun AppNavigation(viewModelFactory: ViewModelFactory) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    
    NavHost (
        navController = navController,
        startDestination = Screen.UserList.route
    ) {
        // User List Screen
        composable(route = Screen.UserList.route) {
            val viewModel = viewModel<UserListViewModel>(
                factory = viewModelFactory
            )
            
            UserListScreen(
                viewModel = viewModel,
                onUserClick = { user ->
                    // Navigate to user detail with user ID
                    navController.navigate(
                        Screen.UserDetail.createRoute(
                            id = user.id,
                        )
                    )
                },
            )
        }
        
        // User Detail Screen (Edit existing user)
        composable(
            route = Screen.UserDetail.route,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            val viewModel = viewModel<UserDetailViewModel>(
                factory = viewModelFactory
            )
            
            UserDetailScreen(
                viewModel = viewModel,
                userId = id,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        
        // User Add Screen (Create new user)
        composable(route = Screen.UserAdd.route) {
            val viewModel = viewModel<UserDetailViewModel>(
                factory = viewModelFactory
            )
            
            UserDetailScreen(
                viewModel = viewModel,
                userId = null,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}

/**
 * Sealed class representing the different screens in the app.
 */
sealed class Screen(val route: String) {
    data object UserList : Screen("user_list")
    data object UserAdd : Screen("user_add")
    data object UserDetail : Screen("user_detail/{id}") {
        fun createRoute(id: Int): String {
            return "user_detail/$id"
        }
    }
}

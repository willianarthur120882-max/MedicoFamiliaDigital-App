package com.medicofamiliadigital.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.medicofamiliadigital.ui.screens.auth.LoginScreen
import com.medicofamiliadigital.ui.screens.auth.LoginViewModel
import com.medicofamiliadigital.ui.screens.home.HomeScreen
import com.medicofamiliadigital.ui.screens.home.HomeViewModel
import com.medicofamiliadigital.ui.screens.profiles.ProfilesScreen
import com.medicofamiliadigital.ui.screens.profiles.ProfilesViewModel

@Composable
fun MedicoFamiliaDigitalNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToProfiles = {
                    navController.navigate("profiles")
                },
                onNavigateToDocuments = {
                    navController.navigate("documents")
                },
                onNavigateToAI = {
                    navController.navigate("ai_assistant")
                }
            )
        }

        composable("profiles") {
            val viewModel: ProfilesViewModel = hiltViewModel()
            ProfilesScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // TODO: Adicionar outras telas (documents, ai_assistant, etc.)
    }
}


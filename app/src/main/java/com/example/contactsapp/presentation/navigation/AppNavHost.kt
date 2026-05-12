package com.example.contactsapp.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contactsapp.presentation.Screens.MainScreen
import com.example.contactsapp.presentation.viewmodel.MainScreenViewModel

@Composable
fun AppNavHost(
    innerPadding: PaddingValues
){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainScreenRoute,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ){
        composable<MainScreenRoute> {
            val viewModel: MainScreenViewModel = hiltViewModel()
            MainScreen(
                viewModel = viewModel
            )
        }
    }
}
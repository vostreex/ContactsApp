package com.example.contactsapp.presentation.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contactsapp.data.service.DuplicateContactResult
import com.example.contactsapp.data.service.DuplicateContactServiceClient
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
            val context = LocalContext.current
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val client = remember(context) { DuplicateContactServiceClient(context) }

            LaunchedEffect(Unit) {
                viewModel.loadContacts(context)
            }

            DisposableEffect(Unit) {
                client.bindService()
                onDispose { client.unBindService()}
            }

            MainScreen(
                onConfirmDeleteDuplicate = {
                    client.removeDuplicateContacts {
                        when(it){
                            DuplicateContactResult.SUCCESS -> {
                                viewModel.loadContacts(context)
                                Toast.makeText(context,"Дубликаты успешно удалены", Toast.LENGTH_SHORT).show()
                            }
                            DuplicateContactResult.DUPLICATE_NOT_FOUND -> Toast.makeText(context,"Дубликаты не найдены", Toast.LENGTH_SHORT).show()
                            DuplicateContactResult.ERROR -> Toast.makeText(context,"Ошибка при удалении", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                uiState = uiState,)
        }
    }
}
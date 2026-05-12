package com.example.contactsapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.usecase.GetContactsUseCase
import com.example.contactsapp.presentation.Screens.MainScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getContacts: GetContactsUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> get() = _uiState

     fun loadContacts(context: Context){
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }
            try {
                val contacts = getContacts(context = context)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        contacts = contacts,
                        error = null
                    )
                }
            } catch (e: Exception){
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }

        }
    }
}
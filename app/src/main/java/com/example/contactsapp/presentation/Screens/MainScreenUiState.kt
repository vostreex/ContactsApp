package com.example.contactsapp.presentation.Screens

import com.example.contactsapp.domain.model.Contact

data class MainScreenUiState(
    val isLoading: Boolean = false,
    val contacts: List<Contact> = emptyList(),
    val error: String? = null
)
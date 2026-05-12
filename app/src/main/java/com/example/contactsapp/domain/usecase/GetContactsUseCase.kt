package com.example.contactsapp.domain.usecase

import android.content.Context
import com.example.contactsapp.domain.repository.ContactsRepository
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
    private val repository: ContactsRepository
) {
    suspend operator fun invoke(context: Context) = repository.getContacts(context)
}

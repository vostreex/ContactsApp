package com.example.contactsapp.data.repositoryImpl

import android.content.Context
import android.provider.ContactsContract
import androidx.core.net.toUri
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.repository.ContactsRepository
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor() : ContactsRepository {
    override fun getContacts(context: Context): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver = context.contentResolver

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val typeIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
            val photoUriIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: "No name"
                val number = it.getString(numberIndex) ?: "No number"
                val type = it.getInt(typeIndex)
                val photoUriStr = it.getString(photoUriIndex)
                val photoUri = photoUriStr?.toUri()

                val typeLabel = when (type) {
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> "mobile"
                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> "home"
                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> "work"
                    else -> "other"
                }

                if (typeLabel == "mobile") {
                    contacts.add(Contact(name, number, typeLabel, photoUri))
                }
            }
        }
        return contacts
    }
}
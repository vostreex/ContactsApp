package com.example.contactsapp.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.ContactsContract
import com.example.contactsapp.IDuplicateContactService

class DuplicateContactService: Service() {

    private val binder = object : IDuplicateContactService.Stub() {
        /**
         * 0 Success
         * 1 Not Found
         * 2 Error
         */
        override fun removeDuplicateContacts(): Int {
            return try {
                val resolver = contentResolver

                val map = mutableMapOf<Pair<String, String>, MutableList<Long>>()
                val cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ),
                    null,
                    null,
                    null,
                )

                cursor?.use{
                    while (it.moveToNext()){
                        val rawNumber = it.getString(0) ?: continue
                        val number = rawNumber.replace("[^\\d]".toRegex(), "")
                        val contactName = it.getString(1)?.trim() ?: continue
                        val contactId = it.getString(2).toLongOrNull() ?: continue

                        val key = number to contactName

                        map.getOrPut(key) { mutableListOf() }.add(contactId)
                    }
                }

                val contactsForDelete = map.values.filter { it.size>1 }
                    .flatMap{ it.sortedDescending().drop(1) }
                if (contactsForDelete.isEmpty()) return 1

                for (contactId in contactsForDelete){
                    val rawCursor = resolver.query(
                        ContactsContract.RawContacts.CONTENT_URI,
                        arrayOf(ContactsContract.RawContacts._ID),
                        "${ContactsContract.RawContacts.CONTACT_ID} = ?",
                        arrayOf(contactId.toString()),
                        null
                    )

                    rawCursor?.use {
                        while (it.moveToNext()){
                            val rawContactId = it.getLong(0)

                            val rows = resolver.delete(
                                ContactsContract.RawContacts.CONTENT_URI,
                                "${ContactsContract.RawContacts._ID} = ?",
                                arrayOf(rawContactId.toString())
                            )
                        }
                    }
                }
                0
            } catch (e: Exception){
                2
            }
        }

    }

    override fun onBind(p0: Intent?): IBinder? {
       return binder
    }
}
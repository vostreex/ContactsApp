package com.example.contactsapp.data.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.contactsapp.IDuplicateContactService

class DuplicateContactServiceClient(
    private val context: Context
) {
    private var service: IDuplicateContactService? = null
    private var isBound = false

    private val connection = object : ServiceConnection{
        override fun onServiceConnected(
            name: ComponentName?,
            binder: IBinder?
        ) {
            service = IDuplicateContactService.Stub.asInterface(binder)
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            service = null
            isBound = false
        }
    }

    fun bindService(){
        val intent = Intent(context, DuplicateContactService::class.java)
        context.bindService(intent,connection, Context.BIND_AUTO_CREATE)
    }

    fun unBindService(){
        if(isBound){
            context.unbindService(connection)
            isBound = false
        }
    }

    fun removeDuplicateContacts(
        onComplete: (DuplicateContactResult) -> Unit
    ){
        try {
            val result = service?.removeDuplicateContacts()
            val status = when(result){
                0 -> DuplicateContactResult.SUCCESS
                1 -> DuplicateContactResult.DUPLICATE_NOT_FOUND
                else -> DuplicateContactResult.ERROR
            }
            onComplete(status)
        }catch (e: Exception){
            onComplete(DuplicateContactResult.ERROR)
        }
    }
}
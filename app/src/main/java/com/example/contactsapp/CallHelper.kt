package com.example.contactsapp

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

fun makeCall(context: Context, phoneNumber: String){

    try {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = "tel:$phoneNumber".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception){
        Toast.makeText(context,"Ошибка вызова", Toast.LENGTH_SHORT).show()
    } catch (e: SecurityException){
        Toast.makeText(context,"Отсутсвует разрешение", Toast.LENGTH_SHORT).show()
    }
}
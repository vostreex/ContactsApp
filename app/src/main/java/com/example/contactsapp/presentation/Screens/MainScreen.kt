package com.example.contactsapp.presentation.Screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.contactsapp.data.service.DuplicateContactResult
import com.example.contactsapp.data.service.DuplicateContactServiceClient
import com.example.contactsapp.makeCall
import com.example.contactsapp.presentation.items.ContactItem
import com.example.contactsapp.presentation.viewmodel.MainScreenViewModel
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
){
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val client = remember(context) { DuplicateContactServiceClient(context) }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadContacts(context)
    }

    DisposableEffect(Unit) {
        client.bindService()
        onDispose { client.unBindService() }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        when{
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.error != null -> {
                Text(text = uiState.error ?: "Error",
                    modifier = Modifier.align (Alignment.Center))
            }
            else ->
                LazyColumn(
                    modifier = Modifier.padding(bottom = 72.dp)
                ){
                    val groupedContact = uiState.contacts.groupBy {
                        it.name.firstOrNull()?.uppercase() ?: "#"
                    }.toSortedMap()
                    groupedContact.forEach { letter, contacts ->
                        item {
                            Text(
                                text = letter,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(
                            items = contacts,
                        ) { contact ->
                            ContactItem(contact) {
                                if (ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.CALL_PHONE
                                    ) == PackageManager.PERMISSION_GRANTED){
                                    makeCall(context,contact.phoneNumber)
                                } else{
                                    Toast.makeText(context,"Ошибка вызова, проверьте разрешение", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
        }
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 8.dp, bottom = 24.dp, end = 8.dp)
                .fillMaxWidth(),
            onClick = {
               showDeleteDialog = true
            }
        ) {
            Text("Удалить дубликаты")
        }
    }
    if (showDeleteDialog){
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить дубликаты") },
            text = { Text("Вы уверены что хотите удалить все дубликаты? Это действие нельзя будет отменить.") },
            confirmButton = {
                Text("Да,удалить",
                    modifier = Modifier
                        .clickable{
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
                        showDeleteDialog = false
                    }
                )
            },
            dismissButton = {
                Text("Отмена",
                    modifier = Modifier
                        .clickable{
                        showDeleteDialog = false
                    }
                )
            },
        )
    }
}
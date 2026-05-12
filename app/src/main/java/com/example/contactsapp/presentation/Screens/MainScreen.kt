package com.example.contactsapp.presentation.Screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.contactsapp.makeCall
import com.example.contactsapp.presentation.items.ContactItem
import com.example.contactsapp.presentation.viewmodel.MainScreenViewModel
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
){
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadContacts(context)
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
                LazyColumn{

                    val groupedContact = uiState.contacts.groupBy {
                        it.name.firstOrNull()?.uppercase() ?: "#"
                    }.toSortedMap()
                    groupedContact.forEach { letter, contacts ->
                        item {
                            Text(
                                text = letter,
                                modifier = Modifier.fillMaxWidth()
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
    }
}

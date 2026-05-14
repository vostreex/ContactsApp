package com.example.contactsapp

import android.content.Context
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.usecase.GetContactsUseCase
import com.example.contactsapp.presentation.viewmodel.MainScreenViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val getContactsUseCase = mockk<GetContactsUseCase>()
    private val context: Context = mockk(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup(){
        Dispatchers.setMain(dispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun down(){
        Dispatchers.resetMain()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun load_contacts_success() = runTest {

        val contacts = listOf(
            Contact(name = "Vasilii Ostnin", phoneNumber = "1234", phoneType = "mobile", photoUri = null)
        )

        coEvery {
            getContactsUseCase(context)
        } returns contacts

        val viewModel = MainScreenViewModel(getContactsUseCase)

        viewModel.loadContacts(context)

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(false,state.isLoading)
        assertEquals(contacts,state.contacts)
        assertEquals(null,state.error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun load_contacts_error() = runTest {

        coEvery {
            getContactsUseCase(context)
        } throws Exception("Error")

        val viewModel = MainScreenViewModel(getContactsUseCase)

        viewModel.loadContacts(context)

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(false,state.isLoading)
        assertEquals(emptyList<Contact>(),state.contacts)
        assertEquals("Error",state.error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun load_contacts_isLoading() = runTest {

        coEvery {
            getContactsUseCase(context)
        } coAnswers {
            delay(1000)
            emptyList()
        }

        val viewModel = MainScreenViewModel(getContactsUseCase)

        viewModel.loadContacts(context)

        advanceTimeBy(1)

        val state = viewModel.uiState.value

        assertEquals(true,state.isLoading)
        assertEquals(emptyList<Contact>(),state.contacts)
        assertEquals(null,state.error)
    }
}
package com.example.contactsapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.presentation.Screens.MainScreen
import com.example.contactsapp.presentation.Screens.MainScreenUiState
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun test_button_deleteDuplicate_confirm() {
        var callback = false
        composeRule.setContent {
            MaterialTheme{
                MainScreen(
                    uiState = MainScreenUiState(),
                    onConfirmDeleteDuplicate = {
                        callback = true
                    }
                )
            }
        }
        composeRule.onNodeWithText("Удалить дубликаты").performClick()
        composeRule.onNodeWithText("Да,удалить").performClick()

        assertEquals(true,callback)
    }

    @Test
    fun test_button_deleteDuplicate_denied() {
        var callback = false
        composeRule.setContent {
            MaterialTheme{
                MainScreen(
                    uiState = MainScreenUiState(),
                    onConfirmDeleteDuplicate = {
                        callback = true
                    }
                )
            }
        }
        composeRule.onNodeWithText("Удалить дубликаты").performClick()
        composeRule.onNodeWithText("Отмена").performClick()

        assertEquals(false,callback)
    }

    @Test
    fun mainScreen_isLoading() {
        composeRule.setContent {
            MaterialTheme{
                MainScreen(
                    uiState = MainScreenUiState(
                        isLoading = true
                    ),
                    onConfirmDeleteDuplicate = {}
                )
            }
        }
        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }

    @Test
    fun mainScreen_isError() {
        composeRule.setContent {
            MaterialTheme{
                MainScreen(
                    uiState = MainScreenUiState(
                        error = "Error"
                    ),
                    onConfirmDeleteDuplicate = {}
                )
            }
        }
        composeRule.onNodeWithText("Error").assertIsDisplayed()
    }

    @Test
    fun mainScreen_isSuccess() {
        composeRule.setContent {
            MaterialTheme{
                MainScreen(
                    uiState = MainScreenUiState(
                        contacts = listOf(
                            Contact(name = "Vasilii Ostnin", phoneNumber = "1234", phoneType = "mobile", photoUri = null),
                            Contact(name = "Vasilii Ostnin2", phoneNumber = "1234", phoneType = "mobile", photoUri = null)
                        )
                    ),
                    onConfirmDeleteDuplicate = {}
                )
            }
        }
        composeRule.onNodeWithText("Vasilii Ostnin").assertIsDisplayed()
        composeRule.onNodeWithText("Vasilii Ostnin2").assertIsDisplayed()
    }
}
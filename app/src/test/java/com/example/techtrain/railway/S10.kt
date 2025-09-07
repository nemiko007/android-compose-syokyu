package com.example.techtrain.railway

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasInsertTextAtCursorAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.techtrain.railway.android.MainScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class S10 {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test() {
        composeTestRule.setContent {
            MaterialTheme {
                MainScreen()
            }
        }

        composeTestRule.onNode(hasText("") and !hasInsertTextAtCursorAction())
            .assertExists(
                errorMessageOnFail = "Text Composableに空文字が設定されていません。"
            )

        composeTestRule.onNode(hasClickAction() and !hasInsertTextAtCursorAction())
            .assertExists("Button Composableが定義されていないか複数定義されています。")
            .performClick()

        composeTestRule.onNode(hasInsertTextAtCursorAction())
            .assertExists("TextField Composableが定義されていないか複数定義されています。")
            .performTextInput("TechTrain")

        composeTestRule.onNode(hasText("TechTrain"))
            .assertExists(
                errorMessageOnFail = "ボタンクリック後にText ComposableにTextFieldで入力された文字列が設定されていません。"
            )
    }
}

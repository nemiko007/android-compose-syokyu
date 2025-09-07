package com.example.techtrain.railway

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.techtrain.railway.android.MainScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class S9 {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test() {
        composeTestRule.setContent {
            MaterialTheme {
                MainScreen()
            }
        }

        composeTestRule.onNode(hasText("0"))
            .assertExists(
                errorMessageOnFail = "Text Composableに「0」が設定されていません。"
            )

        composeTestRule.onNode(hasClickAction())
            .assertExists("Button Composableが定義されていないか複数定義されています。")
            .performClick()

        composeTestRule.onNode(hasText("1"))
            .assertExists(
                errorMessageOnFail = "ボタンクリック後にText Composableに「1」が設定されていません。"
            )

        composeTestRule.onNode(hasClickAction())
            .assertExists("Button Composableが定義されていないか複数定義されています。")
            .performClick()

        composeTestRule.onNode(hasText("2"))
            .assertExists(
                errorMessageOnFail = "ボタンを2回クリック後にText Composableに「2」が設定されていません。"
            )
    }
}

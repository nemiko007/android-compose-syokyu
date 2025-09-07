package com.example.techtrain.railway

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.techtrain.railway.android.MainScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNotEquals

@RunWith(AndroidJUnit4::class)
class S4 {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test() {
        composeTestRule.setContent {
            MaterialTheme {
                MainScreen()
            }
        }

        val matcher = SemanticsMatcher("Text Composable Matcher") {
            val isInTextValue = it.config.getOrNull(SemanticsProperties.Text)
                ?.any { item -> item.text.isNotEmpty() } ?: false
            isInTextValue
        }
        val node = composeTestRule.onNode(matcher)
            .fetchSemanticsNode(
                errorMessageOnFail = "Text Composableが定義されていないか複数定義されています。"
            )
        val actualText = node.config[SemanticsProperties.Text].first().toString()
        assertNotEquals("", actualText, "Text Composableに文字が設定されていません。")
        assertNotEquals("Hello Railway!", actualText, "Text Composableの文字が設定されていません。")
    }
}

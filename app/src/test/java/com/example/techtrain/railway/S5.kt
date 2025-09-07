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
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class S5 {

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
        val displayedText = node.config[SemanticsProperties.Text].first().toString()
        assertNotEquals("", displayedText, "Text Composableに文字が設定されていません。")

        val activitySourceFile =
            File("src/main/java/com/example/techtrain/railway/android/MainActivity.kt").absoluteFile
        assertTrue(activitySourceFile.exists(), "MainActivity.ktが見つかりません。")
        assertFalse(
            activitySourceFile.containsOnAnyLine("\"$displayedText\""),
            "ソースコード内にText Composableで表示されているテキストが定義されています。"
        )
        val stringResourceFile =
            File("src/main/res/values/strings.xml").absoluteFile
        assertTrue(stringResourceFile.exists(), "strings.xmlが見つかりません。")
        assertTrue(
            stringResourceFile.containsOnAnyLine(">$displayedText<"),
            "strings.xmlにText Composableで表示されているテキストが定義されていません。"
        )
    }

    private fun File.containsOnAnyLine(text: String): Boolean =
        useLines { it.any { line -> line.contains(text, false) } }
}

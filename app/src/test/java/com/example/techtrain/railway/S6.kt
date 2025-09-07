package com.example.techtrain.railway

import android.content.res.Configuration
import android.os.LocaleList
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.techtrain.railway.android.MainScreen
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Locale
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class S6 {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test() = runTest {
        val locale = mutableStateOf(Locale.US)

        composeTestRule.setContent {
            val configuration = LocalConfiguration.current
            val newConfiguration = Configuration(configuration).apply {
                setLocale(locale.value)
                setLocales(LocaleList(locale.value))
            }
            val newContext = LocalContext.current.createConfigurationContext(newConfiguration)
            CompositionLocalProvider(
                LocalConfiguration provides newConfiguration,
                LocalContext provides newContext
            ) {
                MaterialTheme {
                    MainScreen()
                }
            }
        }

        val matcher = SemanticsMatcher("Text Composable Matcher") {
            val isInTextValue = it.config.getOrNull(SemanticsProperties.Text)
                ?.any { item -> item.text.isNotEmpty() } ?: false
            isInTextValue
        }

        val enNode = composeTestRule.onNode(matcher)
            .fetchSemanticsNode(
                errorMessageOnFail = "Text Composableが定義されていないか複数定義されています。"
            )
        val enDisplayedText = enNode.config[SemanticsProperties.Text].first().toString()
        assertNotEquals("", enDisplayedText, "Text Composableに文字が設定されていません。")

        locale.value = Locale.JAPAN

        val jpNode = composeTestRule.onNode(matcher)
            .fetchSemanticsNode(
                errorMessageOnFail = "Text Composableが定義されていないか複数定義されています。"
            )
        val jpDisplayedText = jpNode.config[SemanticsProperties.Text].first().toString()
        assertNotEquals("", jpDisplayedText, "Text Composableに文字が設定されていません。")

        val activitySourceFile =
            File("src/main/java/com/example/techtrain/railway/android/MainActivity.kt").absoluteFile
        assertTrue(activitySourceFile.exists(), "MainActivity.ktが見つかりません。")
        assertFalse(
            activitySourceFile.containsOnAnyLine("\"$enDisplayedText\""),
            "ソースコード内にText Composableで表示されているテキストが定義されています。"
        )
        assertFalse(
            activitySourceFile.containsOnAnyLine("\"$jpDisplayedText\""),
            "ソースコード内にText Composableで表示されているテキストが定義されています。"
        )
        val enStringResourceFile =
            File("src/main/res/values/strings.xml").absoluteFile
        assertTrue(enStringResourceFile.exists(), "strings.xmlが見つかりません。")
        assertTrue(
            enStringResourceFile.containsOnAnyLine(">$enDisplayedText<"),
            "strings.xmlにText Composableで表示されているテキストが定義されていません。"
        )
        val jpStringResourceFile =
            File("src/main/res/values-ja/strings.xml").absoluteFile
        assertTrue(jpStringResourceFile.exists(), "日本語用のstrings.xmlが見つかりません。")
        assertTrue(
            jpStringResourceFile.containsOnAnyLine(">$jpDisplayedText<"),
            "日本語用のstrings.xmlにText Composableで表示されているテキストが定義されていません。"
        )
        assertNotEquals(
            jpDisplayedText,
            enDisplayedText,
            "デバイスの言語が日本語とそれ以外の時で同じテキストが表示されています。"
        )
    }

    private fun File.containsOnAnyLine(text: String): Boolean =
        useLines { it.any { line -> line.contains(text, false) } }
}

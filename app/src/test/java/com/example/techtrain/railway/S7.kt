package com.example.techtrain.railway

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.techtrain.railway.android.MainScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class S7 {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test() {
        composeTestRule.setContent {
            MaterialTheme {
                MainScreen()
            }
        }

        val matcher = SemanticsMatcher("Image Composable Matcher") {
            val testTag = it.config.getOrNull(SemanticsProperties.TestTag)
            testTag?.startsWith("Image") == true
        }

        val node = composeTestRule.onNode(matcher)
            .fetchSemanticsNode(
                errorMessageOnFail = "Image Composableが定義されていないか複数定義されています。"
            )

        assertEquals(50, node.layoutInfo.height, "Image Composableの高さが50dpではありません。")
        assertEquals(
            node.layoutInfo.coordinates.size.width,
            node.layoutInfo.width,
            "Image Composableの幅が画面幅と一致しません。"
        )
        assertNull(
            node.config.getOrNull(SemanticsProperties.ContentDescription),
            "Image Composableにnull以外のContentDescriptionが設定されています。"
        )

        val activitySourceFile =
            File("src/main/java/com/example/techtrain/railway/android/MainActivity.kt").absoluteFile
        assertTrue(activitySourceFile.exists(), "MainActivity.ktが見つかりません。")
        assertTrue(
            activitySourceFile.containsOnAnyLine(Regex("""ColorPainter\s*\(\s*Color\s*\.\s*Black\)""")),
            "黒一色(`Colors.Black`)のPainterが使用されていません。"
        )
    }

    private fun File.containsOnAnyLine(regex: Regex): Boolean =
        useLines { it.any { line -> line.contains(regex) } }
}

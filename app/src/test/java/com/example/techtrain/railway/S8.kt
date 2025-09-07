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
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class S8 {

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

        val mdpiDrawableDirectory = File("src/main/res/drawable-mdpi").absoluteFile
        assertTrue(mdpiDrawableDirectory.isDirectory, "drawable-mdpiディレクトリがありません。")
        assertEquals(
            1,
            mdpiDrawableDirectory.listFiles()?.size,
            "画像が存在しないか、2つ以上の画像がdrawable-mdpiフォルダにあります。"
        )
        val drawableFile = checkNotNull(mdpiDrawableDirectory.listFiles()?.first())
        val drawableName = drawableFile.name

        val hdpiDrawableDirectory = File("src/main/res/drawable-hdpi").absoluteFile
        assertTrue(hdpiDrawableDirectory.isDirectory, "drawable-hdpiディレクトリがありません。")
        val hdpiDrawableFile = hdpiDrawableDirectory.resolve(drawableName)
        assertTrue(hdpiDrawableFile.exists(), "drawable-hdpi内にmdpiと同じ名前の画像がありません。")

        val xhdpiDrawableDirectory = File("src/main/res/drawable-xhdpi").absoluteFile
        assertTrue(xhdpiDrawableDirectory.isDirectory, "drawable-xhdpiディレクトリがありません。")
        val xhdpiDrawableFile = xhdpiDrawableDirectory.resolve(drawableName)
        assertTrue(
            xhdpiDrawableFile.exists(),
            "drawable-xhdpi内にmdpiと同じ名前の画像がありません。"
        )

        val name = drawableName.substringBeforeLast('.')
        val activitySourceFile =
            File("src/main/java/com/example/techtrain/railway/android/MainActivity.kt").absoluteFile
        assertTrue(activitySourceFile.exists(), "MainActivity.ktが見つかりません。")
        assertTrue(
            activitySourceFile.containsOnAnyLine(Regex("""painterResource\s*\(\s*R\s*\.\s*drawable\s*\.\s*${name}\s*\)""")),
            "画像リソースを使用したPainterが使用されていません。"
        )
    }

    private fun File.containsOnAnyLine(regex: Regex): Boolean =
        useLines { it.any { line -> line.contains(regex) } }
}

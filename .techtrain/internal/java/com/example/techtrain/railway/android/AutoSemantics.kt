package com.example.techtrain.railway.android

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag

@Composable
fun Modifier.autoSemantics(composableName: String): Modifier = semantics {
    println("Setting testTag: $composableName")
    this.testTag = composableName
}

@SuppressLint("ModifierFactoryExtensionFunction")
@Composable
fun newAutoSemantics(composableName: String): Modifier = Modifier.semantics {
    println("Setting testTag: $composableName")
    this.testTag = composableName
}

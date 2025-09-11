package dev.techtrain

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

class Logger(
    private val messageCollector: MessageCollector
) {
    fun log(text: String) = messageCollector.report(CompilerMessageSeverity.WARNING, text)
}

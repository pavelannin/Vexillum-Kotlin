package io.github.pavelannin.vexillum.logger

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

public actual fun VexillumLogger(): VexillumLogger = JvmLogger()

private class JvmLogger : VexillumLogger {
    private val consoleHandler: ConsoleHandler = ConsoleHandler().apply {
        level = Level.ALL
        formatter = SimpleFormatter()
    }
    private val logger = Logger.getLogger(JvmLogger::class.java.name).apply {
        level = Level.ALL
        addHandler(consoleHandler)
        useParentHandlers = false
    }

    override fun info(message: String) {
        logger.info("[INFO] Vexillum - $message")
    }
}

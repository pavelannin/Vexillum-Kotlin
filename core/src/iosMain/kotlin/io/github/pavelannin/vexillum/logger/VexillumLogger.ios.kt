package io.github.pavelannin.vexillum.logger

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

public actual fun VexillumLogger(): VexillumLogger = IOSLogger()

private class IOSLogger : VexillumLogger {
    private val dateFormatter = NSDateFormatter().apply { dateFormat = "YYYY-MM-dd HH:mm:ss.SSS" }

    override fun info(message: String) {
        log("INFO", message)
    }

    private fun log(tag: String, message: String) {
        val currentTime = dateFormatter.stringFromDate(NSDate())
        val str = "$currentTime [$tag] Vexillum - $message"
        println(str)
    }
}

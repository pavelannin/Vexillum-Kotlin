package io.github.pavelannin.vexillum.logger

import android.util.Log

public actual fun VexillumLogger(): VexillumLogger = AndroidLogger()

private class AndroidLogger : VexillumLogger {
    override fun info(message: String) {
        Log.i("Vexillum", message)
    }
}

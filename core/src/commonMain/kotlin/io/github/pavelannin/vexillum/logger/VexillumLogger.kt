package io.github.pavelannin.vexillum.logger

/**
 * ###### EN:
 * Represents an interface for logging Vexillum events.
 *
 * ###### RU:
 * Представляет интерфейс для логирования событий Vexillum.
 */
public interface VexillumLogger {
    /**
     * ###### EN:
     * Logs an informational message.
     *
     * ###### RU:
     * Логирует информационное сообщение.
     *
     * @param message
     * ###### EN:
     * The message to log.
     *
     * ###### RU:
     * Логируемое сообщение.
     */
    public fun info(message: String)
}

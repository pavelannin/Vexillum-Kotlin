package io.github.pavelannin.vexillum

import io.github.pavelannin.vexillum.runtime.RuntimeFeatureFlagMomentValue
import kotlinx.coroutines.flow.StateFlow

/**
 * ###### EN:
 * Vexillum — provides access to feature flags with support for status checking,
 * monitoring changes, and working with payload.
 * ###### RU:
 * Vexillum — представляет доступ к фичи флагам с поддержкой проверки состояния,
 * наблюдения за изменениями и работы с допонтительными данными (payload).
 */
public interface Vexillum {

    /**
     * ###### EN:
     * Returns the [flag] state.
     * Returns `true` if activated, otherwise `false'.
     * ###### RU:
     * Возвращает состояние [flag].
     * Возвращает `true`, если активирован, иначе `false`.
     */
    public fun isEnabled(flag: CompileFeatureFlag<*>): Boolean

    /**
     * ###### EN:
     * Returns the [flag] state.
     * Returns `true` if activated, otherwise `false'.
     * ###### RU:
     * Возвращает состояние [flag].
     * Возвращает `true`, если активирован, иначе `false`.
     */
    @RuntimeFeatureFlagMomentValue
    public fun isEnabled(flag: RuntimeFeatureFlag<*>): Boolean

    /**
     * ###### EN:
     * Returns a `StateFlow` that emits the current state of the runtime feature
     * flag and continues to emit updates when the state changes.
     * ###### RU:
     * Возвращает `StateFlow`, который передаёт текущее состояние фичи флага и продолжает
     * передавать обновления при его изменении.
     */
    public fun observeEnabled(flag: RuntimeFeatureFlag<*>): StateFlow<Boolean>

    /**
     * ###### EN:
     * Returns the payload (e.g., configuration data) associated with the feature flag.
     * ###### RU:
     * Возвращает дополнительные данные (например, конфигурацию), связанные с фичи флагом.
     */
    public fun <Payload : Any> payload(flag: CompileFeatureFlag<Payload>): Payload

    /**
     * ###### EN:
     * Returns the payload (e.g., configuration data) associated with the feature flag.
     * ###### RU:
     * Возвращает дополнительные данные (например, конфигурацию), связанные с фичи флагом.
     */
    @RuntimeFeatureFlagMomentValue
    public fun <Payload : Any> payload(flag: RuntimeFeatureFlag<Payload>): Payload

    /**
     * ###### EN:
     * Returns a `StateFlow` that emits the current payload of the runtime feature flag and
     * continues to emit updates when the payload changes.
     * ###### RU:
     * Возвращает `StateFlow`, который передаёт текущие значение дополнтиельных данных и
     * продолжает передавать обновления при их изменении.
     */
    public fun <Payload : Any> observePayload(flag: RuntimeFeatureFlag<Payload>): StateFlow<Payload>
}

package io.github.pavelannin.vexillum.runtime

import io.github.pavelannin.vexillum.RuntimeFeatureFlag

/**
 * ###### EN:
 * Presents a values of the feature flag.
 * ###### RU:
 * Представляет значения фичи флага.
 *
 * @param Payload
 * ###### EN
 * The type of data that can additionally be attached to the feature flag.
 * It is used for configuration data and partial A/B test data.
 * ###### RU
 * Тип данных который дополнительно можно прикрепить к фичи флагу.
 * Используется для данных конфигураций, пополоднтиельных данных A/B - тестов.
 *
 * @property isEnabled
 * ###### EN:
 * Current enabled state of the feature flag.
 * ###### RU:
 * Текущее состояние фичи флага.
 * @property payload
 * ###### EN:
 * Current payload value of the feature flag.
 * ###### RU:
 * Текущее значение дополнительных данных фичи флага.
 */
public data class RuntimeFeatureFlagValue<Payload : Any>(
    public val isEnabled: Boolean,
    public val payload: Payload,
)

/**
 * ###### EN:
 * Creates a [RuntimeFeatureFlagValue] with values from the specified [RuntimeFeatureFlag].
 * ###### RU:
 * Создает [RuntimeFeatureFlagValue] со значениями из [RuntimeFeatureFlag].
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <Payload : Any> RuntimeFeatureFlagValue(
    flag: RuntimeFeatureFlag<Payload>
): RuntimeFeatureFlagValue<Payload> = RuntimeFeatureFlagValue(
    isEnabled = flag.defaultEnabled,
    payload = flag.defaultPayload,
)

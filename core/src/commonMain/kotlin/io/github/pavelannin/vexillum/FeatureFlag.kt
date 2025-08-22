package io.github.pavelannin.vexillum

import kotlin.reflect.KClass

/**
 * ###### EN
 * Presents the flag feature object.
 * They are used to switch functionality and provide configuration data.
 * ###### RU
 * Представляет объект фичи флага.
 * Используются для переключения функциональности и предоставления данных конфигурации.
 *
 * @param Payload
 * ###### EN
 * The type of data that can additionally be attached to the feature flag.
 * It is used for configuration data and partial A/B test data.
 * ###### RU
 * Тип данных который дополнительно можно прикрепить к фичи флагу.
 * Используется для данных конфигураций, пополоднтиельных данных A/B - тестов.
 */
public sealed interface FeatureFlag<Payload> {
    /**
     * ###### EN
     * The unique identifier for this feature flag.
     * ###### RU
     * Уникальный идентификатор фичи флага.
     */
    public val key: String

    /**
     * ###### EN
     * A description explaining the purpose and use of the flag feature.
     * Used for documentation and maintenance purposes.
     * ###### RU
     * Описание, объясняющее назначение и использование фичи флага.
     * Используется для целей документации и сопровождения.
     */
    public val description: String?
}

/**
 * ###### EN
 * A feature flag with values determined at compile time.
 * The enabled state and payload are fixed and cannot be changed without recompilation.
 * ###### RU
 * Реализация фичи флага со значениями, определяемыми во время компиляции.
 * Состояние фичи флага и нагрузка фиксированы и не могут быть изменены без перекомпиляции.
 *
 * @property isEnabled
 * ###### EN
 * The fixed enabled state of this feature flag.
 * ###### RU
 * Фиксированное состояние фичи флага (включено / выключено).
 * @property payload
 * ###### EN
 * The fixed payload data associated with this feature flag.
 * ###### RU
 * Фиксированные дополнительные данные.
 */
public class CompileFeatureFlag<Payload : Any> internal constructor(
    override val key: String,
    public val isEnabled: Boolean,
    public val payload: Payload,
    override val description: String? = null,
) : FeatureFlag<Payload>

/**
 * ###### EN
 * Implementation of a flag feature with values that can be changed during execution.
 * Provides default values that can be changed at runtime.
 * ###### RU
 * Реализация фичи флага со значениями, которые можно изменять во время выполнения.
 * Предоставляет значения по умолчанию, которые могут быть изменены во время выполнения.
 *
 * @property defaultEnabled
 * ###### EN
 * The default enabled state of this feature flag.
 * ###### RU
 * Состояние фичи флага (включено / выключено) по умолчанию.
 * @property defaultPayload
 * ###### EN
 * The default payload data associated with this feature flag.
 * ###### RU
 * Дополнительные данные по умолчанию.
 * @property payloadType
 * ###### EN
 * The type of payload data.
 * ###### RU
 * Тип дополнительных данных.
 */
public class RuntimeFeatureFlag<Payload : Any> internal constructor(
    override val key: String,
    public val defaultEnabled: Boolean,
    public val defaultPayload: Payload,
    public val payloadType: KClass<Payload>,
    override val description: String? = null
) : FeatureFlag<Payload>

package io.github.pavelannin.vexillum.source

import io.github.pavelannin.vexillum.RuntimeFeatureFlag
import io.github.pavelannin.vexillum.runtime.RuntimeFeatureFlagMomentValue
import io.github.pavelannin.vexillum.runtime.RuntimeFeatureFlagValue
import kotlinx.coroutines.flow.Flow

/**
 * ###### EN:
 * Represents the source of obtaining the actual values of the changeable flag feature.
 * ###### RU:
 * Представляет источник получения фактических значений изменяемы фичи флагов.
 */
public interface FeatureFlagSource {
    /**
     * ###### EN:
     * Retrieves the current value for the [flag].
     * Returns null if the flag is not found in the source or if no value is set.
     * ###### RU:
     * Возвращает текущее значение для указанного [flag].
     * Возвращает null, если флаг не найден в источнике или значение не установлено.
     */
    @RuntimeFeatureFlagMomentValue
    public operator fun <Payload : Any> get(flag: RuntimeFeatureFlag<Payload>): RuntimeFeatureFlagValue<Payload>?

    /**
     * ###### EN:
     * Observes changes to the [flag] value.
     * Returns a [Flow] that emits the current value and all subsequent updates.
     * Emits `null` if the flag is not available in the source.
     * ###### RU:
     * Наблюдает за изменениями значения [flag].
     * Возвращает [Flow], который испускает текущее значение и все последующие обновления.
     * Испускает `null`, если флаг недоступен в источнике.
     */
    public fun <Payload : Any> observe(flag: RuntimeFeatureFlag<Payload>): Flow<RuntimeFeatureFlagValue<Payload>?>
}

/**
 * ###### EN:
 * Represents a modifiable source for the actual values of the flags being modified by the feature.
 * ###### RU:
 * Представляет изменяемый источник для фактических значений изменяемых фичи флагов.
 */
public interface FeatureFlagMutableSource : FeatureFlagSource {
    /**
     * ###### EN:
     * Updates the value of the [flag] using a transformation function.
     * ###### RU:
     * Обновляет значение [flag] с использованием функции преобразования.
     */
    public suspend fun <Payload : Any> update(
        flag: RuntimeFeatureFlag<Payload>,
        block: suspend RuntimeFeatureFlag<Payload>.(RuntimeFeatureFlagValue<Payload>) -> RuntimeFeatureFlagValue<Payload>
    )

    /**
     * ###### EN:
     * Removes the [flag] from the source.
     * ###### RU:
     * Удаляет указанный [flag] из источника.
     */
    public suspend fun remove(flag: RuntimeFeatureFlag<*>)

    /**
     * ###### EN:
     * Removes all runtime feature flag values from the source.
     * ###### RU:
     * Удаляет все значения из источника.
     */
    public suspend fun clear()
}

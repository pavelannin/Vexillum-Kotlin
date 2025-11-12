package io.github.pavelannin.vexillum.source

import io.github.pavelannin.vexillum.FeatureFlagSpec

/**
 * ###### EN:
 * Represents an extended data source [FeatureFlagSource] with the ability to modify
 * the actual value of a feature flag.
 *
 * Prefer to use in cases where you need to change feature flag values externally.
 *
 * ###### RU:
 * Представляет рассширенный источник данных [FeatureFlagSource] с возможностью изменения
 * фактического значения фича-флага.
 *
 * Предпочительно использовать, в случаях когда нужно изменять значения фича-флага из вне.
 */
public interface FeatureFlagMutableSource : FeatureFlagSource {
    /**
     * ###### EN:
     * Updates the current value of a feature flag.
     *
     * ###### RU:
     * Обновляет значение фича-флага.
     *
     * @param spec
     * ###### EN:
     * Specification of the feature flag to be updated.
     *
     * ###### RU:
     * Спецификация обновляемого фича-флага.
     *
     * @param block
     * ###### EN:
     * Transformation function that takes the current feature flag value and returns a new value.
     *
     * ###### RU:
     * Функция трансформации, которая принимает текущее значение фича-флага, и возвращает новое значение.
     */
    public suspend fun <Value : Any> update(spec: FeatureFlagSpec<Value>, block: suspend (Value?) -> Value)

    /**
     * ###### EN:
     * Removes the feature flag value from the source.
     *
     * ###### RU:
     * Удаляет значение фича-флага из источника.
     *
     * @param spec
     * ###### EN:
     * Specification of the feature flag to be removed.
     *
     * ###### RU:
     * Спецификация удаляемого фича-флага.
     */
    public suspend fun <Value : Any> remove(spec: FeatureFlagSpec<Value>)

    /**
     * ###### EN:
     * Removes all feature flag values from the source.
     *
     * ###### RU:
     * Удаляет значения всех фича-флагов из источника.
     */
    public suspend fun clear()
}

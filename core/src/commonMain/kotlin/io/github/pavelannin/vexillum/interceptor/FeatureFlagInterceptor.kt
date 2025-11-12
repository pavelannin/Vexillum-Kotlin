package io.github.pavelannin.vexillum.interceptor

import io.github.pavelannin.vexillum.FlowFeatureFlagSpec
import io.github.pavelannin.vexillum.ImmutableFeatureFlagSpec
import io.github.pavelannin.vexillum.MutableFeatureFlagSpec
import io.github.pavelannin.vexillum.Vexillum
import io.github.pavelannin.vexillum.source.FeatureFlagSource

/**
 * ###### EN:
 * Represents an interface for intercepting and modifying feature flag values.
 * Interceptors allow modifying feature flag values at runtime, after the value
 * has been fetched from [io.github.pavelannin.vexillum.source.FeatureFlagSource].
 *
 * Interceptors are used for:
 * - debug panels that allow overriding values for testing
 * - A/B testing that modifies values based on user segments
 * - overriding values during development
 * - logging feature flag values
 *
 * ###### RU:
 * Представляет интерфейс перехвата и изменения значений фича-флагов.
 * Перехватчики позволяют изменять значение фича-флага во время выполнения, после того как
 * значение было получено из [io.github.pavelannin.vexillum.source.FeatureFlagSource].
 *
 * Перехватчики используются для:
 * - панелей отладки, которые позволяют переопределять значения для тестирования
 * - A/B тестирование, которые изменяют значения на основе пользовательских сегментов
 * - переопределение значений во время разработки
 * - логирование значений фича-флагов
 *
 * @property id
 * ###### EN:
 * Unique interceptor identifier.
 *
 * ###### RU:
 * Уникальный идентификатор перехватчика.
 *
 * @property description
 * ###### EN:
 * Human-readable purpose of this interceptor.
 *
 * ###### RU:
 * Понятное человеку назначение этого перехватчика.
 */
public interface FeatureFlagInterceptor {
    public val id: String
    public val description: String?

    /**
     * ###### EN:
     * Intercepts a feature flag value and optionally provides a replacement.
     *
     * This method is called before the value is returned to the caller.
     * The interceptor can inspect the value and optionally return a different value.
     *
     * ###### RU:
     * Перехватывает значение фича-флага и при необходимости предоставляет замену.
     *
     * Этот метод вызывается до того, как оно будет возвращено вызывающей стороне.
     * Перехватчик может проверить значение, при необходимости, вернуть другое значение.
     *
     * @param spec
     * ###### EN:
     * Specification of the requested feature flag.
     *
     * ###### RU:
     * Спецификация запрашиваемого фича-флага.
     *
     * @param value
     * ###### EN:
     * Actual feature flag value. Immutable feature flags always return
     * the [ImmutableFeatureFlagSpec.value].
     *
     * ###### RU:
     * Фактическое занчение фича-флага. Неизменяемые фича-флаги всегда возвращают
     * значение [ImmutableFeatureFlagSpec.value].
     *
     * @return
     * ###### EN:
     * New value (to replace the original) or current value (to leave it unchanged).
     *
     * ###### RU:
     * Новое (заменяющее значение) или текущее (чтобы оставить значение без изменений).
     */
    public fun <Value : Any> Vexillum.intercept(
        spec: ImmutableFeatureFlagSpec<Value>,
        value: Value,
    ): Value

    /**
     * ###### EN:
     * Intercepts a feature flag value and optionally provides a replacement.
     *
     * This method is called before the value is returned to the caller.
     * The interceptor can inspect the value and optionally return a different value.
     *
     * @param spec
     * ###### EN:
     * Specification of the requested feature flag.
     *
     * ###### RU:
     * Спецификация запрашиваемого фича-флага.
     *
     * @param source
     * ###### EN:
     * Data source from which the value was obtained.
     *
     * If no data source provided the value, then
     * [io.github.pavelannin.vexillum.source.VexillumDefaultValueSource].
     *
     * ###### RU:
     * Источник данных из которого было полученно значение.
     *
     * Если не один источник даннных не предоставил данные, то
     * [io.github.pavelannin.vexillum.source.VexillumDefaultValueSource].
     *
     * @param value
     * ###### EN:
     * Actual feature flag value.
     *
     * ###### RU:
     * Фактическое занчение фича-флага.
     *
     * @return
     * ###### EN:
     * New value (to replace the original) or current value (to leave it unchanged).
     *
     * ###### RU:
     * Новое (заменяющее значение) или текущее (чтобы оставить значение без изменений).
     */
    public suspend fun <Value : Any> Vexillum.intercept(
        spec: MutableFeatureFlagSpec<Value>,
        source: FeatureFlagSource,
        value: Value,
    ): Value

    /**
     * ###### EN:
     * Intercepts a feature flag value and optionally provides a replacement.
     *
     * This method is called before the value is returned to the caller.
     * The interceptor can inspect the value and optionally return a different value.
     *
     * @param spec
     * ###### EN:
     * Specification of the requested feature flag.
     *
     * ###### RU:
     * Спецификация запрашиваемого фича-флага.
     *
     * @param source
     * ###### EN:
     * Data source from which the value was obtained.
     *
     * If no data source provided the value, then
     * [io.github.pavelannin.vexillum.source.VexillumDefaultValueSource].
     *
     * ###### RU:
     * Источник данных из которого было полученно значение.
     *
     * Если не один источник даннных не предоставил данные, то
     * [io.github.pavelannin.vexillum.source.VexillumDefaultValueSource].
     *
     * @param value
     * ###### EN:
     * Actual feature flag value.
     *
     * ###### RU:
     * Фактическое занчение фича-флага.
     *
     * @return
     * ###### EN:
     * New value (to replace the original) or current value (to leave it unchanged).
     *
     * ###### RU:
     * Новое (заменяющее значение) или текущее (чтобы оставить значение без изменений).
     */
    public suspend fun <Value : Any> Vexillum.intercept(
        spec: FlowFeatureFlagSpec<Value>,
        source: FeatureFlagSource,
        value: Value,
    ): Value
}

package io.github.pavelannin.vexillum

import io.github.pavelannin.vexillum.interceptor.FeatureFlagInterceptor
import io.github.pavelannin.vexillum.logger.VexillumLogger
import io.github.pavelannin.vexillum.source.FeatureFlagSource
import kotlinx.coroutines.flow.StateFlow

/**
 * ###### EN:
 * Represents the main entry point for interacting with the Vexillum library.
 *
 * Vexillum provides a unified approach to working with feature flags in an application by combining
 * data sources, interceptors, and providing access to feature flag values during application runtime.
 *
 * ###### RU:
 * Представляет основную точку взаимодействия с библиотекой Vexillum.
 *
 * Vexillum - единый способ работы с фича-флагами в приложении за счет объединения
 * источников данных, перехватчиков и предоставления доступа к значениям фича-флагов во время
 * выполнения приложения.
 */
public interface Vexillum {
    /**
     * ###### EN:
     * Retrieves the value for an immutable feature flag.
     *
     * The returned value is selected according to the following logic:
     * 1. Feature flag value ([ImmutableFeatureFlagSpec.value])
     * 2. Interceptors are applied in registration order
     *
     * ###### RU:
     * Извлекает значение для неизменяемого фича-флага.
     *
     * Возвращаемое значение выбирается по следующей логике:
     * 1. Значение фича-флага ([ImmutableFeatureFlagSpec.value])
     * 2. Перехватчики применяются в порядке регистрации
     *
     * @param spec
     * ###### EN:
     * Specification of the requested feature flag.
     *
     * ###### RU:
     * Спецификация запрашиваемого фича-флага.
     *
     * @return
     * ###### EN:
     * Current value of the immutable feature flag.
     *
     * ###### RU:
     * Текущее значение неизменяемого фича-флага.
     */
    public operator fun <Value : Any> get(spec: ImmutableFeatureFlagSpec<Value>): Value

    /**
     * ###### EN:
     * Retrieves the value for a mutable feature flag.
     *
     * The returned value is selected according to the following logic:
     * 1. Default feature flag value ([MutableFeatureFlagSpec.defaultValue])
     * 2. Data sources are polled in registration order (first obtained value wins)
     * 3. Interceptors are applied in registration order
     *
     * ###### RU:
     * Извлекает значение для изменяемого фича-флага.
     *
     * Возвращаемое значение выбирается по следующей логике:
     * 1. Значение фича-флага по умолчанию ([MutableFeatureFlagSpec.defaultValue])
     * 2. Опрос источников в порядке регистрации (выигрывает первое полученное значение)
     * 3. Перехватчики применяются в порядке регистрации
     *
     * @param spec
     * ###### EN:
     * Specification of the requested feature flag.
     *
     * ###### RU:
     * Спецификация запрашиваемого фича-флага.
     *
     * @return
     * ###### EN:
     * Current value of the mutable feature flag.
     *
     * ###### RU:
     * Текущее значение изменяемого фича-флага.
     */
    public suspend operator fun <Value : Any> get(spec: MutableFeatureFlagSpec<Value>): Value

    /**
     * ###### EN:
     * Retrieves the value for a mutable feature flag returned as [kotlinx.coroutines.flow.Flow].
     *
     * The returned value is selected according to the following logic:
     * 1. Default feature flag value ([FlowFeatureFlagSpec.defaultValue])
     * 2. Data sources are polled in registration order (first obtained value wins)
     * 3. Interceptors are applied in registration order
     *
     * ###### RU:
     * Извлекает значение для изменяемого фича-флага возвращаемого [kotlinx.coroutines.flow.Flow].
     *
     * Возвращаемое значение выбирается по следующей логике:
     * 1. Значение фича-флага по умолчанию ([FlowFeatureFlagSpec.defaultValue])
     * 2. Опрос источников в порядке регистрации (выигрывает первое полученное значение)
     * 3. Перехватчики применяются в порядке регистрации
     *
     * @param spec
     * ###### EN:
     * Specification of the requested feature flag.
     *
     * ###### RU:
     * Спецификация запрашиваемого фича-флага.
     *
     * @return
     * ###### EN:
     * Current value of the mutable feature flag.
     *
     * ###### RU:
     * Текущее значение изменяемого фича-флага.
     */
    public operator fun <Value : Any> get(spec: FlowFeatureFlagSpec<Value>): StateFlow<Value>

    /**
     * ###### EN:
     * Adds a data source.
     *
     * ###### RU:
     * Добавляет источник данных.
     *
     * @param source
     * ###### EN:
     * Data source to add.
     *
     * ###### RU:
     * Источник данных для добавления.
     *
     * @return
     * ###### EN:
     * `true` if the data source was added.
     *
     * ###### RU:
     * `true` - если источник данных был добавлен.
     */
    public fun addSource(source: FeatureFlagSource): Boolean

    /**
     * ###### EN:
     * Removes a data source.
     *
     * ###### RU:
     * Удаляет источник данных.
     *
     * @param source
     * ###### EN:
     * Data source to remove.
     *
     * ###### RU:
     * Источник данных для удаления.
     *
     * @return
     * ###### EN:
     * `true` if the data source was removed.
     *
     * ###### RU:
     * `true` - если источник данных был удален.
     */
    public fun removeSource(source: FeatureFlagSource): Boolean

    /**
     * ###### EN:
     * Represents a collection of all registered data sources.
     *
     * ###### RU:
     * Представляет коллекцию всех зарегистрированных источников данных.
     *
     * @return
     * ###### EN:
     * Collection of registered data sources.
     *
     * ###### RU:
     * Коллекция зарегистрированных источников данных.
     */
    public fun allSources(): Set<FeatureFlagSource>

    /**
     * ###### EN:
     * Adds an interceptor.
     *
     * ###### RU:
     * Добавляет перехватчик.
     *
     * @param interceptor
     * ###### EN:
     * Interceptor to add.
     *
     * ###### RU:
     * Перехватчик для добавления.
     *
     * @return
     * ###### EN:
     * `true` if the interceptor was added.
     *
     * ###### RU:
     * `true` - если перехватчик был добавлен.
     */
    public fun addInterceptor(interceptor: FeatureFlagInterceptor): Boolean

    /**
     * ###### EN:
     * Removes an interceptor.
     *
     * ###### RU:
     * Удаляет перехватчик.
     *
     * @param interceptor
     * ###### EN:
     * Interceptor to remove.
     *
     * ###### RU:
     * Перехватчик для удаления.
     *
     * @return
     * ###### EN:
     * `true` if the interceptor was removed.
     *
     * ###### RU:
     * `true` - если перехватчик был удален.
     */
    public fun removeInterceptor(interceptor: FeatureFlagInterceptor): Boolean

    /**
     * ###### EN:
     * Represents a collection of all registered interceptors.
     *
     * ###### RU:
     * Представляет коллекцию всех зарегистрированных перехватчиков.
     *
     * @return
     * ###### EN:
     * Collection of registered interceptors.
     *
     * ###### RU:
     * Коллекция зарегистрированных перехватчиков.
     */
    public fun allInterceptors(): Set<FeatureFlagInterceptor>

    /**
     * ###### EN:
     * Sets the logger.
     *
     * ###### RU:
     * Устанавливает логгер.
     *
     * @param logger
     * ###### EN:
     * Logger for logging events.
     *
     * ###### RU:
     * Логгер для логирования событий.
     */
    public fun setLogger(logger: VexillumLogger?)
}

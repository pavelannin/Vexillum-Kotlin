package io.github.pavelannin.vexillum

import io.github.pavelannin.vexillum.runtime.RuntimeFeatureFlagMomentValue
import io.github.pavelannin.vexillum.source.FeatureFlagSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn

/**
 * ###### EN:
 * The default implementation of the [Vexillum] interface that aggregates multiple feature flag
 * [sources]. This class provides a unified way to access feature flag values from multiple
 * [sources] with a defined priority order.
 * ###### RU:
 * Стандартная реализация интерфейса [Vexillum], которая агрегирует несколько [sources].
 * Предоставляет унифицированный способ доступа к значениям фичи флагов из нескольких источников
 * с определенным порядком приоритета.
 *
 * @param sources
 * ###### EN:
 * The list of feature flag sources to use for retrieving flag values.
 * Sources are checked in the order they are provided (first source has highest priority).
 * ###### RU:
 * Список источников для получения значений фичи флагов.
 * Источники проверяются в порядке их предоставления (первый источник имеет высший приоритет).
 */
public class VexillumDefault(
    private val sources: List<FeatureFlagSource>,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : Vexillum {
    private val coroutineScope = CoroutineScope(dispatcher)

    override fun isEnabled(flag: CompileFeatureFlag<*>): Boolean {
        return flag.isEnabled
    }

    @RuntimeFeatureFlagMomentValue
    override fun isEnabled(flag: RuntimeFeatureFlag<*>): Boolean {
        return sources.asSequence()
            .mapNotNull { source -> source[flag]?.isEnabled }
            .firstOrNull()
            ?: flag.defaultEnabled
    }

    @OptIn(RuntimeFeatureFlagMomentValue::class)
    override fun observeEnabled(flag: RuntimeFeatureFlag<*>): StateFlow<Boolean> {
        val flows = sources.map { source -> source.observe(flag) }
        return flows.merge()
            .filterNotNull()
            .map { value -> value.isEnabled }
            .stateIn(coroutineScope, SharingStarted.Lazily, isEnabled(flag))
    }

    override fun <Payload : Any> payload(flag: CompileFeatureFlag<Payload>): Payload {
        return flag.payload
    }

    @RuntimeFeatureFlagMomentValue
    override fun <Payload : Any> payload(flag: RuntimeFeatureFlag<Payload>): Payload {
        return sources.asSequence()
            .mapNotNull { source -> source[flag]?.payload }
            .firstOrNull()
            ?: flag.defaultPayload
    }

    @OptIn(RuntimeFeatureFlagMomentValue::class)
    override fun <Payload : Any> observePayload(flag: RuntimeFeatureFlag<Payload>): StateFlow<Payload> {
        val flows = sources.map { source -> source.observe(flag) }
        return flows.merge()
            .filterNotNull()
            .map { value -> value.payload }
            .stateIn(coroutineScope, SharingStarted.Lazily, payload(flag))
    }
}

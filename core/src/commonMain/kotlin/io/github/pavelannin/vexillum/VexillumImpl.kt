package io.github.pavelannin.vexillum

import io.github.pavelannin.vexillum.interceptor.FeatureFlagInterceptor
import io.github.pavelannin.vexillum.logger.VexillumLogger
import io.github.pavelannin.vexillum.source.VexillumDefaultValueSource
import io.github.pavelannin.vexillum.source.FeatureFlagSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlin.getValue

public fun Vexillum(
    sources: Set<FeatureFlagSource> = emptySet(),
    interceptors: Set<FeatureFlagInterceptor> = emptySet(),
    logger: VexillumLogger? = null,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Vexillum = VexillumImpl(sources, interceptors, logger, dispatcher)

private class VexillumImpl(
    sources: Set<FeatureFlagSource>,
    interceptors: Set<FeatureFlagInterceptor>,
    logger: VexillumLogger?,
    dispatcher: CoroutineDispatcher,
) : Vexillum {
    private val sources = mutableSetOf<FeatureFlagSource>()
    private val interceptors = mutableSetOf<FeatureFlagInterceptor>()
    private val coroutineScope = CoroutineScope(dispatcher)
    private var logger: VexillumLogger?

    init {
        if (sources.isNotEmpty()) {
            this.sources += sources
        }
        if (interceptors.isNotEmpty()) {
            this.interceptors += interceptors
        }
        this.logger = logger
    }

    override fun <Value : Any> get(spec: ImmutableFeatureFlagSpec<Value>): Value {
        val resultValue = interceptors.fold(spec.value) { acc, interceptor ->
            with(interceptor) { intercept(spec, acc) }
        }

        logger?.info("Get value '$resultValue' by immutable spec '${spec.id}'")
        return resultValue
    }

    override suspend fun <Value : Any> get(spec: MutableFeatureFlagSpec<Value>): Value {
        var resultValue = spec.defaultValue
        var valueSource: FeatureFlagSource = VexillumDefaultValueSource

        for (source in sources) {
            val value = source[spec]
            if (value != null) {
                resultValue = value
                valueSource = source
                break
            }
        }

        resultValue = interceptors.fold(resultValue) { acc, interceptor ->
            with(interceptor) { intercept(spec, valueSource, acc) }
        }

        logger?.info("Get value '$resultValue' by mutable spec '${spec.id}' from source '${valueSource.id}'")
        return resultValue
    }

    override fun <Value : Any> get(spec: FlowFeatureFlagSpec<Value>): StateFlow<Value> {
        fun Flow<Value?>.transformValue(source: FeatureFlagSource) = this
            .runningFold<Value?, Value?>(initial = null) { _, value -> value }
            .map { value -> source to value }

        suspend fun FlowCollector<Value>.emit(source: FeatureFlagSource, value: Value) {
            val resultValue = interceptors.fold(value) { acc, interceptor ->
                with(interceptor) { intercept(spec, source, acc) }
            }
            logger?.info("Get value '$resultValue' by flow spec '${spec.id}' from source '${source.id}'")
            emit(resultValue)
        }

        return flow {
            val default by lazy { VexillumDefaultValueSource to spec.defaultValue }
            val flows = sources.mapNotNull { source -> source[spec]?.transformValue(source) }

            if (flows.isNotEmpty()) {
                combine(flows) { values ->
                    for ((source, value) in values) {
                        if (value != null) {
                            return@combine source to value
                        }
                    }
                    return@combine null
                }
                    .map { value -> value ?: default }
                    .collect { (valueSource, resultValue) -> emit(valueSource, resultValue) }
            } else {
                emit(default.first, default.second)
            }
        }.stateIn(coroutineScope, spec.startedStrategy, spec.defaultValue)
    }

    override fun addSource(source: FeatureFlagSource): Boolean {
        return sources.add(source)
    }

    override fun removeSource(source: FeatureFlagSource): Boolean {
        return sources.remove(source)
    }

    override fun allSources(): Set<FeatureFlagSource> {
        return sources.toSet()
    }

    override fun addInterceptor(interceptor: FeatureFlagInterceptor): Boolean {
        return interceptors.add(interceptor)
    }

    override fun removeInterceptor(interceptor: FeatureFlagInterceptor): Boolean {
        return interceptors.remove(interceptor)
    }

    override fun allInterceptors(): Set<FeatureFlagInterceptor> {
        return interceptors.toSet()
    }

    override fun setLogger(logger: VexillumLogger?) {
        this.logger = logger
    }
}

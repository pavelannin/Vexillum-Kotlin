package io.github.pavelannin.vexillum.source.memory

import io.github.pavelannin.vexillum.FeatureFlagSpec
import io.github.pavelannin.vexillum.FlowFeatureFlagSpec
import io.github.pavelannin.vexillum.MutableFeatureFlagSpec
import io.github.pavelannin.vexillum.source.FeatureFlagMutableSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.reflect.safeCast

/**
 * ###### EN:
 * An implementation of [FeatureFlagMutableSource] that stores the flag feature values in memory.
 * This implementation is used for testing, caching, or scenarios where data retention outside the
 * application lifecycle is not required.
 *
 * ###### RU:
 * Реализация [FeatureFlagMutableSource], которая хранит значения фичи флагов в памяти.
 * Эта реализация используется для тестирования, кэширования или сценариев, где
 * не требуется сохранение данных за пределами жизненного цикла приложения.
 */
public class VexillumMemorySource(
    override val id: String = "vexillum_memory_source",
    override val description: String? = "The source stores the values of the feature flags in volatile memory (RAM)",
) : FeatureFlagMutableSource {
    private val map = ObservableMutableMap<String, Any>()

    override suspend fun <Value : Any> get(spec: MutableFeatureFlagSpec<Value>): Value? {
        return spec.valueType.safeCast(map[spec.id])
    }

    override fun <Value : Any> get(spec: FlowFeatureFlagSpec<Value>): Flow<Value?> {
        return map.observe(spec.id)
            .map { value -> spec.valueType.safeCast(value) }
    }

    override suspend fun <Value : Any> update(
        spec: FeatureFlagSpec<Value>,
        block: suspend (Value?) -> Value,
    ) {
        val saved = spec.valueType.safeCast(map[spec.id])
        val new = block(saved)
        map[spec.id] = new
    }

    override suspend fun <Value : Any> remove(spec: FeatureFlagSpec<Value>) {
        map.remove(spec.id)
    }

    override suspend fun clear() {
        map.clear()
    }
}

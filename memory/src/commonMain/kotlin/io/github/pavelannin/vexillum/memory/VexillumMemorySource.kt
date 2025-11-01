package io.github.pavelannin.vexillum.memory

import io.github.pavelannin.vexillum.RuntimeFeatureFlag
import io.github.pavelannin.vexillum.runtime.RuntimeFeatureFlagMomentValue
import io.github.pavelannin.vexillum.runtime.RuntimeFeatureFlagValue
import io.github.pavelannin.vexillum.source.FeatureFlagMutableSource
import kotlinx.coroutines.flow.Flow

/**
 * ###### EN:
 * An implementation of [FeatureFlagMutableSource] that stores the flag feature values in memory.
 * This implementation is used for testing, caching, or scenarios where data retention outside the
 * application lifecycle is not required.
 * ###### RU:
 * Реализация [FeatureFlagMutableSource], которая хранит значения фичи флагов в памяти.
 * Эта реализация используется для тестирования, кэширования или сценариев, где
 * не требуется сохранение данных за пределами жизненного цикла приложения.
 */
public class VexillumMemorySource : FeatureFlagMutableSource {
    private val mutableMap = ObservableMutableMap<RuntimeFeatureFlag<*>, RuntimeFeatureFlagValue<*>>()

    @Suppress("UNCHECKED_CAST")
    @RuntimeFeatureFlagMomentValue
    override fun <Payload : Any> get(flag: RuntimeFeatureFlag<Payload>): RuntimeFeatureFlagValue<Payload>? {
        return mutableMap[flag] as RuntimeFeatureFlagValue<Payload>?
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Payload : Any> observe(flag: RuntimeFeatureFlag<Payload>): Flow<RuntimeFeatureFlagValue<Payload>?> {
        return mutableMap.observe(flag) as Flow<RuntimeFeatureFlagValue<Payload>?>
    }

    @OptIn(RuntimeFeatureFlagMomentValue::class)
    override suspend fun <Payload : Any> update(
        flag: RuntimeFeatureFlag<Payload>,
        block: suspend RuntimeFeatureFlag<Payload>.(RuntimeFeatureFlagValue<Payload>) -> RuntimeFeatureFlagValue<Payload>
    ) {
        val saved = get(flag)
        val new = block(flag, saved ?: RuntimeFeatureFlagValue(flag))
        mutableMap[flag] = new
    }

    override suspend fun remove(flag: RuntimeFeatureFlag<*>) {
        mutableMap.remove(flag)
    }

    override suspend fun clear() {
        mutableMap.clear()
    }
}

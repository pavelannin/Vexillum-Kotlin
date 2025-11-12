package io.github.pavelannin.vexillum.source.stub

import io.github.pavelannin.vexillum.FlowFeatureFlagSpec
import io.github.pavelannin.vexillum.MutableFeatureFlagSpec
import io.github.pavelannin.vexillum.source.FeatureFlagSource
import kotlinx.coroutines.flow.Flow

/**
 * ###### EN:
 * Implements a data source that indicates the value was obtained from the feature flag's default value.
 *
 * This source is used by [io.github.pavelannin.vexillum.interceptor.FeatureFlagInterceptor]
 * to indicate that the feature flag value comes from the default value.
 *
 * ###### RU:
 * Реализует источник данных информирующий о том, что значение получено из значения фича-флага
 * по умолчанию.
 *
 * Этот источник используется для [io.github.pavelannin.vexillum.interceptor.FeatureFlagInterceptor],
 * что бы проинформировать о значение фича-флага по умолчанию.
 */
public object VexillumDefaultValueSource : FeatureFlagSource {
    override val id: String = "default_value"
    override val description: String = "The data source indicates that the default value of the feature flag is used"
    override suspend fun <Value : Any> get(spec: MutableFeatureFlagSpec<Value>): Value? = null
    override fun <Value : Any> get(spec: FlowFeatureFlagSpec<Value>): Flow<Value?>? = null
}
package io.github.pavelannin.vexillum.source

import io.github.pavelannin.vexillum.FlowFeatureFlagSpec
import io.github.pavelannin.vexillum.MutableFeatureFlagSpec
import kotlinx.coroutines.flow.Flow

/**
 * ###### EN:
 * Represents a data source for providing the actual value of a feature flag.
 *
 * Examples of data sources:
 * - Backend (REST API)
 * - Local storage (In-Memory / Shared Preferences / Data Store / SQL / Room)
 * - Firebase Remote Config
 *
 * ###### RU:
 * Представляет источник данных для предоставления фактического значения фича-флага.
 *
 * Примеры источников данных:
 * - Сервераная часть (REST API)
 * - Хранимые настройки (In-Memory / Shared Preferences / Data Store / SQL / Room)
 * - Firebase Remote Config
 *
 * @property id
 * ###### EN:
 * Unique data source identifier.
 *
 * ###### RU:
 * Уникальный идентификатор источника данных.
 *
 * @property description
 * ###### EN:
 * Human-readable purpose of this data source.
 *
 * ###### RU:
 * Понятное человеку назначение этого источника данных.
 */
public interface FeatureFlagSource {
    public val id: String
    public val description: String?

    /**
     * ###### EN:
     * Returns actual values for a feature flag.
     *
     * ###### RU:
     * Возвращает фактические значения для фича-флага.
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
     * Returns the current value of the feature flag, returns `null` if the value is not available.
     *
     * ###### RU:
     * Возвращает текущее значение фича-флага, если значение отсутствует возвращает `null`.
     */
    public suspend operator fun <Value : Any> get(spec: MutableFeatureFlagSpec<Value>): Value?

    /**
     * ###### EN:
     * Returns actual values for a feature flag.
     *
     * ###### RU:
     * Возвращает фактические значения для фича-флага.
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
     * Returns a [Flow] that emits the current value and all subsequent updates for
     * the feature flag.
     * If the source doesn't support the current feature flag, returns `null`.
     * If the value is missing, emits `null'.
     *
     * ###### RU:
     * Возвращает [Flow], который испускает текущее значение и все последующие обновления для
     * фича-флага.
     * Если источник не поддерживает текущий фича-флаг, возвращает `null`.
     * Если значение отсутствует, испускает `null`.
     */
    public operator fun <Value : Any> get(spec: FlowFeatureFlagSpec<Value>): Flow<Value?>?
}

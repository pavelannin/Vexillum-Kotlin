package io.github.pavelannin.vexillum

import kotlinx.coroutines.flow.SharingStarted
import kotlin.reflect.KClass

/**
 * ###### EN:
 * Represents a feature flag specification.
 * The specification contains metadata about the feature flag, including its name, description, and
 * information about the feature flag's data type.
 *
 * ###### RU:
 * Представляет спецификацию фича-флага.
 * Спецификация содержит метаданные о фича-флаге, включая его название, описание и информацию о
 * типе данных фича-флага.
 *
 * @param Value
 * ###### EN:
 * Feature flag type.
 *
 * ###### RU:
 * Тип фича-флага.
 *
 * @property id
 * ###### EN:
 * Unique feature flag identifier.
 *
 * ###### RU:
 * Уникальный идентификатор фича-флага.
 *
 * @property description
 * ###### EN:
 * Human-readable purpose of this feature flag. The description helps developers understand
 * the purpose and scope of the feature flag.
 *
 * ###### RU:
 * Понятное человеку назначение этого фича-флага. Описание помогает разработчикам понять
 * назначение и область применения фича-флага.
 *
 * @property valueType
 * ###### EN:
 * Feature flag type information as a [KClass].
 *
 * ###### RU:
 * Информация о типе фича-флага в виде [KClass].
 */
public sealed interface FeatureFlagSpec<Value : Any> {
    public val id: String
    public val description: String?
    public val valueType: KClass<Value>
}

/**
 * ###### EN:
 * Provides an implementation of [FeatureFlagSpec] for immutable feature flags.
 * Immutable feature flags are defined at compile time and cannot be changed without
 * rebuilding the application.
 *
 * Immutable feature flags are used to gate functionality that is under development.
 *
 * ###### RU:
 * Предоставляет реализацию [FeatureFlagSpec] для неизменяемых фича-флага.
 * Этот фича-флаг определяются на уровне компиляции и неможет быть изменены без пересборки
 * приложения.
 *
 * Этот фича-флаги применяются для закрытия функционала который находится на этапе разработки.
 *
 * @param Value
 * ###### EN:
 * Feature flag type.
 *
 * ###### RU:
 * Тип фича-флага.
 *
 * @param value
 * ###### EN:
 * Feature flag value.
 *
 * ###### RU:
 * Значение фича-флага.
 *
 * @see FeatureFlagSpec
 */
public data class ImmutableFeatureFlagSpec<Value : Any>(
    override val id: String,
    public val value: Value,
    override val valueType: KClass<Value>,
    override val description: String? = null,
) : FeatureFlagSpec<Value>

/**
 * ###### EN:
 * Provides an implementation of [FeatureFlagSpec] for immutable feature flags.
 * Immutable feature flags are defined at compile time and cannot be changed without
 * rebuilding the application.
 *
 * Immutable feature flags are used to gate functionality that is under development.
 *
 * ###### RU:
 * Предоставляет реализацию [FeatureFlagSpec] для неизменяемых фича-флага.
 * Этот фича-флаг определяются на уровне компиляции и неможет быть изменены без пересборки
 * приложения.
 *
 * Этот фича-флаги применяются для закрытия функционала который находится на этапе разработки.
 *
 * @param Value
 * ###### EN:
 * Feature flag type.
 *
 * ###### RU:
 * Тип фича-флага.
 *
 * @param id
 * ###### EN:
 * Unique feature flag identifier.
 *
 * ###### RU:
 * Уникальный идентификатор фича-флага.
 *
 * @param value
 * ###### EN:
 * Feature flag value.
 *
 * ###### RU:
 * Значение фича-флага.
 *
 * @param description
 * ###### EN:
 * Human-readable purpose of this feature flag. The description helps developers understand
 * the purpose and scope of the feature flag.
 *
 * ###### RU:
 * Понятное человеку назначение этого фича-флага. Описание помогает разработчикам понять
 * назначение и область применения фича-флага.
 *
 * @see FeatureFlagSpec
 */
public inline fun <reified Value : Any> ImmutableFeatureFlagSpec(
    id: String,
    value: Value,
    description: String? = null,
): ImmutableFeatureFlagSpec<Value> = ImmutableFeatureFlagSpec(id, value, Value::class, description)

/**
 * ###### EN:
 * Provides an implementation of [FeatureFlagSpec] for mutable feature flags.
 * Mutable feature flags can be changed during application runtime. Values for mutable
 * feature flags are provided by [io.github.pavelannin.vexillum.source.FeatureFlagSource].
 *
 * ###### RU:
 * Предоставляет реализацию [FeatureFlagSpec] для изменяемых фича-флага.
 * Этот фича-флаг может быть изменен во время работы приложения. Значения фича-флагам задают
 * [io.github.pavelannin.vexillum.source.FeatureFlagSource].
 *
 * @param Value
 * ###### EN:
 * Feature flag type.
 *
 * ###### RU:
 * Тип фича-флага.
 *
 * @param defaultValue
 * ###### EN:
 * Default feature flag value. The value can be changed during application runtime.
 *
 * The default value will be used if no
 * [io.github.pavelannin.vexillum.source.FeatureFlagSource] returns a value for
 * this feature flag.
 *
 * ###### RU:
 * Значение фича-флага по умолчанию. Значение может быть именено во время выполенения приложения.
 *
 * Значение по умолчанию будет исспользоваться если не один
 * [io.github.pavelannin.vexillum.source.FeatureFlagSource] не вернул значение для
 * этого фича-флага.
 *
 * @see FeatureFlagSpec
 */
public data class MutableFeatureFlagSpec<Value : Any>(
    override val id: String,
    public val defaultValue: Value,
    override val valueType: KClass<Value>,
    override val description: String? = null,
) : FeatureFlagSpec<Value>

/**
 * ###### EN:
 * Provides an implementation of [FeatureFlagSpec] for mutable feature flags.
 * Mutable feature flags can be changed during application runtime. Values for mutable
 * feature flags are provided by [io.github.pavelannin.vexillum.source.FeatureFlagSource].
 *
 * ###### RU:
 * Предоставляет реализацию [FeatureFlagSpec] для изменяемых фича-флага.
 * Этот фича-флаг может быть изменен во время работы приложения. Значения фича-флагам задают
 * [io.github.pavelannin.vexillum.source.FeatureFlagSource].
 *
 * @param Value
 * ###### EN:
 * Feature flag type.
 *
 * ###### RU:
 * Тип фича-флага.
 *
 * @param id
 * ###### EN:
 * Unique feature flag identifier.
 *
 * ###### RU:
 * Уникальный идентификатор фича-флага.
 *
 * @param defaultValue
 * ###### EN:
 * Default feature flag value. The value can be changed during application runtime.
 *
 * The default value will be used if no
 * [io.github.pavelannin.vexillum.source.FeatureFlagSource] returns a value for
 * this feature flag.
 *
 * ###### RU:
 * Значение фича-флага по умолчанию. Значение может быть именено во время выполенения приложения.
 *
 * Значение по умолчанию будет исспользоваться если не один
 * [io.github.pavelannin.vexillum.source.FeatureFlagSource] не вернул значение для
 * этого фича-флага.
 *
 * @param description
 * ###### EN:
 * Human-readable purpose of this feature flag. The description helps developers understand
 * the purpose and scope of the feature flag.
 *
 * ###### RU:
 * Понятное человеку назначение этого фича-флага. Описание помогает разработчикам понять
 * назначение и область применения фича-флага.
 *
 * @see FeatureFlagSpec
 */
public inline fun <reified Value : Any> MutableFeatureFlagSpec(
    id: String,
    defaultValue: Value,
    description: String? = null,
): MutableFeatureFlagSpec<Value> = MutableFeatureFlagSpec(id, defaultValue, Value::class, description)

/**
 * ###### EN:
 * Provides an implementation of [FeatureFlagSpec] for mutable feature flags delivered as
 * [kotlinx.coroutines.flow.Flow].
 * This feature flag can be changed during application runtime. Values for the feature flag are
 * provided by [io.github.pavelannin.vexillum.source.FeatureFlagSource].
 *
 * ###### RU:
 * Предоставляет реализацию [FeatureFlagSpec] для изменяемых фича-флага поставляемых ввиде
 * [kotlinx.coroutines.flow.Flow].
 * Этот фича-флаг может быть изменен во время работы приложения. Значения фича-флагам задают
 * [io.github.pavelannin.vexillum.source.FeatureFlagSource].
 *
 * @param Value
 * ###### EN:
 * Feature flag type.
 *
 * ###### RU:
 * Тип фича-флага.
 *
 * @param defaultValue
 * ###### EN:
 * Default feature flag value. The value can be changed during application runtime.
 *
 * ###### RU:
 * Значение фича-флага по умолчанию. Значение может быть именено во время выполенения приложения.
 *
 * @param startedStrategy
 * ###### EN:
 * Startup strategy for [kotlinx.coroutines.flow.Flow] when the feature flag is requested.
 *
 * ###### RU:
 * Стратгия запуска [kotlinx.coroutines.flow.Flow] когда запрашивается фича-флаг.
 *
 * ###### EN:
 * The default value will be used if no
 * [io.github.pavelannin.vexillum.source.FeatureFlagSource] returns a value for
 * this feature flag.
 *
 * ###### RU:
 * Значение по умолчанию будет исспользоваться если не один
 * [io.github.pavelannin.vexillum.source.FeatureFlagSource] не вернул значение для
 * этого фича-флага.
 *
 * @see FeatureFlagSpec
 */
public data class FlowFeatureFlagSpec<Value : Any>(
    override val id: String,
    public val defaultValue: Value,
    override val valueType: KClass<Value>,
    override val description: String? = null,
    public val startedStrategy: SharingStarted = SharingStarted.Lazily,
) : FeatureFlagSpec<Value>

/**
 * ###### EN:
 * Provides an implementation of [FeatureFlagSpec] for mutable feature flags delivered as
 * [kotlinx.coroutines.flow.Flow].
 * This feature flag can be changed during application runtime. Values for the feature flag are
 * provided by [io.github.pavelannin.vexillum.source.FeatureFlagSource].
 *
 * ###### RU:
 * Предоставляет реализацию [FeatureFlagSpec] для изменяемых фича-флага поставляемых ввиде
 * [kotlinx.coroutines.flow.Flow].
 * Этот фича-флаг может быть изменен во время работы приложения. Значения фича-флагам задают
 * [io.github.pavelannin.vexillum.source.FeatureFlagSource].
 *
 * @param Value
 * ###### EN:
 * Feature flag type.
 *
 * ###### RU:
 * Тип фича-флага.
 *
 * @param id
 * ###### EN:
 * Unique feature flag identifier.
 *
 * ###### RU:
 * Уникальный идентификатор фича-флага.
 *
 * @param defaultValue
 * ###### EN:
 * Default feature flag value. The value can be changed during application runtime.
 *
 * ###### RU:
 * Значение фича-флага по умолчанию. Значение может быть именено во время выполенения приложения.
 *
 * @param startedStrategy
 * ###### EN:
 * Startup strategy for [kotlinx.coroutines.flow.Flow] when the feature flag is requested.
 *
 * ###### RU:
 * Стратгия запуска [kotlinx.coroutines.flow.Flow] когда запрашивается фича-флаг.
 *
 * ###### EN:
 * The default value will be used if no
 * [io.github.pavelannin.vexillum.source.FeatureFlagSource] returns a value for
 * this feature flag.
 *
 * ###### RU:
 * Значение по умолчанию будет исспользоваться если не один
 * [io.github.pavelannin.vexillum.source.FeatureFlagSource] не вернул значение для
 * этого фича-флага.
 *
 * @param description
 * ###### EN:
 * Human-readable purpose of this feature flag. The description helps developers understand
 * the purpose and scope of the feature flag.
 *
 * ###### RU:
 * Понятное человеку назначение этого фича-флага. Описание помогает разработчикам понять
 * назначение и область применения фича-флага.
 *
 * @see FeatureFlagSpec
 */
public inline fun <reified Value : Any> FlowFeatureFlagSpec(
    id: String,
    defaultValue: Value,
    description: String? = null,
    startedStrategy: SharingStarted = SharingStarted.Lazily,
): FlowFeatureFlagSpec<Value> = FlowFeatureFlagSpec(id, defaultValue, Value::class, description, startedStrategy)

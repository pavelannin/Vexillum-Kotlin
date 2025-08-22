package io.github.pavelannin.vexillum

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

/**
 * ###### EN
 * Represents a container that stores a collection of flag features.
 * To organize flag features into logical groups or namespaces.
 * ###### RU
 * Представляет контейнер, который хранит коллекцию фичи флагов.
 * Для организации фичи флагов в логические группы или пространства имен.
 */
public interface FeatureFlagSpaceable {
    /**
     * ###### EN
     * The name identifying this feature flag space.
     * ###### RU
     * Имя, идентифицирующее пространство фичи флагов.
     */
    public val name: String

    /**
     * ###### EN
     * Description explaining the purpose of this feature flag space.
     * ###### RU
     * Описание, объясняющее назначение этого пространства фичи флагов.
     */
    public val description: String?

    /**
     * ###### EN
     * The set of all feature flags contained within this space.
     * ###### RU
     * Коллекция всех фичи флагов, содержащихся в этом пространстве.
     */
    public val featureFlags: Set<FeatureFlag<*>>
}

/**
 * ###### EN
 * An implementation of [FeatureFlagSpaceable] for creating named feature spaces of flags.
 * Provides methods for creating [CompileFeatureFlag] and [RuntimeFeatureFlag].
 * ###### RU
 * Реализация [FeatureFlagSpaceable] для создания именованных пространств фичи флагов.
 * Предоставляет методы для создания [CompileFeatureFlag] и [RuntimeFeatureFlag].
 */
public abstract class FeatureFlagSpace(
    override val name: String,
    override val description: String? = null,
) : FeatureFlagSpaceable {
    override val featureFlags: Set<FeatureFlag<*>> get() = _featureFlags.toSet()
    private val _featureFlags = mutableSetOf<FeatureFlag<*>>()

    /**
     * ###### EN
     * Creates [CompileFeatureFlag].
     * ###### RU
     * Создает [CompileFeatureFlag].
     */
    public fun <Payload : Any> compile(
        key: String,
        enabled: Boolean,
        payload: Payload,
        description: String? = null,
    ): ReadOnlyProperty<FeatureFlagSpace, CompileFeatureFlag<Payload>> {
        val flag = CompileFeatureFlag(key, enabled, payload, description)
        _featureFlags += flag
        return ReadOnlyProperty { _, _ -> flag }
    }

    /**
     * ###### EN
     * Creates [RuntimeFeatureFlag].
     * ###### RU
     * Создает [RuntimeFeatureFlag].
     */
    public fun <Payload : Any> runtime(
        key: String,
        defaultEnabled: Boolean,
        defaultPayload: Payload,
        payloadType: KClass<Payload>,
        description: String? = null,
    ): ReadOnlyProperty<FeatureFlagSpace, RuntimeFeatureFlag<Payload>> {
        val flag = RuntimeFeatureFlag(key, defaultEnabled, defaultPayload, payloadType, description)
        _featureFlags += flag
        return ReadOnlyProperty { _, _ -> flag }
    }
}

/**
 * ###### EN
 * Creates [CompileFeatureFlag] without payload.
 * ###### RU
 * Создает [CompileFeatureFlag] без дополнительных данных.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun FeatureFlagSpace.compile(
    key: String,
    enabled: Boolean,
    description: String? = null,
): ReadOnlyProperty<FeatureFlagSpace, CompileFeatureFlag<Unit>> {
    return compile(key, enabled, Unit, description)
}

/**
 * ###### EN
 * Creates [RuntimeFeatureFlag]  with reified payload type.
 * ###### RU
 * Создает [RuntimeFeatureFlag] с определением типа дополнительных данных.
 */
public inline fun <reified Payload : Any> FeatureFlagSpace.runtime(
    key: String,
    defaultEnabled: Boolean,
    defaultPayload: Payload,
    description: String? = null,
): ReadOnlyProperty<FeatureFlagSpace, RuntimeFeatureFlag<Payload>> {
    return runtime(key, defaultEnabled, defaultPayload, Payload::class, description)
}

/**
 * ###### EN
 * Creates [RuntimeFeatureFlag] without payload.
 * ###### RU
 * Создает [RuntimeFeatureFlag] без дополнительных данных.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun FeatureFlagSpace.runtime(
    key: String,
    defaultEnabled: Boolean,
    description: String? = null,
): ReadOnlyProperty<FeatureFlagSpace, RuntimeFeatureFlag<Unit>> {
    return runtime(key, defaultEnabled, Unit, description)
}

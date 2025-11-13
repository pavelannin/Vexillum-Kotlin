package io.github.pavelannin.vexillum.persistent

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.pavelannin.vexillum.FeatureFlagSpec
import io.github.pavelannin.vexillum.FlowFeatureFlagSpec
import io.github.pavelannin.vexillum.MutableFeatureFlagSpec
import io.github.pavelannin.vexillum.source.FeatureFlagMutableSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializerOrNull

/**
 * ###### EN:
 * A persistent implementation of [FeatureFlagMutableSource] that stores runtime feature flag
 * values using Android's DataStore Preferences. This implementation provides durable storage that
 * persists across application restarts.
 *
 * **Important**: This implementation only caches primitive payload types
 * (String, Int, Boolean, etc.) and does not support complex object serialization. For complex
 * payload types, consider using a different storage mechanism or serializing the payload to a
 * primitive format.
 *
 * ###### RU:
 * Реализация [FeatureFlagMutableSource], которая хранит значения фичи флагов с использованием
 * Android DataStore Preferences. Эта реализация предоставляет долговременное хранилище,
 * которое сохраняется между перезапусками приложения.
 *
 * **Важно**: Эта реализация кеширует только примитивные типы полезной нагрузки
 * (String, Int, Boolean и т.д.) и не поддерживает сериализацию сложных объектов.
 * Для сложных типов полезной нагрузки рекомендуется использовать другой механизм хранения
 * или сериализовать полезную нагрузку в примитивный формат.
 *
 * @param dataStore
 * ###### EN:
 * The [DataStore] instance used for persistent storage of feature flag values.
 * ###### RU:
 * Экземпляр [DataStore], используемый для постоянного хранения значений фичи флагов.
 * @param dispatcher
 * ###### EN:
 * The coroutine dispatcher to use for all [DataStore] operations.
 * ###### RU:
 * Диспетчер корутин для использования во всех операциях [DataStore].
 */
public class VexillumPreferencesPersistentSource(
    private val dataStore: DataStore<Preferences>,
    override val id: String = "vexillum_preferences_persistent_source",
    override val description: String? = "The source stores the values of the feature flags in non-volatile memory (hash table).",
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    },
) : FeatureFlagMutableSource {
    override suspend fun <Value : Any> get(spec: MutableFeatureFlagSpec<Value>): Value? {
        val preferences = dataStore.data.firstOrNull() ?: return null
        return preferences[spec]
    }

    override fun <Value : Any> get(spec: FlowFeatureFlagSpec<Value>): Flow<Value?> {
        return dataStore.data.map { pref -> pref[spec] }
    }

    override suspend fun <Value : Any> update(spec: FeatureFlagSpec<Value>, block: suspend (Value?) -> Value) {
        withContext(dispatcher) {
            dataStore.edit { pref ->
                val value = pref[spec]
                pref[spec] = block(value)
            }
        }
    }

    override suspend fun <Value : Any> remove(spec: FeatureFlagSpec<Value>) {
        withContext(dispatcher) {
            dataStore.edit { pref -> pref.remove(spec) }
        }
    }

    override suspend fun clear() {
        withContext(dispatcher) {
            dataStore.edit { pref -> pref.clear() }
        }
    }

    @OptIn(InternalSerializationApi::class)
    private fun <Value : Any> checkKSerializer(spec: FeatureFlagSpec<Value>): KSerializer<Value> {
        return checkNotNull(spec.valueType.serializerOrNull()) {
            """
                Serializer for class '${spec.valueType}' is not found.
                Please ensure that class is marked as '@Serializable' and that the serialization compiler plugin is applied.
            """.trimIndent()
        }
    }

    private fun FeatureFlagSpec<*>.key(): Preferences.Key<String> {
        return stringPreferencesKey(id)
    }

    private suspend operator fun <Value : Any> Preferences.get(spec: FeatureFlagSpec<Value>): Value? {
        val raw = get(spec.key()) ?: return null
        val kSerializer = checkKSerializer(spec)
        return withContext(dispatcher) {
            json.decodeFromString(kSerializer, raw)
        }
    }

    internal suspend operator fun <Value : Any> MutablePreferences.set(spec: FeatureFlagSpec<Value>, value: Value): MutablePreferences {
        val kSerializer = checkKSerializer(spec)
        val raw = withContext(dispatcher) {
            json.encodeToString(kSerializer, value)
        }
        return apply {
            set(spec.key(), raw)
        }
    }

    internal fun <Value : Any> MutablePreferences.remove(spec: FeatureFlagSpec<Value>): MutablePreferences {
        val key = spec.key()
        if (contains(key)) {
            remove(key)
        }
        return this
    }

    internal companion object {
        internal const val DATASTORE_FILE_NAME = "vexillum.pref"
    }
}

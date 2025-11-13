package io.github.pavelannin.vexillum.persistent

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.github.pavelannin.vexillum.source.FeatureFlagMutableSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath

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
 */
public fun VexillumPreferencesPersistentSource(
    context: Context,
    fileName: String = VexillumPreferencesPersistentSource.DATASTORE_FILE_NAME,
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
    migrations: List<DataMigration<Preferences>> = listOf(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
): VexillumPreferencesPersistentSource {
    return VexillumPreferencesPersistentSource(
        dataStore = PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = corruptionHandler,
            migrations = migrations,
        ) { context.filesDir.resolve(fileName).absolutePath.toPath() },
        dispatcher = dispatcher,
    )
}

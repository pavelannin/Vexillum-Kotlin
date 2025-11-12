package io.github.pavelannin.vexillum.source.memory

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

internal class ObservableMutableMap<Key, Value>(
    private val map: MutableMap<Key, Value> = mutableMapOf()
) : MutableMap<Key, Value> by map {
    private val sharedFlow = MutableStateFlow<Map<Key, Value>>(map.toMap())

    override fun put(key: Key, value: Value): Value? = mutate { map.put(key, value) }
    override fun putAll(from: Map<out Key, Value>) = mutate { map.putAll(from) }
    override fun remove(key: Key): Value? = mutate { map.remove(key) }
    override fun clear() = mutate { map.clear() }

    fun observe(key: Key): Flow<Value?> = sharedFlow
        .map { it[key] }
        .distinctUntilChanged()

    private fun <T> mutate(block: MutableMap<Key, Value>.() -> T): T {
        val value = map.let(block)
        sharedFlow.update { map.toMap() }
        return value
    }
}
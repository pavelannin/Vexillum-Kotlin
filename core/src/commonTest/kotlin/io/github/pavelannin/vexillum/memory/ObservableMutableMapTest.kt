package io.github.pavelannin.vexillum.memory

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import io.github.pavelannin.vexillum.source.memory.ObservableMutableMap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ObservableMutableMapTest {
    private lateinit var observableMap: ObservableMutableMap<String, Int>

    @BeforeTest
    fun setUp() {
        observableMap = ObservableMutableMap()
    }

    @Test
    fun `should emit initial empty map`() = runTest {
        val key = "testKey"
        val initialValue = observableMap.observe(key).first()

        assertNull(initialValue)
    }

    @Test
    fun `should emit value when put is called`() = runTest {
        val key = "testKey"
        val value = 42

        observableMap.observe(key).test {
            assertNull(awaitItem())

            observableMap[key] = value
            assertEquals(42, awaitItem())

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit updated value when same key is put again`() = runTest {
        val key = "testKey"
        observableMap[key] = 42
        observableMap.observe(key).test {
            assertEquals(42, awaitItem())

            observableMap[key] = 100
            assertEquals(100, awaitItem())

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit null when key is removed`() = runTest {
        val key = "testKey"
        observableMap[key] = 42

        observableMap.observe(key).test {
            assertEquals(42, awaitItem())

            observableMap.remove(key)
            assertNull(awaitItem())

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should not emit when different key is modified`() = runTest {
        val key1 = "key1"
        val key2 = "key2"

        observableMap[key1] = 42
        observableMap.observe(key1).test {
            assertEquals(42, awaitItem())

            observableMap[key2] = 100
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit null when map is cleared`() = runTest {
        val key = "testKey"
        observableMap[key] = 42
        observableMap["otherKey"] = 100

        observableMap.observe(key).test {
            assertEquals(42, awaitItem())

            observableMap.clear()
            assertNull(awaitItem())

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle putAll correctly`() = runTest {
        val key1 = "key1"
        val key2 = "key2"
        turbineScope {
            val flow1 = observableMap.observe(key1).testIn(backgroundScope)
            val flow2 = observableMap.observe(key2).testIn(backgroundScope)

            assertNull(flow1.awaitItem())
            assertNull(flow2.awaitItem())

            observableMap.putAll(mapOf(key1 to 42, key2 to 100))
            assertEquals(42, flow1.awaitItem())
            assertEquals(100, flow2.awaitItem())

            flow1.expectNoEvents()
            flow2.expectNoEvents()

            flow1.cancelAndIgnoreRemainingEvents()
            flow2.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should return correct value from put operation`() {
        val key = "testKey"
        val value = 42

        val result = observableMap.put(key, value)

        assertNull(result)
        assertEquals(value, observableMap[key])
    }

    @Test
    fun `should return previous value from put operation`() {
        val key = "testKey"
        val initialValue = 42
        val newValue = 100
        observableMap[key] = initialValue

        val result = observableMap.put(key, newValue)

        assertEquals(initialValue, result)
        assertEquals(newValue, observableMap[key])
    }

    @Test
    fun `should return correct value from remove operation`() {
        val key = "testKey"
        val value = 42
        observableMap[key] = value

        val result = observableMap.remove(key)

        assertEquals(value, result)
        assertNull(observableMap[key])
    }

    @Test
    fun `should return null from remove operation for non-existent key`() {
        val key = "nonExistentKey"
        val result = observableMap.remove(key)
        assertNull(result)
    }

    @Test
    fun `should implement MutableMap interface correctly`() {
        observableMap["key1"] = 1
        observableMap["key2"] = 2

        assertEquals(2, observableMap.size)
        assertTrue(observableMap.containsKey("key1"))
        assertTrue(observableMap.containsValue(2))
        assertEquals(1, observableMap["key1"])

        observableMap.clear()
        assertTrue(observableMap.isEmpty())
    }

    @Test
    fun `should work with custom initial map`() = runTest {
        val initialMap = mutableMapOf("key1" to 1, "key2" to 2)
        val customObservableMap = ObservableMutableMap(initialMap)

        val value = customObservableMap.observe("key1").first()

        assertEquals(1, value)
        assertEquals(2, customObservableMap.size)
    }

    @Test
    fun `should only emit distinct values`() = runTest {
        val key = "testKey"
        observableMap[key] = 42

        observableMap.observe(key).test {
            observableMap[key] = 42
            observableMap[key] = 42
            observableMap[key] = 42

            assertEquals(42, awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
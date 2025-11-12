package io.github.pavelannin.vexillum.memory

import app.cash.turbine.test
import io.github.pavelannin.vexillum.FlowFeatureFlagSpec
import io.github.pavelannin.vexillum.MutableFeatureFlagSpec
import io.github.pavelannin.vexillum.source.memory.VexillumMemorySource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class VexillumSourceTest {
    private lateinit var source: VexillumMemorySource
    private lateinit var mutableFlag1: MutableFeatureFlagSpec<String>
    private lateinit var mutableFlag2: MutableFeatureFlagSpec<Int>
    private lateinit var flowFlag1: FlowFeatureFlagSpec<String>
    private lateinit var flowFlag2: FlowFeatureFlagSpec<Int>

    @BeforeTest
    fun setUp() {
        source = VexillumMemorySource()
        mutableFlag1 = MutableFeatureFlagSpec("mut_flag_1", "default")
        mutableFlag2 = MutableFeatureFlagSpec("mut_flag_2", 42)
        flowFlag1 = FlowFeatureFlagSpec("flow_flag_1", "default")
        flowFlag2 = FlowFeatureFlagSpec("flow_flag_2", 42)
    }

    @Test
    fun `get should return null for non-existent flag`() = runTest {
        val result = source[mutableFlag1]
        assertNull(result)
    }

    @Test
    fun `get should return value after update`() = runTest {
        source.update(mutableFlag1) { "updated_payload" }

        val result = source[mutableFlag1]

        assertNotNull(result)
        assertEquals("updated_payload", result)
    }

    @Test
    fun `observe should emit null initially for non-existent flag`() = runTest {
        source[flowFlag1].test {
            assertNull(awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observe should emit values after updates`() = runTest {
        source[flowFlag1].test {
            assertNull(awaitItem())

            source.update(flowFlag1) { "first_update" }
            assertEquals("first_update", awaitItem())

            source.update(flowFlag1) { "second_update" }
            assertEquals("second_update", awaitItem())

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `update should create new value when none exists`() = runTest {
        source.update(mutableFlag1) {
            assertEquals("default", it)
            "new"
        }

        val result = source[mutableFlag1]
        assertEquals("new", result)
    }

    @Test
    fun `update should modify existing value`() = runTest {
        source.update(mutableFlag1) { "initial" }

        source.update(mutableFlag1) {
            assertEquals("initial", it)
            "modified"
        }

        val result = source[mutableFlag1]
        assertEquals("modified", result)
    }

    @Test
    fun `remove should delete existing value`() = runTest {
        source.update(mutableFlag1) { "test" }

        source.remove(mutableFlag1)

        val result = source[mutableFlag1]
        assertNull(result)
    }

    @Test
    fun `clear should remove all values`() = runTest {
        source.update(mutableFlag1) { "payload_1" }
        source.update(mutableFlag2) { 100 }
        source.clear()

        assertNull(source[mutableFlag1])
        assertNull(source[mutableFlag2])
    }

    @Test
    fun `should handle multiple flags independently`() = runTest {
        source.update(mutableFlag1) { "string" }
        source.update(mutableFlag2) { 123 }

        val result1 = source[mutableFlag1]
        val result2 = source[mutableFlag2]

        assertEquals("string", result1)
        assertEquals(123, result2)
    }

    @Test
    fun `observe should work for multiple flags`() = runTest {
        val flow1 = source[flowFlag1]
        val flow2 = source[flowFlag2]

        source.update(flowFlag1) { "test1" }
        source.update(flowFlag2) { 999 }

        val value1 = flow1.first { it != null }
        val value2 = flow2.first { it != null }

        assertEquals("test1", value1)
        assertEquals(999, value2)
    }
}
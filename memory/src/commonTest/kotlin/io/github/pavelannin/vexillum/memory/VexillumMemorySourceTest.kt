package io.github.pavelannin.vexillum.memory

import app.cash.turbine.test
import io.github.pavelannin.vexillum.FeatureFlagSpace
import io.github.pavelannin.vexillum.RuntimeFeatureFlag
import io.github.pavelannin.vexillum.runtime
import io.github.pavelannin.vexillum.runtime.RuntimeFeatureFlagMomentValue
import io.github.pavelannin.vexillum.runtime.RuntimeFeatureFlagValue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(RuntimeFeatureFlagMomentValue::class)
class VexillumMemorySourceTest {
    private lateinit var memorySource: VexillumMemorySource
    private lateinit var featureFlagSpace: FeatureFlagSpace
    private lateinit var testFlag: RuntimeFeatureFlag<String>
    private lateinit var testFlag2: RuntimeFeatureFlag<Int>

    @BeforeTest
    fun setUp() {
        memorySource = VexillumMemorySource()
        featureFlagSpace = object : FeatureFlagSpace(name = "Test") {}
        val delegate = featureFlagSpace.runtime(
            key = "test_flag",
            defaultEnabled = false,
            defaultPayload = "default_payload",
        )
        testFlag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)

        val delegate2 = featureFlagSpace.runtime(
            key = "test_flag_2",
            defaultEnabled = true,
            defaultPayload = 42,
        )
        testFlag2 = delegate2.getValue(featureFlagSpace, ::featureFlagSpace)
    }

    @Test
    fun `get should return null for non-existent flag`() = runTest {
        val result = memorySource[testFlag]
        assertNull(result)
    }

    @Test
    fun `get should return value after update`() = runTest {
        memorySource.update(testFlag) { RuntimeFeatureFlagValue(true, "updated_payload") }

        val result = memorySource[testFlag]

        assertNotNull(result)
        assertEquals(true, result.isEnabled)
        assertEquals("updated_payload", result.payload)
    }

    @Test
    fun `observe should emit null initially for non-existent flag`() = runTest {
        memorySource.observe(testFlag).test {
            assertNull(awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observe should emit values after updates`() = runTest {
        memorySource.observe(testFlag).test {
            assertNull(awaitItem())

            memorySource.update(testFlag) { it.copy(payload = "first_update") }
            assertEquals("first_update", awaitItem()?.payload)

            memorySource.update(testFlag) { it.copy(payload = "second_update") }
            assertEquals("second_update", awaitItem()?.payload)

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `update should create new value when none exists`() = runTest {
        memorySource.update(testFlag) {
            assertEquals("default_payload", it.payload)
            it.copy(true, "new_payload")
        }

        val result = memorySource[testFlag]
        assertEquals("new_payload", result?.payload)
        assertEquals(true, result?.isEnabled)
    }

    @Test
    fun `update should modify existing value`() = runTest {
        memorySource.update(testFlag) { it.copy(true, "initial_payload") }

        memorySource.update(testFlag) {
            assertEquals("initial_payload", it.payload)
            it.copy(false, "modified_payload")
        }

        val result = memorySource[testFlag]
        assertEquals("modified_payload", result?.payload)
        assertEquals(false, result?.isEnabled)
    }

    @Test
    fun `remove should delete existing value`() = runTest {
        memorySource.update(testFlag) { it.copy(true, "test_payload") }

        memorySource.remove(testFlag)

        val result = memorySource[testFlag]
        assertNull(result)
    }

    @Test
    fun `clear should remove all values`() = runTest {
        memorySource.update(testFlag) { it.copy(true, "payload_1") }
        memorySource.update(testFlag2) { it.copy(false, 100) }
        memorySource.clear()

        assertNull(memorySource[testFlag])
        assertNull(memorySource[testFlag2])
    }

    @Test
    fun `should handle multiple flags independently`() = runTest {
        memorySource.update(testFlag) { it.copy(true, "string_payload") }
        memorySource.update(testFlag2) { it.copy(false, 123) }

        val result1 = memorySource[testFlag]
        val result2 = memorySource[testFlag2]

        assertEquals("string_payload", result1?.payload)
        assertEquals(true, result1?.isEnabled)
        assertEquals(123, result2?.payload)
        assertEquals(false, result2?.isEnabled)
    }

    @Test
    fun `observe should work for multiple flags`() = runTest {
        val flow1 = memorySource.observe(testFlag)
        val flow2 = memorySource.observe(testFlag2)

        memorySource.update(testFlag) { it.copy(true, "test1") }
        memorySource.update(testFlag2) { it.copy(false, 999) }

        val value1 = flow1.first { it != null }
        val value2 = flow2.first { it != null }

        assertEquals("test1", value1?.payload)
        assertEquals(999, value2?.payload)
    }
}
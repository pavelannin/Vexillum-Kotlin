package io.github.pavelannin.vexillum

import app.cash.turbine.test
import io.github.pavelannin.vexillum.memory.MemoryVexillum
import io.github.pavelannin.vexillum.runtime.RuntimeFeatureFlagMomentValue
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(RuntimeFeatureFlagMomentValue::class)
class MemoryVexillumTest {
    private lateinit var vexillum: MemoryVexillum

    @BeforeTest
    fun setup() {
        vexillum = MemoryVexillum()
    }


    @Test
    fun `isEnabled returns correct values for compile flags`() = runTest {
        val trueFlag = CompileFeatureFlag(
            key = "test1",
            isEnabled = true,
            payload = "default"
        )
        val falseFlag = CompileFeatureFlag(
            key = "test2",
            isEnabled = false,
            payload = "default"
        )

        assertTrue(vexillum.isEnabled(trueFlag))
        assertFalse(vexillum.isEnabled(falseFlag))
    }

    @Test
    fun `isEnabled returns default for runtime flags when not set`() = runTest {
        val flag1 = RuntimeFeatureFlag(
            key = "test1",
            defaultEnabled = true,
            defaultPayload = "default",
        )
        val flag2 = RuntimeFeatureFlag(
            key = "test1",
            defaultEnabled = false,
            defaultPayload = "default",
        )

        assertTrue(vexillum.isEnabled(flag1))
        assertFalse(vexillum.isEnabled(flag2))
    }

    @Test
    fun `payload returns default for runtime flags when not set`() = runTest {
        val flag1 = RuntimeFeatureFlag(
            key = "test1",
            defaultEnabled = false,
            defaultPayload = "default",
        )
        val flag2 = RuntimeFeatureFlag(
            key = "test2",
            defaultEnabled = false,
            defaultPayload = "default_default",
        )

        assertEquals("default", vexillum.payload(flag1))
        assertEquals("default_default", vexillum.payload(flag2))
    }

    @Test
    fun `update and observeEnabled work correctly`() = runTest {
        val flag = RuntimeFeatureFlag(
            key = "test1",
            defaultEnabled = false,
            defaultPayload = "default",
        )

        vexillum.observeEnabled(flag).test {
            assertEquals(false, awaitItem())

            vexillum.update(flag) { it.copy(isEnabled = true) }
            assertEquals(true, awaitItem())

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `update and observePayload work correctly`() = runTest {
        val flag = RuntimeFeatureFlag(
            key = "test1",
            defaultEnabled = false,
            defaultPayload = "default",
        )

        vexillum.observePayload(flag).test {
            assertEquals("default", awaitItem())

            vexillum.update(flag) { it.copy(payload = "updated") }
            assertEquals("updated", awaitItem())

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `remove reverts to default values`() = runTest {
        val flag = RuntimeFeatureFlag(
            key = "test1",
            defaultEnabled = false,
            defaultPayload = "default",
        )

        vexillum.update(flag) { it.copy(isEnabled = true, payload = "updated") }
        assertTrue(vexillum.isEnabled(flag))
        assertEquals("updated", vexillum.payload(flag))

        vexillum.remove(flag)
        assertFalse(vexillum.isEnabled(flag))
        assertEquals("default", vexillum.payload(flag))
    }

    @Test
    fun `clear removes all flags`() = runTest {
        val flag1 = RuntimeFeatureFlag(
            key = "test1",
            defaultEnabled = false,
            defaultPayload = "default1",
        )
        val flag2 = RuntimeFeatureFlag(
            key = "test2",
            defaultEnabled = true,
            defaultPayload = "default2",
        )

        vexillum.update(flag1) { it.copy(isEnabled =  true, payload = "updated1") }
        vexillum.update(flag2) { it.copy(isEnabled =  false, payload = "updated2") }

        vexillum.clear()

        assertFalse(vexillum.isEnabled(flag1))
        assertEquals("default1", vexillum.payload(flag1))
        assertTrue(vexillum.isEnabled(flag2))
        assertEquals("default2", vexillum.payload(flag2))
    }
}
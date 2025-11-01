package io.github.pavelannin.vexillum

import app.cash.turbine.test
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import io.github.pavelannin.vexillum.runtime.RuntimeFeatureFlagMomentValue
import io.github.pavelannin.vexillum.runtime.RuntimeFeatureFlagValue
import io.github.pavelannin.vexillum.source.FeatureFlagSource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(RuntimeFeatureFlagMomentValue::class)
class VexillumDefaultTest {
    private lateinit var vexillum: VexillumDefault
    private lateinit var source1: FeatureFlagSource
    private lateinit var source2: FeatureFlagSource
    private lateinit var featureFlagSpace: FeatureFlagSpace

    @BeforeTest
    fun setUp() {
        source1 = mock()
        source2 = mock()
        vexillum = VexillumDefault(listOf(source1, source2))
        featureFlagSpace = object : FeatureFlagSpace(name = "Test") {}
    }

    @Test
    fun `isEnabled for CompileFeatureFlag should return flag's enabled state`() {
        val delegate = featureFlagSpace.compile("key", true)
        val flag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)

        val result = vexillum.isEnabled(flag)

        assertTrue(result)
        verify { flag.isEnabled }
    }

    @Test
    fun `isEnabled for RuntimeFeatureFlag should return value from first available source`() = runTest {
        val delegate = featureFlagSpace.runtime("key", false)
        val flag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)
        val featureValue = RuntimeFeatureFlagValue(true, Unit)

        every { source1[flag] } returns featureValue
        every { source2[flag] } returns null

        val result = vexillum.isEnabled(flag)

        assertTrue(result)
        verify { source1[flag] }
        verify(VerifyMode.exactly(0)) { source2[flag] }
    }

    @Test
    fun `isEnabled for RuntimeFeatureFlag should return default when no sources have value`() = runTest {
        val delegate = featureFlagSpace.runtime("key", true)
        val flag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)

        every { source1[flag] } returns null
        every { source2[flag] } returns null

        val result = vexillum.isEnabled(flag)

        assertTrue(result)
        verify { source1[flag] }
        verify { source2[flag] }
    }

    @Test
    fun `observeEnabled should merge sources and emit values`() = runTest {
        val delegate = featureFlagSpace.runtime("key", false)
        val flag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)
        val featureValue1 = RuntimeFeatureFlagValue(true, Unit)
        val featureValue2 = RuntimeFeatureFlagValue(false, Unit)

        every { source1.observe(flag) } returns flowOf(featureValue1, null)
        every { source2.observe(flag) } returns flowOf(null, featureValue2)
        every { source1[flag] } returns null
        every { source2[flag] } returns null

        vexillum.observeEnabled(flag).test {
            assertFalse(awaitItem())
            assertTrue(awaitItem())
            assertFalse(awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `payload for CompileFeatureFlag should return flag's payload`() {
        val testPayload = "test_payload"
        val delegate = featureFlagSpace.compile("key", false, payload = testPayload)
        val flag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)

        val result = vexillum.payload(flag)

        assertEquals(testPayload, result)
        verify { flag.payload }
    }

    @Test
    fun `payload for RuntimeFeatureFlag should return value from first available source`() = runTest {
        val delegate = featureFlagSpace.runtime("key", defaultEnabled = false, defaultPayload = "default_payload")
        val flag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)
        val testPayload = "test_payload"
        val featureValue = RuntimeFeatureFlagValue(false, testPayload)

        every { source1[flag] } returns featureValue
        every { source2[flag] } returns null

        val result = vexillum.payload(flag)

        assertEquals(testPayload, result)
        verify { source1[flag] }
        verify(VerifyMode.exactly(0)) { source2[flag] }
    }

    @Test
    fun `payload for RuntimeFeatureFlag should return default when no sources have value`() = runTest {
        val defaultPayload = "default_payload"
        val delegate = featureFlagSpace.runtime("key", defaultEnabled = false, defaultPayload = defaultPayload)
        val flag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)

        every { source1[flag] } returns null
        every { source2[flag] } returns null

        val result = vexillum.payload(flag)

        assertEquals(defaultPayload, result)
        verify { source1[flag] }
        verify { source2[flag] }
    }

    @Test
    fun `observePayload should merge sources and emit values`() = runTest {
        val delegate = featureFlagSpace.runtime("key", defaultEnabled = false, defaultPayload = "default_payload")
        val flag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)
        val featureValue1 = RuntimeFeatureFlagValue(false, "payload1")
        val featureValue2 = RuntimeFeatureFlagValue(false, "payload2")

        every { source1.observe(flag) } returns flowOf(featureValue1, null)
        every { source2.observe(flag) } returns flowOf(null, featureValue2)
        every { source1[flag] } returns null
        every { source2[flag] } returns null

        val result = vexillum.observePayload(flag).test {
            assertEquals("default_payload", awaitItem())
            assertEquals("payload1", awaitItem())
            assertEquals("payload2", awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}

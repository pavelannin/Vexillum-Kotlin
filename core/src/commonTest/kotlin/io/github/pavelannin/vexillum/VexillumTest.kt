package io.github.pavelannin.vexillum

import app.cash.turbine.test
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.github.pavelannin.vexillum.interceptor.FeatureFlagInterceptor
import io.github.pavelannin.vexillum.source.FeatureFlagSource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VexillumTest {
    private lateinit var vexillum: Vexillum
    private lateinit var source1: FeatureFlagSource
    private lateinit var source2: FeatureFlagSource
    private lateinit var interceptor1: FeatureFlagInterceptor
    private lateinit var interceptor2: FeatureFlagInterceptor

    @BeforeTest
    fun setUp() {
        vexillum = Vexillum()
        source1 = mock()
        source2 = mock()
        interceptor1 = mock()
        interceptor2 = mock()
    }

    @Test
    fun `should initialize with provided sources and interceptors`() {
        val sources = setOf(source1, source2)
        val interceptors = setOf(interceptor1, interceptor2)

        val vexillum = Vexillum(sources, interceptors)

        assertEquals(sources, vexillum.allSources())
        assertEquals(interceptors, vexillum.allInterceptors())
    }

    @Test
    fun `should get immutable flag value with interceptors`() {
        val initialValue = "initial"
        val interceptedValue = "intercepted"
        val spec = ImmutableFeatureFlagSpec(id = "id", initialValue)

        // initial
        assertEquals(initialValue, vexillum[spec])

        // interceptor
        every { with(interceptor1) { vexillum.intercept(spec, initialValue) } } returns interceptedValue
        vexillum.addInterceptor(interceptor1)
        assertEquals(interceptedValue, vexillum[spec])
    }

    @Test
    fun `should get mutable flag value from source with interceptors`() = runTest {
        val defaultValue = 0
        val sourceValue = 42
        val interceptedValue = 100
        val spec = MutableFeatureFlagSpec(id = "id", defaultValue)

        // default
        assertEquals(defaultValue, vexillum[spec])

        // source
        everySuspend { source1[spec] } returns sourceValue
        vexillum.addSource(source1)
        assertEquals(sourceValue, vexillum[spec])

        // interceptor
        everySuspend { with(interceptor1) { vexillum.intercept(spec, source1, sourceValue) } } returns interceptedValue
        vexillum.addInterceptor(interceptor1)
        assertEquals(interceptedValue, vexillum[spec])
    }

    @Test
    fun `should get default value when no source provides mutable flag`() = runTest {
        val defaultValue = true
        val interceptedValue = false
        val spec = MutableFeatureFlagSpec(id = "id", defaultValue)

        // default
        assertEquals(defaultValue, vexillum[spec])

        // interceptor
        everySuspend {
            with(interceptor1) { vexillum.intercept(spec, FeatureFlagSource.DefaultValueSource, defaultValue) }
        } returns interceptedValue
        vexillum.addInterceptor(interceptor1)
        assertEquals(interceptedValue, vexillum[spec])
    }

    @Test
    fun `should get value when multiple source provides mutable flag`() = runTest {
        val defaultValue = "default"
        val source1Value = "source1_value"
        val source2Value = "source2_value"
        val spec = MutableFeatureFlagSpec(id = "id", defaultValue)

        everySuspend { source1[spec] } returns source1Value
        everySuspend { source2[spec] } returns source2Value
        vexillum.addSource(source1)
        vexillum.addSource(source2)
        assertEquals(source1Value, vexillum[spec])

        everySuspend { source1[spec] } returns null
        assertEquals(source2Value, vexillum[spec])

        everySuspend { source2[spec] } returns null
        assertEquals(defaultValue, vexillum[spec])
    }

    @Test
    fun `should get flow flag value from source with interceptors`() = runTest {
        val defaultValue = 0
        val sourceValue = 42
        val interceptedValue = 100
        val spec = FlowFeatureFlagSpec(id = "id", defaultValue)

        // default
        vexillum[spec].test {
            assertEquals(defaultValue, awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }

        // source
        every { source1[spec] } returns flowOf(sourceValue)
        vexillum.addSource(source1)
        vexillum[spec].test {
            assertEquals(defaultValue, awaitItem())
            assertEquals(sourceValue, awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }

        // interceptor
        everySuspend {
            with(interceptor1) { vexillum.intercept(spec, source1, sourceValue) }
        } returns interceptedValue
        vexillum.addInterceptor(interceptor1)
        vexillum[spec].test {
            assertEquals(defaultValue, awaitItem())
            assertEquals(interceptedValue, awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should get default value when no source provides flow flag`() = runTest {
        val defaultValue = true
        val interceptedValue = false
        val spec = FlowFeatureFlagSpec(id = "id", defaultValue)

        // default
        vexillum[spec].test {
            assertEquals(defaultValue, awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }

        // interceptor
        everySuspend {
            with(interceptor1) { vexillum.intercept(spec, FeatureFlagSource.DefaultValueSource, defaultValue) }
        } returns interceptedValue
        vexillum.addInterceptor(interceptor1)
        vexillum[spec].test {
            assertEquals(defaultValue, awaitItem())
            assertEquals(interceptedValue, awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should add and remove sources`() {
        assertTrue(vexillum.addSource(source1))
        assertFalse(vexillum.addSource(source1))
        assertEquals(setOf(source1), vexillum.allSources())

        assertTrue(vexillum.addSource(source2))
        assertEquals(setOf(source1, source2), vexillum.allSources())

        assertTrue(vexillum.removeSource(source1))
        assertEquals(setOf(source2), vexillum.allSources())

        assertFalse(vexillum.removeSource(source1))
    }

    @Test
    fun `should add and remove interceptors`() {
        assertTrue(vexillum.addInterceptor(interceptor1))
        assertFalse(vexillum.addInterceptor(interceptor1))
        assertEquals(setOf(interceptor1), vexillum.allInterceptors())

        assertTrue(vexillum.addInterceptor(interceptor2))
        assertEquals(setOf(interceptor1, interceptor2), vexillum.allInterceptors())

        assertTrue(vexillum.removeInterceptor(interceptor1))
        assertEquals(setOf(interceptor2), vexillum.allInterceptors())

        assertFalse(vexillum.removeInterceptor(interceptor1))
    }
}

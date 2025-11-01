package io.github.pavelannin.vexillum

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FeatureFlagSpaceTest {
    private lateinit var featureFlagSpace: FeatureFlagSpace

    @BeforeTest
    fun setUp() {
        featureFlagSpace = object : FeatureFlagSpace(name = "Test") {}
    }

    @Test
    fun `compile with payload should create flag and add to featureFlags`() {
        val payload = TestPayload("test")
        val delegate = featureFlagSpace.compile(
            key = "test_key",
            enabled = true,
            payload = payload,
            description = "test description"
        )
        val resultFlag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)

        assertEquals("test_key", resultFlag.key)
        assertTrue(resultFlag.isEnabled)
        assertEquals(payload, resultFlag.payload)
        assertEquals("test description", resultFlag.description)
        assertTrue(featureFlagSpace.featureFlags.contains(resultFlag))
    }

    @Test
    fun `compile without payload should create Unit flag`() {
        val delegate = featureFlagSpace.compile(
            key = "test_key",
            enabled = false,
            description = "test description"
        )
        val resultFlag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)

        assertEquals("test_key", resultFlag.key)
        assertFalse(resultFlag.isEnabled)
        assertEquals(Unit, resultFlag.payload as? Unit)
        assertEquals("test description", resultFlag.description)
        assertTrue(featureFlagSpace.featureFlags.contains(resultFlag))
    }

    @Test
    fun `runtime with payload should create flag with type`() {
        val payload = TestPayload("test")
        val delegate = featureFlagSpace.runtime(
            key = "test_key",
            defaultEnabled = true,
            defaultPayload = payload,
            payloadType = TestPayload::class,
            description = "test description"
        )

        val resultFlag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)

        assertEquals("test_key", resultFlag.key)
        assertTrue(resultFlag.defaultEnabled)
        assertEquals(payload, resultFlag.defaultPayload)
        assertEquals(TestPayload::class, resultFlag.payloadType)
        assertEquals("test description", resultFlag.description)
        assertTrue(featureFlagSpace.featureFlags.contains(resultFlag))
    }

    @Test
    fun `runtime with reified payload should create flag`() {
        val payload = TestPayload("test")
        val delegate = featureFlagSpace.runtime(
            key = "test_key",
            defaultEnabled = true,
            defaultPayload = payload,
            description = "test description"
        )

        val resultFlag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)

        assertEquals("test_key", resultFlag.key)
        assertTrue(resultFlag.defaultEnabled)
        assertEquals(payload, resultFlag.defaultPayload)
        assertEquals(TestPayload::class, resultFlag.payloadType)
        assertTrue(featureFlagSpace.featureFlags.contains(resultFlag))
    }

    @Test
    fun `runtime without payload should create Unit flag`() {
        val delegate = featureFlagSpace.runtime(
            key = "test_key",
            defaultEnabled = false,
            description = "test description"
        )

        val resultFlag = delegate.getValue(featureFlagSpace, ::featureFlagSpace)

        assertEquals("test_key", resultFlag.key)
        assertFalse(resultFlag.defaultEnabled)
        assertEquals(Unit, resultFlag.defaultPayload)
        assertEquals("test description", resultFlag.description)
        assertTrue(featureFlagSpace.featureFlags.contains(resultFlag))
    }

    data class TestPayload(val value: String)
}

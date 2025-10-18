package life.munay.mqtt.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MqttTopicUtilsTest {

    @Test
    fun `should validate publish topics correctly`() {
        // Valid publish topics
        assertTrue(MqttTopicUtils.isValidPublishTopic("device/123/data"))
        assertTrue(MqttTopicUtils.isValidPublishTopic("home/temperature"))
        assertTrue(MqttTopicUtils.isValidPublishTopic("sensor/humidity/living-room"))

        // Invalid publish topics
        assertFalse(MqttTopicUtils.isValidPublishTopic("")) // Empty
        assertFalse(MqttTopicUtils.isValidPublishTopic("device/+/data")) // Contains wildcard
        assertFalse(MqttTopicUtils.isValidPublishTopic("device/#")) // Contains wildcard
        assertFalse(MqttTopicUtils.isValidPublishTopic("\$SYS/broker/load")) // System topic
    }

    @Test
    fun `should validate subscription topics correctly`() {
        // Valid subscription topics
        assertTrue(MqttTopicUtils.isValidSubscriptionTopic("device/123/data"))
        assertTrue(MqttTopicUtils.isValidSubscriptionTopic("device/+/data"))
        assertTrue(MqttTopicUtils.isValidSubscriptionTopic("device/#"))
        assertTrue(MqttTopicUtils.isValidSubscriptionTopic("home/+/temperature"))

        // Invalid subscription topics
        assertFalse(MqttTopicUtils.isValidSubscriptionTopic("")) // Empty
        assertFalse(MqttTopicUtils.isValidSubscriptionTopic("\$SYS/broker/load")) // System topic
        assertFalse(MqttTopicUtils.isValidSubscriptionTopic("device/+data")) // Invalid wildcard
        assertFalse(MqttTopicUtils.isValidSubscriptionTopic("device/#/data")) // # not at end
    }

    @Test
    fun `should build device topics correctly`() {
        assertEquals("device/123/status", MqttTopicUtils.buildDeviceTopic("123", "status"))
        assertEquals("device/sensor001/data", MqttTopicUtils.buildDeviceTopic("sensor001", "data"))
    }

    @Test
    fun `should extract device ID correctly`() {
        assertEquals("123", MqttTopicUtils.extractDeviceId("device/123/status"))
        assertEquals("sensor001", MqttTopicUtils.extractDeviceId("device/sensor001/data"))
        assertNull(MqttTopicUtils.extractDeviceId("invalid/topic"))
        assertNull(MqttTopicUtils.extractDeviceId("home/temperature"))
    }

    @Test
    fun `should validate wildcard usage`() {
        // Valid single-level wildcards
        assertTrue(MqttTopicUtils.isValidSubscriptionTopic("+"))
        assertTrue(MqttTopicUtils.isValidSubscriptionTopic("device/+"))
        assertTrue(MqttTopicUtils.isValidSubscriptionTopic("+/temperature"))
        assertTrue(MqttTopicUtils.isValidSubscriptionTopic("home/+/sensor"))

        // Valid multi-level wildcards
        assertTrue(MqttTopicUtils.isValidSubscriptionTopic("#"))
        assertTrue(MqttTopicUtils.isValidSubscriptionTopic("device/#"))
        assertTrue(MqttTopicUtils.isValidSubscriptionTopic("home/living-room/#"))

        // Invalid wildcard usage
        assertFalse(MqttTopicUtils.isValidSubscriptionTopic("device/+data"))
        assertFalse(MqttTopicUtils.isValidSubscriptionTopic("device/data+"))
        assertFalse(MqttTopicUtils.isValidSubscriptionTopic("device/#/data"))
        assertFalse(MqttTopicUtils.isValidSubscriptionTopic("device/data#"))
    }
}

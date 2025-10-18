package life.munay.mqtt.utils

object MqttConstants {
    // QoS levels
    const val QOS_AT_MOST_ONCE = 0
    const val QOS_AT_LEAST_ONCE = 1
    const val QOS_EXACTLY_ONCE = 2

    // Default values
    const val DEFAULT_KEEP_ALIVE_INTERVAL = 60
    const val DEFAULT_CONNECTION_TIMEOUT = 30
    const val DEFAULT_QOS = QOS_AT_LEAST_ONCE

    // Topic separators and wildcards
    const val TOPIC_SEPARATOR = "/"
    const val SINGLE_LEVEL_WILDCARD = "+"
    const val MULTI_LEVEL_WILDCARD = "#"

    // Common topic patterns
    const val DEVICE_STATUS_TOPIC_PATTERN = "device/+/status"
    const val DEVICE_DATA_TOPIC_PATTERN = "device/+/data"
    const val SYSTEM_TOPIC_PATTERN = "\$SYS/#"
}

object MqttTopicUtils {

    /**
     * Validates if a topic is valid for publishing
     */
    fun isValidPublishTopic(topic: String): Boolean {
        return topic.isNotBlank() && !topic.contains(MqttConstants.SINGLE_LEVEL_WILDCARD) &&
            !topic.contains(MqttConstants.MULTI_LEVEL_WILDCARD) &&
            !topic.startsWith("\$") &&
            topic.length <= 65535
    }

    /**
     * Validates if a topic is valid for subscription
     */
    fun isValidSubscriptionTopic(topic: String): Boolean {
        return topic.isNotBlank() && !topic.startsWith("\$") &&
            topic.length <= 65535 &&
            isValidWildcardUsage(topic)
    }

    /**
     * Checks if wildcard usage in topic is valid
     */
    private fun isValidWildcardUsage(topic: String): Boolean {
        // Multi-level wildcard must be last character and preceded by separator
        val multiLevelIndex = topic.indexOf(MqttConstants.MULTI_LEVEL_WILDCARD)
        if (multiLevelIndex != -1) {
            if (multiLevelIndex != topic.length - 1) return false
            if (multiLevelIndex > 0 && topic[multiLevelIndex - 1] != '/') return false
        }

        // Single-level wildcard must be surrounded by separators or be the entire level
        val singleLevelIndices = topic.indices.filter { topic[it] == '+' }
        for (index in singleLevelIndices) {
            val prevChar = if (index > 0) topic[index - 1] else '/'
            val nextChar = if (index < topic.length - 1) topic[index + 1] else '/'
            if (prevChar != '/' || nextChar != '/') return false
        }

        return true
    }

    /**
     * Builds a device-specific topic
     */
    fun buildDeviceTopic(deviceId: String, subtopic: String): String {
        return "device/$deviceId/$subtopic"
    }

    /**
     * Extracts device ID from a device topic
     */
    fun extractDeviceId(topic: String): String? {
        val parts = topic.split(MqttConstants.TOPIC_SEPARATOR)
        return if (parts.size >= 3 && parts[0] == "device") {
            parts[1]
        } else {
            null
        }
    }
}

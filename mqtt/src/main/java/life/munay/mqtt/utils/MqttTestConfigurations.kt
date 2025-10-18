package life.munay.mqtt.utils

import life.munay.mqtt.models.MqttConnectionConfig
import java.util.UUID

/**
 * Utility class for creating test MQTT configurations with free public brokers
 */
object MqttTestConfigurations {

    /**
     * HiveMQ Public Broker - Recommended for testing HiveMQ client
     * - Host: broker.hivemq.com
     * - Port: 1883 (TCP), 8883 (SSL), 8000 (WebSocket)
     * - No authentication required
     */
    fun hiveMqPublicBroker(): MqttConnectionConfig {
        return MqttConnectionConfig(
            brokerUrl = "tcp://broker.hivemq.com:1883",
            clientId = "android_hivemq_${UUID.randomUUID().toString().take(8)}",
            username = null,
            password = null,
            cleanSession = true,
            keepAliveInterval = 60,
            connectionTimeout = 30,
            automaticReconnect = true
        )
    }

    /**
     * Eclipse Mosquitto Public Broker
     * - Host: test.mosquitto.org
     * - Port: 1883 (TCP), 8883 (SSL), 8080 (WebSocket)
     * - No authentication required
     */
    fun mosquittoPublicBroker(): MqttConnectionConfig {
        return MqttConnectionConfig(
            brokerUrl = "tcp://test.mosquitto.org:1883",
            clientId = "android_mosquitto_${UUID.randomUUID().toString().take(8)}",
            username = null,
            password = null,
            cleanSession = true,
            keepAliveInterval = 60,
            connectionTimeout = 30,
            automaticReconnect = true
        )
    }

    /**
     * EMQX Public Broker
     * - Host: broker.emqx.io
     * - Port: 1883 (TCP), 8883 (SSL), 8083 (WebSocket)
     * - No authentication required
     */
    fun emqxPublicBroker(): MqttConnectionConfig {
        return MqttConnectionConfig(
            brokerUrl = "tcp://broker.emqx.io:1883",
            clientId = "android_emqx_${UUID.randomUUID().toString().take(8)}",
            username = null,
            password = null,
            cleanSession = true,
            keepAliveInterval = 60,
            connectionTimeout = 30,
            automaticReconnect = true
        )
    }

    /**
     * Flespi Public Broker
     * - Host: mqtt.flespi.io
     * - Port: 1883 (TCP), 8883 (SSL)
     * - No authentication required
     */
    fun flespiPublicBroker(): MqttConnectionConfig {
        return MqttConnectionConfig(
            brokerUrl = "tcp://mqtt.flespi.io:1883",
            clientId = "android_flespi_${UUID.randomUUID().toString().take(8)}",
            username = null,
            password = null,
            cleanSession = true,
            keepAliveInterval = 60,
            connectionTimeout = 30,
            automaticReconnect = true
        )
    }

    /**
     * Get all available test configurations
     */
    fun getAllTestConfigurations(): List<Pair<String, MqttConnectionConfig>> {
        return listOf(
            "HiveMQ Public" to hiveMqPublicBroker(),
            "Mosquitto Public" to mosquittoPublicBroker(),
            "EMQX Public" to emqxPublicBroker(),
            "Flespi Public" to flespiPublicBroker()
        )
    }

    /**
     * Common test topics for chat testing
     */
    object TestTopics {
        const val CHAT_GENERAL = "chat/general"
        const val CHAT_TEST = "chat/test"
        const val CHAT_ANDROID = "chat/android"
        const val PRESENCE = "presence/online"

        // Wildcard subscriptions for testing
        const val ALL_CHAT = "chat/+"
        const val ALL_TOPICS = "#"
    }
}

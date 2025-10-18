package life.munay.mqtt.repositories

import kotlinx.coroutines.flow.Flow
import life.munay.mqtt.models.MqttConnectionConfig
import life.munay.mqtt.models.MqttConnectionState
import life.munay.mqtt.models.MqttMessage

interface MqttRepository {

    /**
     * Connection state as a Flow
     */
    val connectionState: Flow<MqttConnectionState>

    /**
     * Incoming messages as a Flow
     */
    val incomingMessages: Flow<MqttMessage>

    /**
     * Connect to MQTT broker
     */
    suspend fun connect(config: MqttConnectionConfig): Result<Unit>

    /**
     * Disconnect from MQTT broker
     */
    suspend fun disconnect(): Result<Unit>

    /**
     * Subscribe to a topic
     */
    suspend fun subscribe(topic: String, qos: Int = 1): Result<Unit>

    /**
     * Subscribe to multiple topics
     */
    suspend fun subscribe(topics: List<String>, qos: Int = 1): Result<Unit>

    /**
     * Unsubscribe from a topic
     */
    suspend fun unsubscribe(topic: String): Result<Unit>

    /**
     * Unsubscribe from multiple topics
     */
    suspend fun unsubscribe(topics: List<String>): Result<Unit>

    /**
     * Publish a message to a topic
     */
    suspend fun publish(
        topic: String,
        message: String,
        qos: Int = 1,
        retained: Boolean = false
    ): Result<Unit>

    /**
     * Publish an MqttMessage
     */
    suspend fun publish(message: MqttMessage): Result<Unit>

    /**
     * Get current connection state
     */
    fun getCurrentConnectionState(): MqttConnectionState

    /**
     * Check if connected to broker
     */
    fun isConnected(): Boolean

    /**
     * Get list of subscribed topics
     */
    fun getSubscribedTopics(): List<String>
}

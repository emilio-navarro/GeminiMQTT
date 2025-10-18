package life.munay.mqtt.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import life.munay.mqtt.models.MqttConnectionConfig
import life.munay.mqtt.models.MqttConnectionState
import life.munay.mqtt.models.MqttMessage
import life.munay.mqtt.repositories.MqttRepository
import life.munay.mqtt.utils.MqttTopicUtils
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttUseCase @Inject constructor(
    private val mqttRepository: MqttRepository
) {

    val connectionState: Flow<MqttConnectionState> = mqttRepository.connectionState
    val incomingMessages: Flow<MqttMessage> = mqttRepository.incomingMessages

    /**
     * Get connection state and messages combined
     */
    fun getMqttState(): Flow<MqttState> {
        return combine(
            mqttRepository.connectionState,
            mqttRepository.incomingMessages
        ) { connectionState, _ ->
            MqttState(
                connectionState = connectionState,
                isConnected = mqttRepository.isConnected(),
                subscribedTopics = mqttRepository.getSubscribedTopics()
            )
        }
    }

    /**
     * Connect to MQTT broker with enhanced validation
     */
    suspend fun connectToBroker(
        brokerUrl: String,
        clientId: String? = null,
        username: String? = null,
        password: String? = null
    ): MqttResult<Unit> {
        return try {
            // Enhanced validation
            if (!isValidBrokerUrl(brokerUrl)) {
                return MqttResult.Error("Invalid broker URL format. Use tcp://hostname:port")
            }

            val finalClientId = clientId ?: generateClientId()

            val config = MqttConnectionConfig(
                brokerUrl = brokerUrl,
                clientId = finalClientId,
                username = username,
                password = password,
                keepAliveInterval = 60,
                connectionTimeout = 30,
                cleanSession = true,
                automaticReconnect = true
            )

            val result = mqttRepository.connect(config)
            if (result.isSuccess) {
                Timber.i("Successfully connected to broker: $brokerUrl")
                MqttResult.Success(Unit)
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Timber.e("Failed to connect: $error")
                MqttResult.Error("Connection failed: $error")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in connectToBroker")
            MqttResult.Error("Connection error: ${e.message}")
        }
    }

    /**
     * Connect with legacy Result API for backward compatibility
     */
    suspend fun connectToBroker(config: MqttConnectionConfig): Result<Unit> {
        return if (isValidBrokerUrl(config.brokerUrl)) {
            mqttRepository.connect(config)
        } else {
            Result.failure(IllegalArgumentException("Invalid broker URL"))
        }
    }

    /**
     * Disconnect from MQTT broker
     */
    suspend fun disconnectFromBroker(): MqttResult<Unit> {
        return try {
            val result = mqttRepository.disconnect()
            if (result.isSuccess) {
                Timber.i("Successfully disconnected from broker")
                MqttResult.Success(Unit)
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Timber.e("Failed to disconnect: $error")
                MqttResult.Error("Disconnect failed: $error")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in disconnect")
            MqttResult.Error("Disconnect error: ${e.message}")
        }
    }

    /**
     * Subscribe to topic with enhanced validation
     */
    suspend fun subscribeToTopic(topic: String, qos: Int = 1): MqttResult<Unit> {
        return try {
            if (!MqttTopicUtils.isValidSubscriptionTopic(topic)) {
                return MqttResult.Error("Invalid subscription topic: $topic")
            }

            if (mqttRepository.getSubscribedTopics().contains(topic)) {
                return MqttResult.Error("Already subscribed to topic: $topic")
            }

            val result = mqttRepository.subscribe(topic, qos)
            if (result.isSuccess) {
                Timber.i("Successfully subscribed to topic: $topic")
                MqttResult.Success(Unit)
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Timber.e("Failed to subscribe: $error")
                MqttResult.Error("Subscription failed: $error")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in subscribeToTopic")
            MqttResult.Error("Subscription error: ${e.message}")
        }
    }

    /**
     * Subscribe to multiple topics
     */
    suspend fun subscribeToTopics(topics: List<String>, qos: Int = 1): Result<Unit> {
        val invalidTopics = topics.filter { !MqttTopicUtils.isValidSubscriptionTopic(it) }
        return if (invalidTopics.isEmpty()) {
            mqttRepository.subscribe(topics, qos)
        } else {
            Result.failure(IllegalArgumentException("Invalid topics: $invalidTopics"))
        }
    }

    /**
     * Unsubscribe from topic
     */
    suspend fun unsubscribeFromTopic(topic: String): Result<Unit> {
        return mqttRepository.unsubscribe(topic)
    }

    /**
     * Publish message with enhanced validation
     */
    suspend fun publishMessage(
        topic: String,
        message: String,
        qos: Int = 1,
        retained: Boolean = false
    ): MqttResult<Unit> {
        return try {
            if (!MqttTopicUtils.isValidPublishTopic(topic)) {
                return MqttResult.Error("Invalid publish topic: $topic")
            }

            if (message.isBlank()) {
                return MqttResult.Error("Message cannot be empty")
            }

            if (qos !in 0..2) {
                return MqttResult.Error("QoS must be 0, 1, or 2")
            }

            if (!mqttRepository.isConnected()) {
                return MqttResult.Error("Not connected to MQTT broker")
            }

            val result = mqttRepository.publish(topic, message, qos, retained)
            if (result.isSuccess) {
                Timber.i("Successfully published to topic: $topic")
                MqttResult.Success(Unit)
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Timber.e("Failed to publish: $error")
                MqttResult.Error("Publish failed: $error")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in publishMessage")
            MqttResult.Error("Publish error: ${e.message}")
        }
    }

    /**
     * Publish device status
     */
    suspend fun publishDeviceStatus(
        deviceId: String,
        status: String,
        retained: Boolean = true
    ): MqttResult<Unit> {
        val topic = MqttTopicUtils.buildDeviceTopic(deviceId, "status")
        return publishMessage(topic, status, retained = retained)
    }

    /**
     * Publish device data
     */
    suspend fun publishDeviceData(
        deviceId: String,
        data: String,
        qos: Int = 1
    ): MqttResult<Unit> {
        val topic = MqttTopicUtils.buildDeviceTopic(deviceId, "data")
        return publishMessage(topic, data, qos = qos)
    }

    /**
     * Subscribe to device status updates
     */
    suspend fun subscribeToDeviceStatus(deviceId: String? = null): MqttResult<Unit> {
        val topic = if (deviceId != null) {
            MqttTopicUtils.buildDeviceTopic(deviceId, "status")
        } else {
            "device/+/status"
        }
        return subscribeToTopic(topic)
    }

    /**
     * Subscribe to device data updates
     */
    suspend fun subscribeToDeviceData(deviceId: String? = null): MqttResult<Unit> {
        val topic = if (deviceId != null) {
            MqttTopicUtils.buildDeviceTopic(deviceId, "data")
        } else {
            "device/+/data"
        }
        return subscribeToTopic(topic)
    }

    /**
     * Get current connection status
     */
    fun isConnected(): Boolean = mqttRepository.isConnected()

    /**
     * Get subscribed topics
     */
    fun getSubscribedTopics(): List<String> = mqttRepository.getSubscribedTopics()

    private fun generateClientId(): String {
        return "android_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    private fun isValidBrokerUrl(url: String): Boolean {
        return url.isNotBlank() && (url.startsWith("tcp://") || url.startsWith("ssl://") || url.startsWith("mqtt://")) &&
            url.contains(":") &&
            url.length > 10 &&
            try {
                val parts = url.substringAfter("://").split(":")
                parts.size == 2 && parts[1].toIntOrNull() != null
            } catch (e: Exception) {
                false
            }
    }
}

/**
 * Data class representing MQTT state
 */
data class MqttState(
    val connectionState: MqttConnectionState,
    val isConnected: Boolean,
    val subscribedTopics: List<String>
)

/**
 * Sealed class for MQTT operation results with enhanced error handling
 */
sealed class MqttResult<out T> {
    data class Success<T>(val data: T) : MqttResult<T>()
    data class Error(val message: String) : MqttResult<Nothing>()

    inline fun onSuccess(action: (T) -> Unit): MqttResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (String) -> Unit): MqttResult<T> {
        if (this is Error) action(message)
        return this
    }

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error

    fun getOrNull(): T? = if (this is Success) data else null
    fun getErrorOrNull(): String? = if (this is Error) message else null
}

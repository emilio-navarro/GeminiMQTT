package life.munay.mqtt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import life.munay.mqtt.models.MqttConnectionConfig
import life.munay.mqtt.models.MqttConnectionState
import life.munay.mqtt.models.MqttMessage
import life.munay.mqtt.repositories.MqttRepository
import life.munay.mqtt.utils.MqttTopicUtils
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MqttViewModel @Inject constructor(
    private val mqttRepository: MqttRepository
) : ViewModel() {

    // Connection state from repository
    val connectionState: Flow<MqttConnectionState> = mqttRepository.connectionState

    // Messages list
    private val _messages = MutableStateFlow<List<MqttMessage>>(emptyList())
    val messages: StateFlow<List<MqttMessage>> = _messages.asStateFlow()

    // Subscribed topics
    private val _subscribedTopics = MutableStateFlow<List<String>>(emptyList())
    val subscribedTopics: StateFlow<List<String>> = _subscribedTopics.asStateFlow()

    // Error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Loading states
    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    private val _isPublishing = MutableStateFlow(false)
    val isPublishing: StateFlow<Boolean> = _isPublishing.asStateFlow()

    init {
        // Collect incoming messages
        viewModelScope.launch {
            mqttRepository.incomingMessages.collect { message ->
                _messages.value = _messages.value + message
                Timber.d("New message received: ${message.topic} - ${message.payload}")
            }
        }
    }

    fun connect(brokerUrl: String) {
        viewModelScope.launch {
            try {
                // Validate broker URL
                if (!isValidBrokerUrl(brokerUrl)) {
                    _errorMessage.value = "Invalid broker URL format. Use tcp://hostname:port"
                    return@launch
                }

                _isConnecting.value = true
                _errorMessage.value = null

                // Generate a unique client ID
                val clientId = "android_client_${System.currentTimeMillis()}"

                val config = MqttConnectionConfig(
                    brokerUrl = brokerUrl,
                    clientId = clientId,
                    keepAliveInterval = 60,
                    connectionTimeout = 30,
                    cleanSession = true,
                    automaticReconnect = true
                )

                val result = mqttRepository.connect(config)
                if (result.isSuccess) {
                    Timber.i("Successfully connected to MQTT broker: $brokerUrl")
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Unknown connection error"
                    _errorMessage.value = "Connection failed: $errorMsg"
                    Timber.e("Failed to connect to MQTT broker: $errorMsg")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Connection error: ${e.message}"
                Timber.e(e, "Error connecting to MQTT broker")
            } finally {
                _isConnecting.value = false
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                val result = mqttRepository.disconnect()
                if (result.isSuccess) {
                    _subscribedTopics.value = emptyList()
                    Timber.i("Successfully disconnected from MQTT broker")
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Unknown disconnection error"
                    _errorMessage.value = "Disconnection failed: $errorMsg"
                    Timber.e("Failed to disconnect from MQTT broker: $errorMsg")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Disconnection error: ${e.message}"
                Timber.e(e, "Error disconnecting from MQTT broker")
            }
        }
    }

    fun subscribe(topic: String) {
        viewModelScope.launch {
            try {
                // Validate topic
                if (!MqttTopicUtils.isValidSubscriptionTopic(topic)) {
                    _errorMessage.value = "Invalid subscription topic format"
                    return@launch
                }

                // Check if already subscribed
                if (_subscribedTopics.value.contains(topic)) {
                    _errorMessage.value = "Already subscribed to topic: $topic"
                    return@launch
                }

                _errorMessage.value = null
                val result = mqttRepository.subscribe(topic)
                if (result.isSuccess) {
                    _subscribedTopics.value = mqttRepository.getSubscribedTopics()
                    Timber.i("Successfully subscribed to topic: $topic")
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Unknown subscription error"
                    _errorMessage.value = "Subscription failed: $errorMsg"
                    Timber.e("Failed to subscribe to topic $topic: $errorMsg")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Subscription error: ${e.message}"
                Timber.e(e, "Error subscribing to topic: $topic")
            }
        }
    }

    fun unsubscribe(topic: String) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                val result = mqttRepository.unsubscribe(topic)
                if (result.isSuccess) {
                    _subscribedTopics.value = mqttRepository.getSubscribedTopics()
                    Timber.i("Successfully unsubscribed from topic: $topic")
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Unknown unsubscription error"
                    _errorMessage.value = "Unsubscription failed: $errorMsg"
                    Timber.e("Failed to unsubscribe from topic $topic: $errorMsg")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Unsubscription error: ${e.message}"
                Timber.e(e, "Error unsubscribing from topic: $topic")
            }
        }
    }

    fun publish(topic: String, message: String, qos: Int = 1, retained: Boolean = false) {
        viewModelScope.launch {
            try {
                // Validate inputs
                if (!MqttTopicUtils.isValidPublishTopic(topic)) {
                    _errorMessage.value = "Invalid publish topic format (wildcards not allowed)"
                    return@launch
                }

                if (message.isBlank()) {
                    _errorMessage.value = "Message cannot be empty"
                    return@launch
                }

                if (qos !in 0..2) {
                    _errorMessage.value = "QoS must be 0, 1, or 2"
                    return@launch
                }

                _isPublishing.value = true
                _errorMessage.value = null

                val result = mqttRepository.publish(topic, message, qos, retained)
                if (result.isSuccess) {
                    // Add sent message to the messages list
                    val sentMessage = MqttMessage(
                        topic = topic,
                        payload = message,
                        qos = qos,
                        retained = retained,
                        timestamp = System.currentTimeMillis(),
                        isSentByMe = true
                    )
                    _messages.value = _messages.value + sentMessage
                    Timber.i("Successfully published message to topic: $topic")
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Unknown publish error"
                    _errorMessage.value = "Publish failed: $errorMsg"
                    Timber.e("Failed to publish message to topic $topic: $errorMsg")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Publish error: ${e.message}"
                Timber.e(e, "Error publishing message to topic: $topic")
            } finally {
                _isPublishing.value = false
            }
        }
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }

    fun clearError() {
        _errorMessage.value = null
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

    override fun onCleared() {
        super.onCleared()
        // Disconnect when ViewModel is cleared
        viewModelScope.launch {
            try {
                if (mqttRepository.isConnected()) {
                    mqttRepository.disconnect()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error disconnecting in onCleared")
            }
        }
    }
}

package life.munay.mqtt.repositories

import android.content.Context
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import life.munay.mqtt.models.MqttConnectionConfig
import life.munay.mqtt.models.MqttConnectionState
import life.munay.mqtt.models.MqttMessage
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class MqttRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MqttRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var mqttClient: Mqtt3AsyncClient? = null
    private var currentConfig: MqttConnectionConfig? = null

    // State flows
    private val _connectionState = MutableStateFlow(MqttConnectionState.DISCONNECTED)
    override val connectionState: Flow<MqttConnectionState> = _connectionState.asStateFlow()

    // Shared flow for MQTT messages (events, not state)
    private val _incomingMessages = MutableSharedFlow<MqttMessage>(
        replay = 0, // Don't replay old messages to new collectors
        extraBufferCapacity = 64 // Buffer up to 64 messages if collector is slow
    )
    override val incomingMessages: Flow<MqttMessage> = _incomingMessages.asSharedFlow()

    // Subscribed topics tracking
    private val subscribedTopics = mutableSetOf<String>()

    override suspend fun connect(config: MqttConnectionConfig): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (_connectionState.value == MqttConnectionState.CONNECTED) {
                return@withContext Result.success(Unit)
            }

            _connectionState.value = MqttConnectionState.CONNECTING
            currentConfig = config

            // Create HiveMQ MQTT client
            val clientBuilder = Mqtt3Client.builder()
                .identifier(config.clientId)
                .serverHost(extractHostFromUrl(config.brokerUrl))
                .serverPort(extractPortFromUrl(config.brokerUrl))

            // Add authentication if provided
            config.username?.let { username ->
                val authBuilder = clientBuilder.simpleAuth()
                    .username(username)
                config.password?.let { password ->
                    authBuilder.password(password.toByteArray())
                }
                authBuilder.applySimpleAuth()
            }

            mqttClient = clientBuilder.buildAsync()

            // Set up message listener
            mqttClient?.publishes(com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL) { publish ->
                val mqttMessage = MqttMessage(
                    topic = publish.topic.toString(),
                    payload = String(publish.payloadAsBytes, StandardCharsets.UTF_8),
                    qos = publish.qos.code,
                    retained = publish.isRetain,
                    isSentByMe = false
                )
                _incomingMessages.tryEmit(mqttMessage)
                Timber.d("Message received on topic: ${publish.topic}")
            }

            // Connect
            suspendCancellableCoroutine<Unit> { continuation ->
                val connectBuilder = mqttClient!!.connectWith()
                    .cleanSession(config.cleanSession)
                    .keepAlive(config.keepAliveInterval)

                val connectFuture: CompletableFuture<Mqtt3ConnAck> = connectBuilder.send()

                connectFuture
                    .whenComplete { connAck, throwable ->
                        if (throwable != null) {
                            _connectionState.value = MqttConnectionState.ERROR
                            Timber.e(throwable, "MQTT connection failed")
                            continuation.resumeWithException(throwable)
                        } else {
                            _connectionState.value = MqttConnectionState.CONNECTED
                            Timber.i("MQTT connected successfully: ${connAck.returnCode}")
                            continuation.resume(Unit)
                        }
                    }

                continuation.invokeOnCancellation {
                    connectFuture.cancel(true)
                    mqttClient?.disconnect()
                    _connectionState.value = MqttConnectionState.DISCONNECTED
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            _connectionState.value = MqttConnectionState.ERROR
            Timber.e(e, "Error connecting to MQTT broker")
            Result.failure(e)
        }
    }

    override suspend fun disconnect(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            mqttClient?.let { client ->
                suspendCancellableCoroutine<Unit> { continuation ->
                    val disconnectFuture = client.disconnect()

                    disconnectFuture
                        .whenComplete { _, throwable ->
                            if (throwable != null) {
                                _connectionState.value = MqttConnectionState.ERROR
                                Timber.e(throwable, "MQTT disconnection failed")
                                continuation.resumeWithException(throwable)
                            } else {
                                _connectionState.value = MqttConnectionState.DISCONNECTED
                                subscribedTopics.clear()
                                Timber.i("MQTT disconnected successfully")
                                continuation.resume(Unit)
                            }
                        }
                }
            } ?: run {
                _connectionState.value = MqttConnectionState.DISCONNECTED
                subscribedTopics.clear()
            }

            mqttClient = null
            currentConfig = null
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error disconnecting from MQTT broker")
            Result.failure(e)
        }
    }

    override suspend fun subscribe(topic: String, qos: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val client = mqttClient ?: return@withContext Result.failure(
                IllegalStateException("Not connected to MQTT broker")
            )

            suspendCancellableCoroutine<Unit> { continuation ->
                val subscribeFuture = client.subscribeWith()
                    .topicFilter(topic)
                    .qos(MqttQos.fromCode(qos) ?: MqttQos.AT_MOST_ONCE)
                    .send()

                subscribeFuture
                    .whenComplete { _, throwable ->
                        if (throwable != null) {
                            Timber.e(throwable, "Failed to subscribe to topic: $topic")
                            continuation.resumeWithException(throwable)
                        } else {
                            subscribedTopics.add(topic)
                            Timber.i("Successfully subscribed to topic: $topic")
                            continuation.resume(Unit)
                        }
                    }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error subscribing to topic: $topic")
            Result.failure(e)
        }
    }

    override suspend fun subscribe(topics: List<String>, qos: Int): Result<Unit> {
        return try {
            topics.forEach { topic ->
                val result = subscribe(topic, qos)
                if (result.isFailure) {
                    return result
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unsubscribe(topic: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val client = mqttClient ?: return@withContext Result.failure(
                IllegalStateException("Not connected to MQTT broker")
            )

            suspendCancellableCoroutine<Unit> { continuation ->
                val unsubscribeFuture = client.unsubscribeWith()
                    .topicFilter(topic)
                    .send()

                unsubscribeFuture
                    .whenComplete { _, throwable ->
                        if (throwable != null) {
                            Timber.e(throwable, "Failed to unsubscribe from topic: $topic")
                            continuation.resumeWithException(throwable)
                        } else {
                            subscribedTopics.remove(topic)
                            Timber.i("Successfully unsubscribed from topic: $topic")
                            continuation.resume(Unit)
                        }
                    }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error unsubscribing from topic: $topic")
            Result.failure(e)
        }
    }

    override suspend fun unsubscribe(topics: List<String>): Result<Unit> {
        return try {
            topics.forEach { topic ->
                val result = unsubscribe(topic)
                if (result.isFailure) {
                    return result
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun publish(
        topic: String,
        message: String,
        qos: Int,
        retained: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val client = mqttClient ?: return@withContext Result.failure(
                IllegalStateException("Not connected to MQTT broker")
            )

            suspendCancellableCoroutine<Unit> { continuation ->
                val publishFuture = client.publishWith()
                    .topic(topic)
                    .payload(message.toByteArray(StandardCharsets.UTF_8))
                    .qos(MqttQos.fromCode(qos) ?: MqttQos.AT_MOST_ONCE)
                    .retain(retained)
                    .send()

                publishFuture
                    .whenComplete { _, throwable ->
                        if (throwable != null) {
                            Timber.e(throwable, "Failed to publish message to topic: $topic")
                            continuation.resumeWithException(throwable)
                        } else {
                            Timber.i("Successfully published message to topic: $topic")
                            continuation.resume(Unit)
                        }
                    }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error publishing message to topic: $topic")
            Result.failure(e)
        }
    }

    override suspend fun publish(message: MqttMessage): Result<Unit> {
        return publish(
            topic = message.topic,
            message = message.payload,
            qos = message.qos,
            retained = message.retained
        )
    }

    override fun getCurrentConnectionState(): MqttConnectionState {
        return _connectionState.value
    }

    override fun isConnected(): Boolean {
        return _connectionState.value == MqttConnectionState.CONNECTED
    }

    override fun getSubscribedTopics(): List<String> {
        return subscribedTopics.toList()
    }

    private fun extractHostFromUrl(brokerUrl: String): String {
        // Handle tcp://host:port format
        return when {
            brokerUrl.startsWith("tcp://") -> {
                val hostPort = brokerUrl.removePrefix("tcp://")
                hostPort.substringBefore(":")
            }
            brokerUrl.startsWith("ssl://") -> {
                val hostPort = brokerUrl.removePrefix("ssl://")
                hostPort.substringBefore(":")
            }
            brokerUrl.startsWith("ws://") -> {
                val hostPort = brokerUrl.removePrefix("ws://")
                hostPort.substringBefore(":")
            }
            brokerUrl.startsWith("wss://") -> {
                val hostPort = brokerUrl.removePrefix("wss://")
                hostPort.substringBefore(":")
            }
            else -> brokerUrl.substringBefore(":")
        }
    }

    private fun extractPortFromUrl(brokerUrl: String): Int {
        return when {
            brokerUrl.startsWith("tcp://") -> {
                val hostPort = brokerUrl.removePrefix("tcp://")
                hostPort.substringAfter(":", "1883").toIntOrNull() ?: 1883
            }
            brokerUrl.startsWith("ssl://") -> {
                val hostPort = brokerUrl.removePrefix("ssl://")
                hostPort.substringAfter(":", "8883").toIntOrNull() ?: 8883
            }
            brokerUrl.startsWith("ws://") -> {
                val hostPort = brokerUrl.removePrefix("ws://")
                hostPort.substringAfter(":", "80").toIntOrNull() ?: 80
            }
            brokerUrl.startsWith("wss://") -> {
                val hostPort = brokerUrl.removePrefix("wss://")
                hostPort.substringAfter(":", "443").toIntOrNull() ?: 443
            }
            else -> brokerUrl.substringAfter(":", "1883").toIntOrNull() ?: 1883
        }
    }

    private fun reconnect() {
        currentConfig?.let { config ->
            repositoryScope.launch {
                try {
                    kotlinx.coroutines.delay(5000) // Wait 5 seconds before reconnecting
                    _connectionState.value = MqttConnectionState.RECONNECTING
                    connect(config)
                } catch (e: Exception) {
                    Timber.e(e, "Reconnection failed")
                    _connectionState.value = MqttConnectionState.ERROR
                }
            }
        }
    }
}

package life.munay.mqtt.models

data class MqttConnectionConfig(
    val brokerUrl: String,
    val clientId: String,
    val username: String? = null,
    val password: String? = null,
    val keepAliveInterval: Int = 60,
    val connectionTimeout: Int = 30,
    val cleanSession: Boolean = true,
    val automaticReconnect: Boolean = true
)

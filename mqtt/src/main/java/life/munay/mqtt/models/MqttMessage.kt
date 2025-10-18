package life.munay.mqtt.models

data class MqttMessage(
    val topic: String,
    val payload: String,
    val qos: Int = 1,
    val retained: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val isSentByMe: Boolean = false
)

package life.munay.mqtt.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import life.munay.core.resusables.composables.themes.AppTheme
import life.munay.mqtt.models.MqttConnectionState
import life.munay.mqtt.models.MqttMessage
import life.munay.mqtt.utils.MqttTopicUtils
import life.munay.mqtt.viewmodels.MqttViewModel

@Composable
fun MqttScreen(
    mqttViewModel: MqttViewModel? = null
) {
    // Safe state collection with default values for preview
    val connectionState by (
        mqttViewModel?.connectionState?.collectAsState(initial = MqttConnectionState.DISCONNECTED)
            ?: remember { mutableStateOf(MqttConnectionState.DISCONNECTED) }
        )
    val messages by (
        mqttViewModel?.messages?.collectAsState(initial = emptyList())
            ?: remember { mutableStateOf(emptyList<MqttMessage>()) }
        )
    val subscribedTopics by (
        mqttViewModel?.subscribedTopics?.collectAsState(initial = emptyList())
            ?: remember { mutableStateOf(emptyList<String>()) }
        )
    val errorMessage by (
        mqttViewModel?.errorMessage?.collectAsState(initial = null)
            ?: remember { mutableStateOf<String?>(null) }
        )

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var brokerUrl by remember { mutableStateOf("tcp://test.mosquitto.org:1883") }
    var publishTopic by remember { mutableStateOf("test/android") }
    var subscribeTopic by remember { mutableStateOf("test/android") }
    var messageText by remember { mutableStateOf("Hello from Android!") }
    var isConnecting by remember { mutableStateOf(false) }

    // Show error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            mqttViewModel?.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Connection Status
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Connection Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Status: ${connectionState.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (connectionState) {
                        MqttConnectionState.CONNECTED -> Color(0xFF4CAF50)
                        MqttConnectionState.CONNECTING -> Color(0xFF2196F3)
                        MqttConnectionState.RECONNECTING -> Color(0xFFFF9800)
                        MqttConnectionState.DISCONNECTED -> Color(0xFF757575)
                        MqttConnectionState.ERROR -> Color(0xFFF44336)
                    }
                )
            }
        }

        // Broker Configuration
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "MQTT Connection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = brokerUrl,
                    onValueChange = { brokerUrl = it },
                    label = { Text("Broker URL") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = connectionState != MqttConnectionState.CONNECTED && !isConnecting,
                    isError = brokerUrl.isBlank() || !isValidBrokerUrl(brokerUrl),
                    supportingText = {
                        if (brokerUrl.isNotBlank() && !isValidBrokerUrl(brokerUrl)) {
                            Text(
                                text = "Invalid URL format. Use tcp://hostname:port",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                // Quick Broker Selection
                Text(
                    text = "Quick Test Brokers:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { brokerUrl = "tcp://broker.hivemq.com:1883" },
                        enabled = connectionState != MqttConnectionState.CONNECTED && !isConnecting,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("HiveMQ", fontSize = 10.sp)
                    }
                    Button(
                        onClick = { brokerUrl = "tcp://test.mosquitto.org:1883" },
                        enabled = connectionState != MqttConnectionState.CONNECTED && !isConnecting,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mosquitto", fontSize = 10.sp)
                    }
                    Button(
                        onClick = { brokerUrl = "tcp://broker.emqx.io:1883" },
                        enabled = connectionState != MqttConnectionState.CONNECTED && !isConnecting,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("EMQX", fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (isValidBrokerUrl(brokerUrl)) {
                                isConnecting = true
                                scope.launch {
                                    mqttViewModel?.connect(brokerUrl)
                                    isConnecting = false
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please enter a valid broker URL")
                                }
                            }
                        },
                        enabled = connectionState != MqttConnectionState.CONNECTED && connectionState != MqttConnectionState.CONNECTING && !isConnecting &&
                            brokerUrl.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isConnecting) "Connecting..." else "Connect")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                mqttViewModel?.disconnect()
                            }
                        },
                        enabled = connectionState == MqttConnectionState.CONNECTED,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Disconnect")
                    }
                }
            }
        }

        // Subscribe Section
        if (connectionState == MqttConnectionState.CONNECTED) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Subscribe to Topic",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = subscribeTopic,
                            onValueChange = { subscribeTopic = it },
                            label = { Text("Topic") },
                            modifier = Modifier.weight(1f),
                            isError = subscribeTopic.isNotBlank() && !MqttTopicUtils.isValidSubscriptionTopic(subscribeTopic),
                            supportingText = {
                                if (subscribeTopic.isNotBlank() && !MqttTopicUtils.isValidSubscriptionTopic(subscribeTopic)) {
                                    Text(
                                        text = "Invalid topic format",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )

                        Button(
                            onClick = {
                                if (MqttTopicUtils.isValidSubscriptionTopic(subscribeTopic) && !subscribedTopics.contains(subscribeTopic)) {
                                    scope.launch {
                                        mqttViewModel?.subscribe(subscribeTopic)
                                    }
                                } else if (subscribedTopics.contains(subscribeTopic)) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Already subscribed to this topic")
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Please enter a valid topic")
                                    }
                                }
                            },
                            enabled = subscribeTopic.isNotBlank()
                        ) {
                            Text("Subscribe")
                        }
                    }

                    if (subscribedTopics.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Subscribed Topics:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        subscribedTopics.forEach { topic ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        scope.launch {
                                            mqttViewModel?.unsubscribe(topic)
                                        }
                                    }
                                ) {
                                    Text("Unsubscribe")
                                }
                            }
                        }
                    }
                }
            }

            // Publish Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Publish Message",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = publishTopic,
                        onValueChange = { publishTopic = it },
                        label = { Text("Topic") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = publishTopic.isNotBlank() && !MqttTopicUtils.isValidPublishTopic(publishTopic),
                        supportingText = {
                            if (publishTopic.isNotBlank() && !MqttTopicUtils.isValidPublishTopic(publishTopic)) {
                                Text(
                                    text = "Invalid publish topic (no wildcards allowed)",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            label = { Text("Message") },
                            modifier = Modifier.weight(1f),
                            isError = messageText.isBlank()
                        )

                        Button(
                            onClick = {
                                if (MqttTopicUtils.isValidPublishTopic(publishTopic) && messageText.isNotBlank()) {
                                    scope.launch {
                                        mqttViewModel?.publish(publishTopic, messageText)
                                        messageText = "" // Clear message after sending
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Please enter valid topic and message")
                                    }
                                }
                            },
                            enabled = publishTopic.isNotBlank() && messageText.isNotBlank()
                        ) {
                            Text("Send")
                        }
                    }
                }
            }
        }

        // Chat Messages Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chat (${messages.size} messages)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (messages.isNotEmpty()) {
                        Button(
                            onClick = { mqttViewModel?.clearMessages() }
                        ) {
                            Text("Clear")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (messages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No messages yet...\nStart a conversation!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = true,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(messages.reversed()) { message ->
                            ChatMessageItem(message = message)
                        }
                    }
                }
            }
        }

        // Snackbar Host
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
private fun ChatMessageItem(message: MqttMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = if (message.isSentByMe) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isSentByMe) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isSentByMe) 16.dp else 4.dp,
                bottomEnd = if (message.isSentByMe) 4.dp else 16.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Topic header
                Text(
                    text = message.topic,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (message.isSentByMe) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Message content
                Text(
                    text = message.payload,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isSentByMe) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Time and QoS
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                                .format(java.util.Date(message.timestamp)),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (message.isSentByMe) {
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            } else {
                                Color.Gray
                            }
                        )

                        if (message.qos > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Q${message.qos}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (message.isSentByMe) {
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                } else {
                                    Color.Gray
                                },
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Status indicators
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (message.retained) {
                            Text(
                                text = "R",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        if (message.isSentByMe) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Sent",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun isValidBrokerUrl(url: String): Boolean {
    return url.isNotBlank() && (url.startsWith("tcp://") || url.startsWith("ssl://") || url.startsWith("mqtt://")) &&
        url.contains(":") &&
        url.length > 10
}

@Preview(showBackground = true)
@Composable
fun MqttScreenPreview() {
    AppTheme {
        MqttScreen(mqttViewModel = null)
    }
}

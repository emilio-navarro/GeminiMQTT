package life.munay.mqtt.repositories

import android.content.Context
import kotlinx.coroutines.test.runTest
import life.munay.mqtt.models.MqttConnectionConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MqttRepositoryImplTest {

    @Mock
    private lateinit var context: Context

    private lateinit var repository: MqttRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = MqttRepositoryImpl(context)
    }

    @Test
    fun `initial connection state should be disconnected`() {
        assertFalse(repository.isConnected())
        assertEquals(
            life.munay.mqtt.models.MqttConnectionState.DISCONNECTED,
            repository.getCurrentConnectionState()
        )
    }

    @Test
    fun `subscribed topics should be empty initially`() {
        assertTrue(repository.getSubscribedTopics().isEmpty())
    }

    @Test
    fun `should fail to publish when not connected`() = runTest {
        val result = repository.publish("test/topic", "test message")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `should fail to subscribe when not connected`() = runTest {
        val result = repository.subscribe("test/topic")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `should create valid mqtt connection config`() {
        val config = MqttConnectionConfig(
            brokerUrl = "tcp://test.mosquitto.org:1883",
            clientId = "test-client-123",
            username = "testuser",
            password = "testpass"
        )

        assertEquals("tcp://test.mosquitto.org:1883", config.brokerUrl)
        assertEquals("test-client-123", config.clientId)
        assertEquals("testuser", config.username)
        assertEquals("testpass", config.password)
        assertEquals(60, config.keepAliveInterval)
        assertEquals(30, config.connectionTimeout)
        assertTrue(config.cleanSession)
        assertTrue(config.automaticReconnect)
    }
}

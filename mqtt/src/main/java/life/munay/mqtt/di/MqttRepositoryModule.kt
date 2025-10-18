package life.munay.mqtt.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import life.munay.mqtt.repositories.MqttRepository
import life.munay.mqtt.repositories.MqttRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MqttRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMqttRepository(
        mqttRepositoryImpl: MqttRepositoryImpl
    ): MqttRepository
}

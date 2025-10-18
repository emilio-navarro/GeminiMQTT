package life.munay.mqtt.base

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
import life.munay.mqtt.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class MunayApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        MultiDex.install(this)

        if (BuildConfig.DEBUG) {
            if (Timber.treeCount == 0) {
                Timber.plant(Timber.DebugTree())
            }
        }
    }
}

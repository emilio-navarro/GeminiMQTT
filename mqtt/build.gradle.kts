plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = libs.versions.namespaceMqtt.get()
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()

    defaultConfig {
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            resValue("string", "app_name", libs.versions.appName.get())
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        debug {
            resValue("string", "app_name", libs.versions.appName.get() + " Mqtt")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_18)
        }
    }
}

androidComponents {
    onVariants(selector().withBuildType("release")) {
        it.packaging.resources.excludes
            .add("/META-INF/{AL2.0,LGPL2.1}")
        it.packaging.resources.excludes
            .add("/META-INF/*")
    }
}

dependencies {
    implementation(project(":core"))

    // AndroidX
    implementation(libs.androidx.core.ktx)

    // Composable
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material.window)

    // Google
    implementation(libs.google.android.material)
    implementation(libs.google.gson)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.androidx.hilt.ext.compiler)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.runtime)

    // MQTT - Eclipse Paho has Android 13+ compatibility issues
    // implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    // implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1") {
    //     exclude(group = "com.android.support", module = "support-v4")
    // }

    // Use HiveMQ MQTT client for better Android 13+ compatibility
    implementation("com.hivemq:hivemq-mqtt-client:1.3.3")

    // Keep AndroidX compatibility libraries
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // AndroidX compatibility for Eclipse Paho
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Alternative: Modern MQTT client (comment out Eclipse Paho above and use this instead if issues persist)
    // implementation("com.hivemq:hivemq-mqtt-client:1.3.3")

    // Unit Tests
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)

    // Android Instrumentation Tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

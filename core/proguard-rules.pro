# Core Module ProGuard Rules
# Production-grade obfuscation and optimization rules

# ============================================================================
# ESSENTIAL ATTRIBUTES
# ============================================================================
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ============================================================================
# CORE MODULE CLASSES
# ============================================================================
# Keep all public API classes and interfaces
-keep public class life.munay.core.** { public *; }

# Keep base classes that are extended by other modules
-keep class life.munay.core.base.BaseViewModel { *; }
-keep class life.munay.core.base.BaseComponentActivity { *; }

# Keep repository interfaces (used by dependency injection)
-keep interface life.munay.core.repositories.** { *; }

# Keep navigation classes (used by other modules)
-keep class life.munay.core.navigation.** { *; }

# ============================================================================
# GSON SERIALIZATION
# ============================================================================
# Keep all classes that might be serialized
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep generic signatures for Gson
-keepattributes Signature
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# ============================================================================
# HILT DEPENDENCY INJECTION
# ============================================================================
# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }

# Keep classes annotated with Hilt annotations
-keep @dagger.hilt.** class * { *; }
-keep @javax.inject.** class * { *; }

# ============================================================================
# ANDROIDX & COMPOSE
# ============================================================================
# Keep Compose runtime classes
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.graphics.** { *; }

# Keep ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep navigation classes
-keep class androidx.navigation.** { *; }

# ============================================================================
# KOTLIN COROUTINES
# ============================================================================
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Keep coroutines internals
-dontwarn kotlinx.coroutines.flow.**

# ============================================================================
# OPTIMIZATION SETTINGS
# ============================================================================
# Enable aggressive optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# ============================================================================
# DEBUGGING (Remove in production)
# ============================================================================
# Keep source file names and line numbers for stack traces
-keepattributes SourceFile,LineNumberTable
# Rename source file attribute to hide original file names
-renamesourcefileattribute SourceFile

# ============================================================================
# GENERAL ANDROID
# ============================================================================
# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ============================================================================
# WARNING SUPPRESSIONS
# ============================================================================
-dontwarn org.json.**
-dontwarn com.google.gson.**
-dontwarn kotlinx.coroutines.**
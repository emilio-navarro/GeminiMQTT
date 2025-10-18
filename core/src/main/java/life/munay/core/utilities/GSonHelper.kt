package life.munay.core.utilities

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import kotlin.reflect.KClass

/**
 * Enhanced JSON utilities with type safety and comprehensive error handling.
 *
 * Provides safe JSON serialization/deserialization with proper error handling
 * and type safety for Kotlin classes.
 */
object GSonHelper {
    @JvmStatic
    val gson = Gson()

    /**
     * Convert object to JSONObject safely
     */
    fun <T> objectToJSON(obj: T): JSONObject? =
        try {
            JSONObject(gson.toJson(obj))
        } catch (e: Exception) {
            null
        }

    /**
     * Convert object to JSON string safely
     */
    fun <T> objectToJsonString(obj: T): String? =
        try {
            gson.toJson(obj)
        } catch (e: Exception) {
            null
        }

    /**
     * Parse JSON string to object with type safety
     */
    inline fun <reified T> fromJson(json: String): T? =
        try {
            gson.fromJson(json, T::class.java)
        } catch (e: JsonSyntaxException) {
            null
        } catch (e: Exception) {
            null
        }

    /**
     * Parse JSON string to object with explicit class
     */
    fun <T : Any> fromJson(
        json: String,
        clazz: KClass<T>
    ): T? =
        try {
            gson.fromJson(json, clazz.java)
        } catch (e: JsonSyntaxException) {
            null
        } catch (e: Exception) {
            null
        }

    /**
     * Parse JSON string to list of objects
     */
    inline fun <reified T> fromJsonList(json: String): List<T>? =
        try {
            val type = object : TypeToken<List<T>>() {}.type
            gson.fromJson(json, type)
        } catch (e: JsonSyntaxException) {
            null
        } catch (e: Exception) {
            null
        }

    /**
     * Check if string is valid JSON
     */
    fun isValidJson(json: String): Boolean {
        return try {
            if (json.isBlank()) return false
            // Try to parse as JsonElement and check if it's properly formatted JSON
            val element = gson.fromJson(json, com.google.gson.JsonElement::class.java)
            // Ensure it's not just a plain string without quotes
            // Valid JSON must be null, boolean, number, string (with quotes), array, or object
            element != null &&
                (
                    element.isJsonObject ||
                        element.isJsonArray ||
                        element.isJsonNull ||
                        (
                            element.isJsonPrimitive &&
                                (
                                    element.asJsonPrimitive.isBoolean ||
                                        element.asJsonPrimitive.isNumber ||
                                        (element.asJsonPrimitive.isString && json.startsWith("\"") && json.endsWith("\""))
                                    )
                            )
                    )
        } catch (e: JsonSyntaxException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Convert object to pretty printed JSON string
     */
    fun <T> toPrettyJson(obj: T): String? =
        try {
            gson
                .newBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(obj)
        } catch (e: Exception) {
            null
        }
}

package life.munay.core.utilities

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

/**
 * Performance monitoring utilities for the Core module.
 *
 * Provides methods to measure execution time, memory usage, and performance metrics
 * with minimal overhead and comprehensive logging.
 */
object PerformanceMonitor {
    /**
     * Measure execution time of a suspending operation
     */
    suspend fun <T> measureSuspendTime(
        tag: String = "PerformanceMonitor",
        operation: suspend () -> T
    ): Pair<T, Long> {
        val result: T
        val timeMillis =
            measureTimeMillis {
                result = operation()
            }
        logPerformance(tag, "Operation completed in ${timeMillis}ms")
        return Pair(result, timeMillis)
    }

    /**
     * Measure execution time of a regular operation
     */
    fun <T> measureTime(
        tag: String = "PerformanceMonitor",
        operation: () -> T
    ): Pair<T, Long> {
        val result: T
        val timeMillis =
            measureTimeMillis {
                result = operation()
            }
        logPerformance(tag, "Operation completed in ${timeMillis}ms")
        return Pair(result, timeMillis)
    }

    /**
     * Execute operation with performance tracking
     */
    suspend fun <T> trackPerformance(
        operationName: String,
        threshold: Long = 1000L, // Log if operation takes more than 1 second
        operation: suspend () -> T
    ): T =
        withContext(Dispatchers.Default) {
            val (result, timeMillis) = measureSuspendTime(operationName, operation)

            if (timeMillis > threshold) {
                logWarning(operationName, "Slow operation detected: ${timeMillis}ms (threshold: ${threshold}ms)")
            }

            result
        }

    /**
     * Get current memory usage information
     */
    fun getMemoryInfo(): MemoryInfo {
        val runtime = Runtime.getRuntime()
        return MemoryInfo(
            totalMemory = runtime.totalMemory(),
            freeMemory = runtime.freeMemory(),
            maxMemory = runtime.maxMemory(),
            usedMemory = runtime.totalMemory() - runtime.freeMemory()
        )
    }

    /**
     * Log memory usage with tag
     */
    fun logMemoryUsage(tag: String = "MemoryMonitor") {
        val memInfo = getMemoryInfo()
        logPerformance(
            tag,
            "Memory Usage - Used: ${formatBytes(memInfo.usedMemory)}, " +
                "Free: ${formatBytes(memInfo.freeMemory)}, " +
                "Total: ${formatBytes(memInfo.totalMemory)}, " +
                "Max: ${formatBytes(memInfo.maxMemory)}"
        )
    }

    /**
     * Execute operation with memory monitoring
     */
    suspend fun <T> trackMemoryUsage(
        operationName: String,
        operation: suspend () -> T
    ): T {
        val beforeMemory = getMemoryInfo()
        logPerformance(operationName, "Memory before: ${formatBytes(beforeMemory.usedMemory)}")

        val result = operation()

        val afterMemory = getMemoryInfo()
        val memoryDiff = afterMemory.usedMemory - beforeMemory.usedMemory

        logPerformance(
            operationName,
            "Memory after: ${formatBytes(afterMemory.usedMemory)}, " +
                "Difference: ${formatBytes(memoryDiff)}"
        )

        return result
    }

    /**
     * Start a performance session for complex operations
     */
    fun startSession(sessionName: String): PerformanceSession = PerformanceSession(sessionName, System.currentTimeMillis())

    /**
     * Format bytes to human readable format
     */
    private fun formatBytes(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = 0

        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }

        return "%.2f %s".format(size, units[unitIndex])
    }

    /**
     * Log performance message (can be overridden for custom logging)
     */
    fun logPerformance(
        tag: String,
        message: String
    ) {
        println("[$tag] $message") // Replace with your logging framework
    }

    /**
     * Log warning message
     */
    fun logWarning(
        tag: String,
        message: String
    ) {
        println("⚠️ [$tag] WARNING: $message") // Replace with your logging framework
    }

    /**
     * Data class for memory information
     */
    data class MemoryInfo(
        val totalMemory: Long,
        val freeMemory: Long,
        val maxMemory: Long,
        val usedMemory: Long
    ) {
        val memoryUsagePercentage: Double
            get() = (usedMemory.toDouble() / maxMemory.toDouble()) * 100.0
    }

    /**
     * Performance session for tracking complex operations
     */
    class PerformanceSession(
        private val sessionName: String,
        private val startTime: Long
    ) {
        private val checkpoints = mutableListOf<Checkpoint>()

        /**
         * Add checkpoint to session
         */
        fun checkpoint(name: String) {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - startTime
            checkpoints.add(Checkpoint(name, elapsedTime))
            logPerformance(sessionName, "Checkpoint '$name' at ${elapsedTime}ms")
        }

        /**
         * End session and return summary
         */
        fun end(): SessionSummary {
            val endTime = System.currentTimeMillis()
            val totalTime = endTime - startTime
            val summary = SessionSummary(sessionName, totalTime, checkpoints.toList())

            logPerformance(sessionName, "Session completed in ${totalTime}ms with ${checkpoints.size} checkpoints")
            return summary
        }

        data class Checkpoint(
            val name: String,
            val elapsedTime: Long
        )

        data class SessionSummary(
            val sessionName: String,
            val totalTime: Long,
            val checkpoints: List<Checkpoint>
        )
    }
}

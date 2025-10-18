package life.munay.core.repositories.resource

import androidx.annotation.StringRes

interface ResourceRepository {
    fun getString(@StringRes id: Int): String
    fun getString(@StringRes id: Int, vararg arguments: Any): String
    fun getApplicationVersionInfo(): String
}

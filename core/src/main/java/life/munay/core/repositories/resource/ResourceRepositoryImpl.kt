package life.munay.core.repositories.resource

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import life.munay.core.extensions.getApplicationVersionInfo
import javax.inject.Inject

class ResourceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceRepository {
    override fun getString(@StringRes id: Int): String = runCatching { context.getString(id) }.getOrDefault("")
    override fun getString(@StringRes id: Int, vararg arguments: Any): String = runCatching { context.getString(id, *arguments) }.getOrDefault("")
    override fun getApplicationVersionInfo(): String = runCatching { context.getApplicationVersionInfo() }.getOrDefault("")
}

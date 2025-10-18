package life.munay.core.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import life.munay.core.repositories.resource.ResourceRepository

open class BaseViewModel(
    protected val resourceRepository: ResourceRepository
) : ViewModel() {
    // Navigation
    private val _destination = MutableSharedFlow<String>(replay = 0)
    val destination: SharedFlow<String> = _destination.asSharedFlow()

    protected fun getString(resId: Int): String = resourceRepository.getString(resId)
}

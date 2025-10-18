package life.munay.about.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import life.munay.core.repositories.resource.ResourceRepository
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val resourceRepository: ResourceRepository
) : ViewModel() {
    fun getApplicationVersionInfo(): String = resourceRepository.getApplicationVersionInfo()
}

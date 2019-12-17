package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    private val _status = MutableLiveData<String>()

    val status: LiveData<String>
        get() = _status

    private val _properties = MutableLiveData<List<MarsProperty>>()

    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties()
    }

    /**
     * Sets the value of the response LiveData to the Mars API status or the successful number of
     * Mars properties retrieved.
     */
    private fun getMarsRealEstateProperties() {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            var getPropertiesDeferred = MarsApi.retrofitService.getProperties()
            try {
                // Await the completion of our Retrofit request
                var listResult = getPropertiesDeferred.await()
                if (listResult.size > 0) {
                    _properties.value = listResult
                }
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
    }

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

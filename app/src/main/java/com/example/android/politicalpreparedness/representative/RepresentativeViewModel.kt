package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.data.ElectionRepository
import com.example.android.politicalpreparedness.data.remote.models.Address
import com.example.android.politicalpreparedness.data.remote.models.Official
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import java.lang.Exception

class RepresentativeViewModel(private val repository: ElectionRepository) : ViewModel() {

    var userFilledAddress = Address()

    var _address = MutableLiveData<Address>()
    var address: LiveData<Address> = _address

    private val _fetchLocationEvent = MutableLiveData<Boolean>()
    val fetchLocationEvent: LiveData<Boolean> = _fetchLocationEvent

    private val _openWebViewEvent = MutableLiveData<String>()
    val openWebViewEvent: LiveData<String> = _openWebViewEvent

    val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>> = _representatives

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    var stateItemPosition = MutableLiveData<Int>()
    var stateValue
        get() =
            stateItemPosition.value?.let {
                stateList?.get(it)
            }
        set(value) {
            val position = stateList?.indexOfFirst {
                it == value
            } ?: -1
            if (position != -1) {
                stateItemPosition.value = position
            }
        }

    var stateList: List<String>? = null

    init {
        stateItemPosition.value = 0
    }

    fun fetchRepresentatives() {
        if (_address.value != null) stateValue?.apply { _address.value!!.state = this }

        _address.value = userFilledAddress

        if (_address.value == null || _address.value!!.isEmpty()) {
            _toastMessage.value = "Please fill the details"
        } else {
            _dataLoading.value = true
            viewModelScope.launch {
                try {
                    val representativeResponse =
                        repository.fetchRepresentatives(address = _address.value!!.toFormattedString())
                    val reps = ArrayList<Representative>()
                    for (office in representativeResponse.offices) {
                        reps.addAll(office.getRepresentatives(representativeResponse.officials))
                    }
                    _representatives.value = reps

                    _dataLoading.value = false
                    if (_representatives.value.isNullOrEmpty()) {
                        _toastMessage.value = "No Representatives available at this location"
                    } else {
                        _toastMessage.value = "Representatives fetched successfully"
                    }
                } catch (ex: Exception) {
                    _dataLoading.value = false
                    _toastMessage.value = "Unable to load representatives : ${ex.localizedMessage}"
                }
            }
        }
    }

    fun fetchLocation() {
        _fetchLocationEvent.value = true
    }

    override fun onCleared() {
        super.onCleared()
        _fetchLocationEvent.value = false
    }

}

@Suppress("UNCHECKED_CAST")
class RepresentativeModelFactory(
    private val tasksRepository: ElectionRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (RepresentativeViewModel(tasksRepository) as T)
}

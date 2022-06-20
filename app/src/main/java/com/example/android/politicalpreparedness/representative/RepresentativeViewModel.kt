package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.data.ElectionRepository
import com.example.android.politicalpreparedness.data.remote.models.Address
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import java.lang.Exception

class RepresentativeViewModel(private val repository: ElectionRepository): ViewModel() {

    var address = Address()

    private val _fetchLocationEvent = MutableLiveData<Boolean>()
    val fetchLocationEvent : LiveData<Boolean> = _fetchLocationEvent

    private val _openWebViewEvent = MutableLiveData<String>()
    val openWebViewEvent : LiveData<String> = _openWebViewEvent

    val _representatives = MutableLiveData<List<Representative>>()
    val representatives : LiveData<List<Representative>> = _representatives

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    fun fetchRepresentatives(){
        if (address.isEmpty()){
            _toastMessage.value = "Please fill the details"
        } else {
            _dataLoading.value = true
            viewModelScope.launch {
                try {
                    repository.fetchRepresentatives(address = address.toFormattedString())
                    _dataLoading.value = false
                    _toastMessage.value = "Representatives fetched successfully"
                } catch (ex: Exception) {
                    _dataLoading.value = false
                    _toastMessage.value = "Unable to load representatives : ${ex.localizedMessage}"
                }
            }
        }
    }

    fun fetchLocation(){
        _fetchLocationEvent.value = true
    }

    override fun onCleared() {
        super.onCleared()
        _fetchLocationEvent.value = false
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location


    //TODO: Create function to get address from individual fields

}

@Suppress("UNCHECKED_CAST")
class RepresentativeModelFactory (
    private val tasksRepository: ElectionRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (VoterInfoViewModel(tasksRepository) as T)
}

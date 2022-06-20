package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.ElectionRepository
import com.example.android.politicalpreparedness.data.remote.models.Election
import kotlinx.coroutines.launch
import java.lang.Exception

class ElectionsViewModel(private val repository: ElectionRepository) : ViewModel() {

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElection: LiveData<List<Election>> = _savedElections

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>> = _upcomingElections

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _showUpcomingElectionData = MutableLiveData<Boolean>()
    val showUpcomingElectionData: LiveData<Boolean> = _showUpcomingElectionData

    private val _showSavedElectionData = MutableLiveData<Boolean>()
    val showSavedElectionData: LiveData<Boolean> = _showSavedElectionData

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _openVoterInfoEvent = MutableLiveData<Election>()
    val openVoterInfoEvent: LiveData<Election> = _openVoterInfoEvent

    fun openVoterInfo(election: Election) {
        _openVoterInfoEvent.value = election
    }

    init {
        fetchUpcomingElections()
        fetchSavedElections()
    }

    private fun fetchUpcomingElections() {
        viewModelScope.launch {
            _dataLoading.value = true
            try {
                _upcomingElections.value = repository.fetchUpcomingElections().elections
                _dataLoading.value = false
                _showUpcomingElectionData.value = true
            } catch (ex: Exception) {
                _dataLoading.value = false
                _showUpcomingElectionData.value = false
                _toastMessage.value = "Unable to load upcoming elections ${ex.localizedMessage}"
            }
        }
    }

    private fun fetchSavedElections() {
        viewModelScope.launch {
            try {
                _savedElections.value = repository.getSavedElections()
                _showSavedElectionData.value = true
            } catch (ex: Exception) {
                _showSavedElectionData.value = false
                _toastMessage.value = "Unable to load saved elections ${ex.localizedMessage}"
            }
        }
    }

    private fun doneNavigating() {
        _openVoterInfoEvent.value = null
    }

    override fun onCleared() {
        super.onCleared()
        doneNavigating()
    }
}
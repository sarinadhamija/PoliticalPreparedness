package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.ElectionRepository
import com.example.android.politicalpreparedness.data.remote.models.*
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val repository: ElectionRepository) : ViewModel() {

    private var _electionId: Int = 0
    private var division: Division? = null

    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election> = _election

    private val _electionOfficials = MutableLiveData<List<ElectionOfficial>?>()
    val electionOfficials: LiveData<List<ElectionOfficial>?> = _electionOfficials

    private val _state = MutableLiveData<State?>()
    val state : LiveData<State?> = _state

    private val _openWebViewEvent = MutableLiveData<String?>()
    val openWebViewEvent : LiveData<String?> = _openWebViewEvent

    val isMailingAddressAvailable : Boolean = _electionOfficials.value.isNullOrEmpty()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> = _isDataAvailable

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    fun initializeElection(electionId : Int, division : Division) {
        this._electionId = electionId
        this.division = division

        viewModelScope.launch {
            _dataLoading.value = true
            _isDataAvailable.value = false
            try {
                //TODO if not available in db then get that from server
                _election.value = repository.getElection(_electionId)
                if (_election.value == null){
                    repository.fetchUpcomingElections()
                } else {

                }
                _dataLoading.value = true
                _isDataAvailable.value = true
            } catch (ex: Exception) {
                _toastMessage.value = "Unable to load voter info : ${ex.localizedMessage}"
                _dataLoading.value = false
                _isDataAvailable.value = false
            }
        }

    }

    fun openElectionLocations(){
        _openWebViewEvent.value = state.value?.electionAdministrationBody?.electionInfoUrl
    }

    fun openBallotInformation(){
        _openWebViewEvent.value = state.value?.electionAdministrationBody?.ballotInfoUrl
    }

    fun followElection(shouldFollow: Boolean) {
        election.value?.apply {
            viewModelScope.launch {
                _dataLoading.value = true
                try {
                    if (shouldFollow) {
                        repository.saveElection(this@apply)
                    } else {
                        repository.deleteElection(this@apply.id)
                    }
                    _dataLoading.value = false
                } catch (ex: Exception) {
                    if (shouldFollow) {
                        _toastMessage.value = "Unable to follow election : ${ex.localizedMessage}"
                    } else {
                        _toastMessage.value = "Unable to unfollow election : ${ex.localizedMessage}"
                    }
                    _dataLoading.value = false
                }
            }
        }
    }

}
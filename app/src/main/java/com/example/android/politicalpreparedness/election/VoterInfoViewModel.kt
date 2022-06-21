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

    private val _isAvailableInDb = MutableLiveData<Boolean>()
    val isAvailableInDb: LiveData<Boolean> = _isAvailableInDb

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse> = _voterInfo

    private val _openWebViewEvent = MutableLiveData<String?>()
    val openWebViewEvent: LiveData<String?> = _openWebViewEvent

    val isMailingAddressAvailable: Boolean =
        voterInfo.value?.electionElectionOfficials.isNullOrEmpty()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> = _isDataAvailable

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    fun initializeElection(electionId: Int, division: Division) {
        this._electionId = electionId
        this.division = division

        viewModelScope.launch {
            try {
                _dataLoading.value = true
                _voterInfo.value =
                    repository.fetchElection(electionId, "${division.country},${division.state}")

                _isAvailableInDb.value =
                    repository.getSavedElections().singleOrNull { it.id == electionId } != null

                _dataLoading.value = false
            } catch (ex: Exception) {
                _toastMessage.value = "Unable to load voter info : ${ex.localizedMessage}"
                _dataLoading.value = false
            }
        }

    }

    fun openElectionLocations() {
        voterInfo.value?.apply {
            if (this.state.isNullOrEmpty()) {
                _toastMessage.value = " No URL available"
            } else {
                var url = ""
                for (state in this.state) {
                    if (!state.electionAdministrationBody.electionInfoUrl.isNullOrEmpty()) {
                        url = state.electionAdministrationBody.electionInfoUrl
                        break
                    }
                }
                if (url.isEmpty()) _toastMessage.value = " No URL available"
                else _openWebViewEvent.value = url
            }
        }

    }

    fun openBallotInformation() {
        voterInfo.value?.apply {
            if (this.state.isNullOrEmpty()) {
                _toastMessage.value = " No URL available"
            } else {
                var url = ""
                for (state in this.state) {
                    if (!state.electionAdministrationBody.ballotInfoUrl.isNullOrEmpty()) {
                        url = state.electionAdministrationBody.ballotInfoUrl
                        break
                    }
                }
                if (url.isEmpty()) _toastMessage.value = " No URL available"
                else _openWebViewEvent.value = url
            }
        }
    }

    fun followElection(shouldFollow: Boolean) {
        voterInfo.value?.election?.apply {
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

    fun doneNavigating() {
        _openWebViewEvent.value = null
    }

}
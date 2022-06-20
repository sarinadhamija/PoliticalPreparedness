package com.example.android.politicalpreparedness.data

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.local.ElectionDatabase
import com.example.android.politicalpreparedness.data.remote.CivicsApi
import com.example.android.politicalpreparedness.data.remote.models.Election
import com.example.android.politicalpreparedness.data.remote.models.ElectionResponse
import com.example.android.politicalpreparedness.data.remote.models.RepresentativeResponse
import com.example.android.politicalpreparedness.data.remote.models.VoterInfoResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionInfoRepository(
    private val database: ElectionDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ElectionRepository {
    override suspend fun fetchUpcomingElections(): ElectionResponse {
        return CivicsApi.retrofitService.getElectionList()
    }

    override suspend fun fetchElection(electionId: Int, address : String) : VoterInfoResponse {
        return CivicsApi.retrofitService.getVoterInfo(electionID = electionId, address = address)
    }

    override suspend fun fetchRepresentatives(address: String): RepresentativeResponse {
        return CivicsApi.retrofitService.getRepresentativeList(address = address)
    }

    override suspend fun observeSavedElections(): LiveData<List<Election>> {
        return withContext(ioDispatcher) {
            database.electionDao.observeElections()
        }
    }

    override suspend fun getSavedElections(): List<Election> = withContext(ioDispatcher) {
        database.electionDao.getElectionList()
    }

    override suspend fun getElection(electionId: Int) = withContext(ioDispatcher) {
        database.electionDao.getElection(electionId)
    }

    override suspend fun saveElection(election: Election) = withContext(ioDispatcher) {
        database.electionDao.insert(election)
    }

    override suspend fun deleteElection(electionId: Int) = withContext(ioDispatcher) {
        database.electionDao.deleteElection(electionId)
    }

}
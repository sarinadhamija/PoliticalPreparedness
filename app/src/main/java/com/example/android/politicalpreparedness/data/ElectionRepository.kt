package com.example.android.politicalpreparedness.data

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.remote.models.Election
import com.example.android.politicalpreparedness.data.remote.models.ElectionResponse
import com.example.android.politicalpreparedness.data.remote.models.RepresentativeResponse
import com.example.android.politicalpreparedness.data.remote.models.VoterInfoResponse

interface ElectionRepository {

    suspend fun fetchUpcomingElections() : ElectionResponse

    suspend fun fetchElection(electionId: Int, address: String) : VoterInfoResponse

    suspend fun fetchRepresentatives(address: String) : RepresentativeResponse

    suspend fun observeSavedElections() : LiveData<List<Election>>

    suspend fun getSavedElections() : List<Election>

    suspend fun getElection(electionId : Int) : Election?

    suspend fun saveElection(election: Election)

    suspend fun deleteElection(electionId: Int)
}
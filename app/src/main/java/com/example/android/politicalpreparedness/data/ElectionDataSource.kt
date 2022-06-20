package com.example.android.politicalpreparedness.data

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.remote.models.Election

interface ElectionDataSource {
    fun observeElections() : LiveData<List<Election>>

    suspend fun getElections() : List<Election>

    suspend fun getElection(electionId : Int) : Election

    suspend fun saveElection(election: Election)

    suspend fun deleteElection(electionId: Int)
}
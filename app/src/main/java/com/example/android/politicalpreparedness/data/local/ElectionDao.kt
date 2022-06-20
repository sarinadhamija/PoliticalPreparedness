package com.example.android.politicalpreparedness.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.data.remote.models.Election

@Dao
interface ElectionDao {

    @Query("select * from election_table")
    fun observeElections() : LiveData<List<Election>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(election : Election)

    @Query("select * from election_table")
    fun getElectionList() : List<Election>

    @Query("select * from election_table where id = :electionId")
    fun getElection(electionId : Int) : Election

    @Query("delete from election_table where id = :electionId")
    fun deleteElection(electionId: Int)

    @Query("delete from election_table")
    fun clear()
}
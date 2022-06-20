package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.ElectionRepository

@Suppress("UNCHECKED_CAST")
class VoterInfoViewModelFactory (
    private val tasksRepository: ElectionRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (VoterInfoViewModel(tasksRepository) as T)
}
package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.data.ElectionRepository

class PoliticalPreparednessApplication : Application() {
    val electionRepository: ElectionRepository
        get() = ServiceLocator.provideElectionsRepository(this)
}
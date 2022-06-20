package com.example.android.politicalpreparedness

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.example.android.politicalpreparedness.data.ElectionInfoRepository
import com.example.android.politicalpreparedness.data.ElectionRepository
import com.example.android.politicalpreparedness.data.local.ElectionDatabase

/**
 * A Service Locator for the [TasksRepository]. This is the prod version, with a
 * the "real" [TasksRemoteDataSource].
 */
object ServiceLocator {
    private val lock = Any()
    private var database: ElectionDatabase? = null

    @Volatile
    var tasksRepository: ElectionRepository? = null
        @VisibleForTesting set

    fun provideElectionsRepository(context: Context): ElectionRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): ElectionRepository {
        val newRepo = ElectionInfoRepository(createElectionsLocalDataSource(context))
        tasksRepository = newRepo
        return newRepo
    }

    private fun createElectionsLocalDataSource(context: Context): ElectionDatabase {
        if (database == null) {
            database = ElectionDatabase.getInstance(context)
        }
        return database!!
    }
}

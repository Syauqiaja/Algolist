package com.aglotest.algolist.di

import android.content.Context
import com.aglotest.algolist.data.database.AlgolistDatabase
import com.aglotest.algolist.data.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun getAlgolistDatabase(@ApplicationContext context: Context): AlgolistDatabase {
        try {
            return AlgolistDatabase.getInstance(context)
        }catch (e:Exception){
            Timber.tag("DatabaseModule").e(e)
            throw e
        }
    }

    @Provides
    fun provideTaskDao(algolistDatabase: AlgolistDatabase): TaskDao = algolistDatabase.getTaskDao()
}
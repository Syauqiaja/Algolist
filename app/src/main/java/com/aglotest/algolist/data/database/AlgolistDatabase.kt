package com.aglotest.algolist.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aglotest.algolist.data.dao.TaskDao
import com.aglotest.algolist.data.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AlgolistDatabase:RoomDatabase() {
    abstract fun getTaskDao() : TaskDao

    companion object{
        @Volatile private var INSTANCE: AlgolistDatabase? = null
        fun getInstance(context: Context): AlgolistDatabase {
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context,
                    AlgolistDatabase::class.java,
                    "Algolist.db"
                ).fallbackToDestructiveMigration()
                    .addCallback(PrepopulateRoomCallback(context))
                    .build()
            }
        }
    }
}
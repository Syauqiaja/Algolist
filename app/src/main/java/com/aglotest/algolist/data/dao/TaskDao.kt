package com.aglotest.algolist.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.aglotest.algolist.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: TaskEntity)
    @Insert
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Query("SELECT * FROM tasks ORDER BY taskDate, priority")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Transaction
    suspend fun updateItemPositions(tasks: List<TaskEntity>){
        tasks.forEach { task ->
            updateTask(task)
        }
    }
}
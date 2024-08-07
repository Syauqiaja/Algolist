package com.aglotest.algolist.data.database

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aglotest.algolist.R
import com.aglotest.algolist.data.entity.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber

class PrepopulateRoomCallback(private val context: Context) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            prepoulateTaskData(context, AlgolistDatabase.getInstance(context))
        }
    }

    private suspend fun prepoulateTaskData(context: Context, database: AlgolistDatabase) {
        try {
            val taskDao = database.getTaskDao()
            val taskList : JSONArray = context.resources.openRawResource(R.raw.dummy_task_data).bufferedReader().use {
                JSONArray(it.readText())
            }
            taskList.takeIf { it.length() > 0 }?.let { tasks ->
                val taskEntities = arrayListOf<TaskEntity>()
                for (index in 0 until tasks.length()){
                    val taskObj = tasks.getJSONObject(index)
                    val taskEntity = TaskEntity(
                        title = taskObj.getString("title"),
                        description = taskObj.getString("description"),
                        taskDate = taskObj.getString("taskDate"),
                        isChecked = false,
                        time = taskObj.optString("time"),
                        priority = index
                    )
                    taskEntities.add(taskEntity)
                }
                taskDao.insertTasks(taskEntities)
            }
        }catch (e:Exception){
            Timber.tag("PrepopulateDatabaseCallback").e(e)
        }
    }
}
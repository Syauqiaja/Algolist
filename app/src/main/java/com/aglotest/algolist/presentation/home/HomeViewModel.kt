package com.aglotest.algolist.presentation.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aglotest.algolist.data.dao.TaskDao
import com.aglotest.algolist.data.entity.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskDao: TaskDao
) :ViewModel() {
    val tasks : Flow<List<TaskEntity>> = flow {
        var _tasks : List<TaskEntity>? = null
        while (_tasks == null){
            _tasks = taskDao.getAllTasks().firstOrNull()
            delay(500)
        }
        emit(_tasks)
    }

    fun updateData(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.updateTask(task)
        }
    }

    fun deleteItem(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.delete(task)
        }
    }

    fun updateItemPositions(tasks: List<TaskEntity>){
        viewModelScope.launch {
            taskDao.updateItemPositions(tasks)
        }
    }
}


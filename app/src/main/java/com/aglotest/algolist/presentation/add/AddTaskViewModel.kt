package com.aglotest.algolist.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aglotest.algolist.data.dao.TaskDao
import com.aglotest.algolist.data.entity.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskDao: TaskDao
) :ViewModel() {
    fun insertTask(taskEntity: TaskEntity) {
        viewModelScope.launch {
            taskDao.insertTask(taskEntity)
        }
    }
}
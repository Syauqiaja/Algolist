package com.aglotest.algolist.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo("description")
    val description: String,
    @ColumnInfo(name = "time")
    val time: String?,
    @ColumnInfo(name = "isChecked")
    var isChecked: Boolean,
    @ColumnInfo("taskDate")
    var taskDate: String,
    @ColumnInfo("priority")
    var priority: Int
)
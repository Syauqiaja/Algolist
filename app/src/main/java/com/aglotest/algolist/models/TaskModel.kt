package com.aglotest.algolist.models


class TaskEntity(
    val taskId: Int = 0,
    val title: String,
    val description: String,
    val time: String?,
    val isChecked: Boolean,
    val taskDate: String
)
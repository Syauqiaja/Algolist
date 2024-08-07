package com.aglotest.algolist.utils

import androidx.recyclerview.widget.DiffUtil
import com.aglotest.algolist.data.entity.TaskEntity

class TaskDiffUtil(
    private val oldList: List<TaskEntity>,
    private val newList: List<TaskEntity>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].taskId == newList[newItemPosition].taskId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
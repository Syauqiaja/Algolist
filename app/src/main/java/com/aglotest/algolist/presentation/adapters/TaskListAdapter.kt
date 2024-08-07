package com.aglotest.algolist.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aglotest.algolist.data.entity.TaskEntity
import com.aglotest.algolist.databinding.ItemTaskBinding
import com.aglotest.algolist.utils.TaskDiffUtil
import com.aglotest.algolist.utils.scaleViewOneShot
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

class TaskListAdapter(private val lifecycleScope: LifecycleCoroutineScope) :RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {
    var onDeleteClicked: ((viewHolder: ViewHolder, data: TaskEntity)->Unit)? = null
    val listItem: CopyOnWriteArrayList<TaskEntity> = CopyOnWriteArrayList()
    var onCheckChange: ((data: TaskEntity) -> Unit)? = null
    var onDateChange: ((data: TaskEntity) -> Unit)? = null

    fun submitData(newListItem: MutableList<TaskEntity>){
        val diffCallback = TaskDiffUtil(this.listItem, newListItem)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        listItem.clear()
        listItem.addAll(newListItem)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root) {
        private var isCheckedJob: Job = Job()

        fun showCardDelete(){
            binding.apply {
                if(cardTask.currentState == cardTask.endState){
                    cardTask.transitionToStart()
                }else{
                    cardTask.transitionToEnd()
                }
            }
        }
        fun hideCardDelete(){
            binding.cardTask.transitionToStart()
        }
        fun bind(data: TaskEntity){
            binding.apply {
                tvTaskTitle.text = data.title
                checkbox.isChecked = data.isChecked
                lotCheckbox.progress = if(data.isChecked) 1f else 0f
                root.visibility = View.VISIBLE
                if(!data.time.isNullOrBlank()){
                    tvTaskTime.text = data.time
                }else{
                    tvTaskTime.visibility = View.GONE
                }
                root.setOnClickListener {
                    isCheckedJob.cancel()
                    checkbox.toggle()
                    if(checkbox.isChecked){
                        lotCheckbox.apply {
                            speed = 2f
                            playAnimation()
                        }
                    }else{
                        lotCheckbox.apply {
                            speed = -2f
                            playAnimation()
                        }
                    }
                    isCheckedJob = lifecycleScope.launch {
                        delay(500)
                        data.isChecked = checkbox.isChecked
                        onCheckChange?.invoke(data)
                    }
                    cardTask.scaleViewOneShot(0.95f, 100)
                }
                btnDelete.setOnClickListener {
                    onDeleteClicked?.invoke(this@ViewHolder, data)
                }
            }
        }
        fun deleteItem(data: TaskEntity){
            binding.apply {
                val index = listItem.indexOfFirst { it.taskId == data.taskId }
                listItem.removeAt(index)
                root.visibility = View.GONE
                notifyItemRemoved(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = listItem.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listItem[position].priority = position
        holder.bind(listItem[position])
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        listItem[fromPosition].taskDate = listItem[toPosition].taskDate
        onDateChange?.invoke(listItem[fromPosition])

        val tempPriority = listItem[fromPosition].priority
        listItem[fromPosition].priority = listItem[toPosition].priority
        listItem[toPosition].priority = tempPriority

        val item = listItem.removeAt(fromPosition)
        listItem.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, item)
    }
}
package com.aglotest.algolist.presentation.home

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aglotest.algolist.R
import com.aglotest.algolist.data.entity.TaskEntity
import com.aglotest.algolist.databinding.FragmentHomeBinding
import com.aglotest.algolist.databinding.SnackbarDefaultBinding
import com.aglotest.algolist.presentation.BaseFragment
import com.aglotest.algolist.presentation.adapters.ItemSectionDecoration
import com.aglotest.algolist.presentation.adapters.TaskListAdapter
import com.aglotest.algolist.presentation.dialogs.BottomSheetDeleteConfirmation
import com.aglotest.algolist.presentation.dialogs.DialogSuccessAdd
import com.aglotest.algolist.utils.getNavigationResult
import com.aglotest.algolist.utils.safeNavigate
import com.aglotest.algolist.utils.scaleView
import com.aglotest.algolist.utils.scaleViewOneShot
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import java.time.Duration


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate
) {
    override val viewModel: HomeViewModel by viewModels()
    private lateinit var taskListAdapter: TaskListAdapter
    private lateinit var itemSectionDecoration: ItemSectionDecoration

    override fun initView() {
        initTaskListAdapter()
        initRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                taskListAdapter.submitData(viewModel.tasks.first().toMutableList())
            }
        }
        getNavigationResult<String>()?.observe(viewLifecycleOwner){
            if(it != null){
                DialogSuccessAdd().show(childFragmentManager, "DialogSuccess")
            }
        }

        binding.btnAddTask.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddTaskFragment()
            findNavController().safeNavigate(action)
        }
    }

    private fun initTaskListAdapter() {
        taskListAdapter = TaskListAdapter(viewLifecycleOwner.lifecycleScope)
        taskListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                binding.rvTodo.invalidateItemDecorations()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                binding.rvTodo.invalidateItemDecorations()
            }
        })
        taskListAdapter.onDeleteClicked = {viewHolder, data ->
            //Show confirmation dialog when on item delete button clicked
            val confirmationDialog = BottomSheetDeleteConfirmation(onDialogButtonClick(viewHolder, data))
            confirmationDialog.show(childFragmentManager, "DialogDeleteConfirmation")
        }
        taskListAdapter.onCheckChange = {task ->
            viewModel.updateData(task)
        }
        taskListAdapter.onDateChange = {task ->
            viewModel.updateData(task)
        }
    }

    private fun initRecyclerView() {
        itemSectionDecoration = ItemSectionDecoration(requireContext()){
            taskListAdapter.listItem
        }
        binding.rvTodo.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTodo.addItemDecoration(itemSectionDecoration)
        binding.rvTodo.adapter = taskListAdapter

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvTodo)
    }

    private fun onDialogButtonClick(viewHolder: TaskListAdapter.ViewHolder, data: TaskEntity) = object : BottomSheetDeleteConfirmation.OnButtonClicked{
        override fun onCancel() {
            viewHolder.hideCardDelete()
        }

        override fun onDelete() {
            viewModel.deleteItem(data)
            viewHolder.deleteItem(data)
        }

    }

    var simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
        ItemTouchHelper.UP or ItemTouchHelper.DOWN
    ) {
        private var isDragging = false
        private var movePositionJob: Job = Job()
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            movePositionJob.cancel()

            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            taskListAdapter.moveItem(fromPosition, toPosition)
            taskListAdapter.notifyItemMoved(fromPosition, toPosition)
            isDragging = true

            movePositionJob = lifecycleScope.launch {
                delay(1000)
                updatePositionsInDatabase()
            }
            return false
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            if (!isDragging) {
                (viewHolder as TaskListAdapter.ViewHolder).showCardDelete()
            }
            isDragging = false
            viewHolder.itemView.scaleView(1.0f, 100) // Scale back to original size
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                isDragging = false
            }
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
                viewHolder.itemView.scaleView(1.1f, 100) // Scale down to 95% of the original size
            } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && viewHolder != null) {
                viewHolder.itemView.scaleView(1.0f, 100) // Scale back to original size
            }
        }
        private fun updatePositionsInDatabase() {
            for ((index, task) in taskListAdapter.listItem.withIndex()) {
                task.priority = index
            }
            viewModel.updateItemPositions(taskListAdapter.listItem)
        }
    }

    private fun showCustomSnackBar(message: String) {
        binding.root.let {
            val snackView = View.inflate(requireContext(), R.layout.snackbar_default, null)
            val binding = SnackbarDefaultBinding.bind(snackView)
            val snackBar = Snackbar.make(it, "", Snackbar.LENGTH_SHORT)
            snackBar.apply {
                (view as ViewGroup).addView(binding.root)
                binding.toastText.text = message
                duration = Snackbar.LENGTH_SHORT
                setBackgroundTint(
                    MaterialColors.getColor(
                        binding.root,
                        com.google.android.material.R.attr.colorOnPrimary
                    )
                )
                show()
            }
        }
    }
}
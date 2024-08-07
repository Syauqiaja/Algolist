package com.aglotest.algolist.presentation.add

import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aglotest.algolist.R
import com.aglotest.algolist.data.entity.TaskEntity
import com.aglotest.algolist.databinding.FragmentAddTaskBinding
import com.aglotest.algolist.databinding.SnackbarDefaultBinding
import com.aglotest.algolist.presentation.BaseFragment
import com.aglotest.algolist.presentation.dialogs.BottomSheetDatePicker
import com.aglotest.algolist.presentation.dialogs.BottomSheetTimePicker
import com.aglotest.algolist.utils.setNavigationResult
import com.google.android.material.color.MaterialColors.getColor
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddTaskFragment : BaseFragment<FragmentAddTaskBinding, AddTaskViewModel>(
    FragmentAddTaskBinding::inflate
) {
    override val viewModel: AddTaskViewModel by viewModels()

    override fun initView() {
        setNavigationResult(null)
        binding.apply {
            edtTitle.addTextChangedListener(titleTextWatcher)
            toolbar.setNavigationOnClickListener{
                findNavController().popBackStack()
            }
            edtDate.setOnClickListener {
                showDatePicker()
            }
            switchTime.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    showTimePicker()
                }else{
                    binding.tvTime.text = "Time"
                    binding.tvTime.setTextColor(com.google.android.material.R.attr.colorOnSurface)
                }
            }
            btnCancel.setOnClickListener {
                findNavController().popBackStack()
            }
            btnSave.setOnClickListener {
                if(validateInputs()){
                    val taskEntity = TaskEntity(
                        title = edtTitle.text!!.trim().toString(),
                        description = edtDescription.text!!.trim().toString(),
                        taskDate = edtDate.text!!.trim().toString(),
                        time = if(tvTime.text.toString().equals("Time", true)){
                            ""
                        }else{
                            tvTime.text.toString()
                        },
                        isChecked = false,
                        priority = 10000
                    )
                    viewModel.insertTask(taskEntity)
                    setNavigationResult(taskEntity.taskDate)
                    findNavController().popBackStack()
                }else{
                    showCustomSnackBar("Fill the required field first")
                }
            }
        }
    }

    private fun validateInputs():Boolean{
        var result = true
        binding.apply {
            if((edtTitle.text?.trim()?.length ?: 0) == 0){
                result = false
            }

            if((edtDescription.text?.trim()?.length ?: 0) == 0){
                result = false
            }

            if((edtDate.text?.trim()?.length ?: 0) == 0){
                result = false
            }
        }
        return result
    }

    private fun showTimePicker() {
        val timePicker = BottomSheetTimePicker(object : BottomSheetTimePicker.OnButtonClicked{
            override fun onCancel() {
                binding.switchTime.isChecked = false
            }

            override fun onSave(hour: Int, minute: Int) {
                binding.tvTime.text = String.format("%02d:%02d", hour, minute)
                binding.tvTime.setTextColor(resources.getColor(R.color.primary, null))
            }

        })
        timePicker.show(childFragmentManager, "BottomSheetTimePicker")
    }

    private fun showDatePicker() {
        val datePicker = BottomSheetDatePicker(){date ->
            binding.edtDate.setText(date)
        }
        datePicker.show(childFragmentManager, "BottomSheetDatePicker")
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
                setBackgroundTint(getColor(binding.root, com.google.android.material.R.attr.colorOnPrimary))
                show()
            }
        }
    }

    private val titleTextWatcher: TextWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if(s.toString().contains("today", true)){
                binding.edtDate.setText(getCurrentCalendar())
                highlightText(binding.edtTitle.text!!, "today")
            }else{
                binding.edtDate.text = null
            }
        }

    }

    private fun highlightText(text: Editable, wordToHighlight: String): SpannableString {
        val spannable = SpannableString(text)
        val startIndex = text.indexOf(wordToHighlight)
        if (startIndex >= 0) {
            val endIndex = startIndex + wordToHighlight.length
            text.setSpan(
                BackgroundColorSpan(Color.MAGENTA),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text.setSpan(
                ForegroundColorSpan(Color.WHITE),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }

    private fun getCurrentCalendar(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
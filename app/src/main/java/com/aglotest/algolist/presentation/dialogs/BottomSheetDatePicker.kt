package com.aglotest.algolist.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aglotest.algolist.databinding.BottomSheetDatePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BottomSheetDatePicker(private val onDatePicked: (String)->Unit): BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDatePickerBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetDatePickerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnSave.setOnClickListener {
                val dateInLong = calendarView.date
                onDatePicked.invoke(convertLongToTime(dateInLong))
                dismiss()
            }
            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                val date = Calendar.getInstance()
                date.set(year, month, dayOfMonth)
                val stringDate = calendarToString(date)
                view.date = convertDateToLong(stringDate)
            }
        }
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }
    fun calendarToString(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("yyyy-MM-dd")
        return df.parse(date).time
    }
}

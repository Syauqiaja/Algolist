package com.aglotest.algolist.presentation.adapters

import android.content.Context
import android.graphics.Canvas
import android.widget.FrameLayout
import android.widget.TextView
import com.aglotest.algolist.R
import com.aglotest.algolist.databinding.DividerItemTaskBinding
import com.aglotest.algolist.utils.formatDate

class SectionViewHolder(context: Context): FrameLayout(context) {
    private lateinit var tvDate: TextView
    init {
        inflate(context, R.layout.divider_item_task, this)
        findView()
    }

    private fun findView() {
        tvDate = findViewById(R.id.tv_headline)
    }

    fun setDate(date: String){
        tvDate.text = formatDate(date)
    }
}
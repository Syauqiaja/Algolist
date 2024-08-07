package com.aglotest.algolist.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aglotest.algolist.databinding.BottomSheetDeleteConfirmationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDeleteConfirmation(private val onButtonClicked: OnButtonClicked):BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDeleteConfirmationBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetDeleteConfirmationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnDelete.setOnClickListener {
                onButtonClicked.onDelete()
                dismiss()
            }
            btnCancel.setOnClickListener {
                onButtonClicked.onCancel()
                dismiss()
            }
        }
    }

    public interface OnButtonClicked{
        abstract fun onCancel()
        abstract fun onDelete()
    }
}
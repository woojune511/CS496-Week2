package com.example.madcamp_week1.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.madcamp_week1.R
import com.example.madcamp_week1.ui.main.map.MapFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.ClassCastException

class ChooseCityDialog(val cityFileList : Array<String>, var index: Int) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val cityList = cityFileList.map{ s -> s.let{
                val arr = it.split('_')
                "${arr[0]} ${arr[1]}"
            }}.toTypedArray()
            val selectedItem = null // Where we track the selected items
            val builder =  MaterialAlertDialogBuilder(it, R.style.AlertDialogTheme)
            // Set the dialog title
            builder.setTitle("동네 선택")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(cityList, index,
                    DialogInterface.OnClickListener() { _, which ->
                        index = which
                    })
                // Set the action buttons
                .setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        //listener.onDialogPositiveClick(this, index)
                        (targetFragment as MapFragment).onCityDialogResult(true, index)
                    })
                .setNegativeButton(
                    R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        //listener.onDialogNegativeClick(this, index)
                        (targetFragment as MapFragment).onCityDialogResult(false, index)
                    })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
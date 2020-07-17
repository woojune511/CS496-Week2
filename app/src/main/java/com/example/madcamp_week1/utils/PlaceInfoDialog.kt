package com.example.madcamp_week1.utils

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.madcamp_week1.R
import com.example.madcamp_week1.model.PlaceInfo

public class PlaceInfoDialog(private val placeInfo : PlaceInfo) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val message = """
                주소: ${placeInfo.address}
                개방시간:
                ${placeInfo.hours.replace('+', ' ')}
                전화번호: ${placeInfo.phone}
                """.trimIndent()
            // Inflate and set the layout for the dialog_number
            // Pass null as the parent view because its going in the dialog_number layout
            builder
                // Add action buttons
                .setPositiveButton(R.string.ok, null)
                .setTitle(placeInfo.placeName)
                .setMessage(message)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
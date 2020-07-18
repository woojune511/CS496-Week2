package com.example.madcamp_week1.ui.main.contact

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.madcamp_week1.R

class dialogFragment(val parent : ContactFragment) : DialogFragment() {

    compositeDisposable

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.add_contact, null)
        builder.setView(view)
        val submit = view.findViewById<View>(R.id.buttonSubmit) as Button
        val name = view.findViewById<View>(R.id.edittextEmailAddress) as EditText
        val number = view.findViewById<View>(R.id.edittextPassword) as EditText
        submit.setOnClickListener {
            val strName = name.text.toString()
            val strNumber = number.text.toString()
            val data = Intent()
            data.putExtra("name", strName)
            data.putExtra("number", strNumber)
            targetFragment!!.onActivityResult(
                targetRequestCode,
                Activity.RESULT_OK,
                data
            )
            parent.writeContact(strName, strNumber)
            dismiss()
        }
        return builder.create()
    }
}
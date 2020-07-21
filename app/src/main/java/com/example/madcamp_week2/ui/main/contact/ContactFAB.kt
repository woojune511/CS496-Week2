package com.example.madcamp_week2.ui.main.contact

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp_week2.R

class ContactFAB(): AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.fragment_contact_add)
        val nameInput = findViewById<View>(R.id.NameInput) as EditText
        val numInput = findViewById<View>(R.id.NumInput) as EditText
        val confirmButton = findViewById<View>(R.id.ConfirmButton) as Button
        val cancelButton = findViewById<View>(R.id.CancelButton) as Button
        confirmButton.setOnClickListener {
            val intent = Intent()
            val strName = nameInput.text.toString()
            val strNumber = numInput.text.toString()

            when {
                TextUtils.isEmpty(strName) -> {
                    Toast.makeText(this, "Name cannot be null or empty", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(strNumber) -> {
                    Toast.makeText(this, "Number cannot be null or empty", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    intent.putExtra("newName", strName)
                    intent.putExtra("newNum", strNumber)
                    setResult(Activity.RESULT_OK, intent)
                    //parent.writeContact(strName, strNumber)
                    finish()
                }
            }
        }
        cancelButton.setOnClickListener { finish() }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return event.action != MotionEvent.ACTION_OUTSIDE
    }

    override fun onBackPressed() {
        return
    }
}
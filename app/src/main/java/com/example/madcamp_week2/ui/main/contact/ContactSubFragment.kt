package com.example.madcamp_week2.ui.main.contact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp_week2.R

class ContactSubFragment: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_contact_sub)
        val intent = Intent(this.intent)
        val name = intent.getStringExtra("name")
        val number = intent.getStringExtra("number")
        val nametextview = findViewById<View>(R.id.nameTextView) as TextView
        val numtextview = findViewById<View>(R.id.numTextView) as TextView
        nametextview.text = name
        numtextview.text = number
        val callButton =
            findViewById<View>(R.id.CallButton) as Button
        val textButton =
            findViewById<View>(R.id.TextButton) as Button
        callButton.setOnClickListener {
            startActivity(
                Intent(
                    "android.intent.action.DIAL",
                    Uri.parse("tel:$number")
                )
            )
        }
        textButton.setOnClickListener {
            startActivity(
                Intent(
                    "android.intent.action.SENDTO",
                    Uri.parse("sms:$number")
                )
            )
        }
    }
}
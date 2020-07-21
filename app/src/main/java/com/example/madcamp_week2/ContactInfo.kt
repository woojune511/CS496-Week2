package com.example.madcamp_week2

import com.example.madcamp_week2.ui.main.contact.PhoneBook
import com.google.gson.annotations.SerializedName

class ContactInfo (
    @field:SerializedName("_id") val id: String,
    val ContactList: List<PhoneBook>,
    @field:SerializedName("_v") val v: Int
)

class PhoneBook_bak(
    @field:SerializedName("_id") var id: String,
    var name: String,
    var number: String
)
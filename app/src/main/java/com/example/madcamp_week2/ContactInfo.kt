package com.example.madcamp_week2

import com.google.gson.annotations.SerializedName

class ContactInfo (
    @field:SerializedName("_id") val id: String,
    val ContactList: List<EntryInfo>,
    @field:SerializedName("_v") val v: Int
)

class EntryInfo(
    @field:SerializedName("_id") val id: String,
    val name: String,
    val number: String
)
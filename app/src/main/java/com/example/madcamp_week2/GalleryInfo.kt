package com.example.madcamp_week2

import com.google.gson.annotations.SerializedName

class GalleryInfo (
        @field:SerializedName("_id") val id: String,
        //val uid: Int,
        val ImageList: List<ImageInfo>,
        @field:SerializedName("_v") val v: Int
)

class ImageInfo(
        @field:SerializedName("_id") val id: String,
        val path: String = ""
)
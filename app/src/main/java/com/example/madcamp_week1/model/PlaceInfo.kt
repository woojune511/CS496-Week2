package com.example.madcamp_week1.model

import android.util.Log
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint

class PlaceInfo (
    val placeName : String,
    val x: Double,
    val y: Double,
    val address: String,
    val phone: String,
    val hours: String
){
    fun toMapPOIItem() : MapPOIItem{
        val marker = MapPOIItem()
        Log.d("TAG", placeName)
        marker.itemName = placeName
        val mapPoint = MapPoint.mapPointWithGeoCoord(x, y)
        marker.mapPoint = mapPoint
        marker.userObject = this
        return marker
    }
}
package com.example.madcamp_week1.ui.main.map

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.madcamp_week1.R
import com.example.madcamp_week1.model.PlaceInfo
import com.example.madcamp_week1.utils.ChooseCityDialog
import com.example.madcamp_week1.utils.PlaceInfoDialog
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPoint.GeoCoordinate
import net.daum.mf.map.api.MapView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset


class MapFragment : Fragment(), MapView.CurrentLocationEventListener, MapView.POIItemEventListener, View.OnClickListener{
    private val TAG = "MapTAG"
    private val MY_PERMISSIONS_REQUEST_PERMISSION = 1
    private lateinit var mapViewContainer : ViewGroup
    private lateinit var mapView : MapView
    private lateinit var fab : FloatingActionButton
    private var currentMapPoint : MapPoint? = null
    private var isTrackingMode = false
    private var currentLng: Double? = null
    private var currentLat: Double? = null
    private var cityFileIndex = 1
    private lateinit var cityFileList : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cityFileList = context?.assets?.list("csv") ?: throw Exception("City file not set")
        Log.d(TAG, ">>> ${cityFileList[0]}")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = MapView(context)
        mapViewContainer = view.findViewById<ViewGroup>(R.id.map_view)
        mapViewContainer.addView(mapView)
        fab = view.findViewById(R.id.fab_location)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.setCurrentLocationEventListener(this)

        val toiletPOIs = getPOIItems("csv/${cityFileList[cityFileIndex]}")
        mapView.addPOIItems(toiletPOIs.toTypedArray())
        mapView.setPOIItemEventListener(this)

        fab.setOnClickListener(this)
    }

    private fun getPOIItems(srcFile: kotlin.String) : List<MapPOIItem>{
        val toilets = mutableListOf<PlaceInfo>()
        val assetManager = context!!.assets
        try{
            //val input = InputStreamReader(assetManager.open(srcFile), Charset.forName("euc-kr"))
            //val reader = BufferedReader(input)
            val tsvReader = csvReader{
                charset = "euc-kr"
            }
            val data: List<List<String>> = tsvReader.readAll(assetManager.open(srcFile)).drop(1)
            for (args in data) {
                val name = args[1]
                Log.d(TAG, "$name ${args[18]} ${args[19]}")
                val x = args[18].toDouble()
                val y = args[19].toDouble()
                val address = args[3]
                val phone = args[15]
                val hours = args[16]
                val place = PlaceInfo(name, x, y, address, phone, hours)
                toilets.add(place)
            }
            Log.d(TAG, "finished")
            Log.d(TAG, "finished")
        }catch(e: Exception){
            Log.d(TAG, e.toString())
        }
        return toilets.map{x -> x.toMapPOIItem()}
    }


    override fun onCurrentLocationUpdate(mapView: MapView?, mapPoint: MapPoint?, accuracyInMeters: Float) {
        val mapPointGeo: GeoCoordinate = mapPoint!!.mapPointGeoCoord
        Log.i(
            TAG,
            String.format(
                "MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)",
                mapPointGeo.latitude,
                mapPointGeo.longitude,
                accuracyInMeters
            )
        )
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude)
        //이 좌표로 지도 중심 이동
        //이 좌표로 지도 중심 이동
        mapView!!.setMapCenterPoint(currentMapPoint, true)
        //전역변수로 현재 좌표 저장
        //전역변수로 현재 좌표 저장
        currentLat = mapPointGeo.latitude
        currentLng = mapPointGeo.longitude
        Log.d(TAG, "현재위치 => " + currentLat.toString() + "  " + currentLng)
        //loaderLayout.setVisibility(View.GONE)
        //트래킹 모드가 아닌 단순 현재위치 업데이트일 경우, 한번만 위치 업데이트하고 트래킹을 중단시키기 위한 로직
        //트래킹 모드가 아닌 단순 현재위치 업데이트일 경우, 한번만 위치 업데이트하고 트래킹을 중단시키기 위한 로직
        if (!isTrackingMode) {
            mapView!!.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        }
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {

    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
        Log.i(TAG, "onCurrentLocationUpdateCancelled")
    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
        Log.i(TAG, "onCurrentLocationUpdateFailed")
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(
        mapView: MapView?,
        mapPOIItem: MapPOIItem?,
        calloutBalloonButtonType: MapPOIItem.CalloutBalloonButtonType
    ) {
        val lat: Double = mapPOIItem!!.mapPoint.mapPointGeoCoord.latitude
        val lng: Double = mapPOIItem.mapPoint.mapPointGeoCoord.longitude
        //Toast.makeText(context, mapPOIItem.itemName, Toast.LENGTH_SHORT).show()
        val dialog = PlaceInfoDialog(mapPOIItem.userObject as PlaceInfo)
        dialog.show(fragmentManager!!, "placeInfo")
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

    }

    override fun onPOIItemSelected(mapView: MapView?, mapPOIItem: MapPOIItem?) {

    }

    override fun onClick(view: View?) {
        when(view!!.id) {
            R.id.fab_location -> {
                if (checkLocationPermission()) {
                    mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
                    if(currentMapPoint != null)
                        mapView!!.setMapCenterPoint(currentMapPoint, true)
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_choose_city -> {
                val dialog = ChooseCityDialog(cityFileList, cityFileIndex)
                dialog.setTargetFragment(this, 123)
                dialog.show(fragmentManager!!.beginTransaction(), "chooseCity")
                Log.d(TAG, "selected action_choose_city")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onCityDialogResult(confirmed : Boolean, newIndex : Int) {
        if (confirmed && newIndex != cityFileIndex) {
            cityFileIndex = newIndex
            mapView.removeAllPOIItems()
            val toiletPOIs = getPOIItems("csv/${cityFileList[cityFileIndex]}")
            mapView.addPOIItems(toiletPOIs.toTypedArray())
        }
    }

    private fun checkLocationPermission() : Boolean {
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_PERMISSION
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
                    Log.d(TAG, "permission granted!")
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "permission not granted")
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
                Log.d(TAG, "some other request")
            }
        }
    }
}
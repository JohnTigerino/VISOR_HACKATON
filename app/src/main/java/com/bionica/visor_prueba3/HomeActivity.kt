package com.bionica.visor_prueba3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        createFragment()
    }

    private fun createFragment(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { this }
    }
    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        createMarker()

        /*googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isZoomGesturesEnabled = true
        googleMap.uiSettings.isRotateGesturesEnabled = true
        googleMap.uiSettings.isScrollGesturesEnabled = true
        googleMap.uiSettings.isTiltGesturesEnabled = true

        // Tipo de mapa opcional
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Marcador inicial
        val leon = LatLng(12.4356, -86.8794)
        googleMap.addMarker(MarkerOptions().position(leon).title("León, Nicaragua"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(leon, 12f))

        // Mi ubicación (requiere permiso fine/coarse location concedido)
        // if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
        //     googleMap.isMyLocationEnabled = true
        // }*/
    }
    private fun createMarker() {
        val coordenadas = LatLng(12.420324887210588, -86.8717269759507)
        val marker = MarkerOptions().position(coordenadas).title("León")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordenadas, 18f),
            4000,
            null
        )
    }

    /*override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onStart() { super.onStart(); mapView.onStart() }
    override fun onStop() { super.onStop(); mapView.onStop() }
    override fun onPause() { mapView.onPause(); super.onPause() }
    override fun onDestroy() { mapView.onDestroy(); super.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }*/
}
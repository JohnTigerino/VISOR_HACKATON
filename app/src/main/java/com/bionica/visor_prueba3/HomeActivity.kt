package com.bionica.visor_prueba3

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    // --- UI / Toolbar ---
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvTitle: TextView

    // --- Map ---
    private lateinit var map: GoogleMap
    private var selectedMarker: Marker? = null

    // Search
    private lateinit var searchMenuItem: MenuItem
    private lateinit var searchView: SearchView

    // Fullscreen control
    private var isMapFullscreen = false
    private var defaultMapHeightPx: Int = 0

    // Area selection
    private var isSelectingArea = false
    private val selectionPoints = mutableListOf<LatLng>()
    private var selectionPolygon: com.google.android.gms.maps.model.Polygon? = null

    // Permissions
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
            val fine = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarse = grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (fine || coarse) enableMyLocation()
            else Snackbar.make(findViewById(R.id.map), "Permiso de ubicación denegado", Snackbar.LENGTH_LONG).show()
        }

    // ---------------------- Lifecycle ----------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvTitle = findViewById(R.id.tvTitle)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setOnMenuItemClickListener { onToolbarItemSelected(it) }

        // Botón "Dirección": abre SearchView
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnDireccion)
            .setOnClickListener {
                if (::searchMenuItem.isInitialized && ::searchView.isInitialized) {
                    searchMenuItem.expandActionView()
                    searchView.requestFocus()
                } else {
                    Snackbar.make(findViewById(R.id.map), "Cargando buscador…", Snackbar.LENGTH_SHORT).show()
                }
            }

        // Botón "Subir imagen" (placeholder)
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSubirImagen)
            .setOnClickListener {
                Snackbar.make(findViewById(R.id.map), "Subir imagen (pendiente)", Snackbar.LENGTH_SHORT).show()
            }

        // Map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Guardar altura inicial del card del mapa (para restaurar al salir de fullscreen)
        val cardMap = findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardMap)
        cardMap.post { defaultMapHeightPx = cardMap.height }

        // Cajita de botones (sobre el mapa)
        findViewById<ImageButton>(R.id.btnFullscreen).setOnClickListener { toggleFullscreenMap() }

        findViewById<ImageButton>(R.id.btnSelect).setOnClickListener {
            isSelectingArea = !isSelectingArea
            if (isSelectingArea) {
                Snackbar.make(findViewById(R.id.map), "Modo selección: toca en el mapa para marcar puntos", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(findViewById(R.id.map), "Modo selección desactivado", Snackbar.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageButton>(R.id.btnConfirm).setOnClickListener { confirmAreaSelection() }
        findViewById<ImageButton>(R.id.btnUndo).setOnClickListener { undoLastPoint() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Menú del toolbar (incluye la SearchView)
        menuInflater.inflate(R.menu.menu_home, menu)
        searchMenuItem = menu.findItem(R.id.busqueda)
        searchView = searchMenuItem.actionView as SearchView
        searchView.queryHint = "Buscar dirección o lugar"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { geocodeAndMove(it, alsoUpdateTitle = true) }
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?) = false
        })
        return true
    }

    private fun onToolbarItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.lista -> {
                Snackbar.make(findViewById(R.id.map), "Lista (pendiente)", Snackbar.LENGTH_SHORT).show()
                true
            }
            R.id.alerta -> {
                Snackbar.make(findViewById(R.id.map), "No hay alertas por el momento", Snackbar.LENGTH_LONG).show()
                true
            }
            R.id.infoUser -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Usuario")
                    .setMessage("Nombre: John\nCorreo: john@gmail.com\nRol: Estudiante\nEstado: Activo")
                    .setPositiveButton("Cerrar", null)
                    .show()
                true
            }
            else -> false
        }
    }

    // ---------------------- Map ----------------------

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false
        map.mapType = GoogleMap.MAP_TYPE_SATELLITE // o HYBRID si querés labels

        // Posición inicial: León
        val leon = LatLng(12.4356, -86.8794)
        selectedMarker = map.addMarker(MarkerOptions().position(leon).title("León, Nicaragua"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(leon, 14f))
        updateTitleFromLatLng(leon)

        // Tap del mapa: según modo
        map.setOnMapClickListener { latLng ->
            if (isSelectingArea) {
                // Agregar vértice al polígono
                selectionPoints.add(latLng)
                updateSelectionPolygon()
            } else {
                // Mover/crear marcador y actualizar dirección
                if (selectedMarker == null) {
                    selectedMarker = map.addMarker(MarkerOptions().position(latLng))
                } else {
                    selectedMarker!!.position = latLng
                }
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                updateTitleFromLatLng(latLng)
            }
        }

        enableMyLocation()
    }

    // ---------------------- Search / Geocoder ----------------------

    /** Buscar por texto y centrar (opcionalmente actualizar título) */
    private fun geocodeAndMove(query: String, alsoUpdateTitle: Boolean) {
        lifecycleScope.launch {
            val geocoder = Geocoder(this@HomeActivity, Locale.getDefault())
            val result = withContext(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= 33) geocoder.getFromLocationName(query, 1)
                else @Suppress("DEPRECATION") geocoder.getFromLocationName(query, 1)
            }
            val addr = result?.firstOrNull()
            if (addr != null) {
                val target = LatLng(addr.latitude, addr.longitude)
                if (selectedMarker == null) {
                    selectedMarker = map.addMarker(MarkerOptions().position(target))
                } else {
                    selectedMarker!!.position = target
                }
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 16f))
                if (alsoUpdateTitle) tvTitle.text = formatAddress(addr, target)
            } else {
                Snackbar.make(findViewById(R.id.map), "Sin resultados para “$query”", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /** Reverse geocoding y actualización del título */
    private fun updateTitleFromLatLng(latLng: LatLng) {
        lifecycleScope.launch {
            val geocoder = Geocoder(this@HomeActivity, Locale.getDefault())
            val addr = withContext(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= 33) geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                else @Suppress("DEPRECATION") geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            }?.firstOrNull()
            tvTitle.text = formatAddress(addr, latLng)
        }
    }

    /** Formatea dirección o fallback a lat/lng */
    private fun formatAddress(addr: Address?, latLng: LatLng): String {
        return when {
            addr == null -> "Mi terreno (${latLng.latitude.format(5)}, ${latLng.longitude.format(5)})"
            !addr.featureName.isNullOrBlank() && !addr.thoroughfare.isNullOrBlank() ->
                "${addr.featureName}, ${addr.thoroughfare}"
            !addr.getAddressLine(0).isNullOrBlank() ->
                addr.getAddressLine(0)
            else -> "Mi terreno (${latLng.latitude.format(5)}, ${latLng.longitude.format(5)})"
        }
    }

    // ---------------------- Pantalla COmpleta ----------------------

    private fun toggleFullscreenMap() {
        val cardMap = findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardMap)
        val title = findViewById<TextView>(R.id.tvTitle)
        val btnDir = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnDireccion)
        val btnImg = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSubirImagen)
        val tvHelp = findViewById<TextView>(R.id.tvHelp)

        isMapFullscreen = !isMapFullscreen

        if (isMapFullscreen) {
            // Ocultar UI superior/inferior (se mantiene visible la cajita de controles)
            title.visibility = View.GONE
            btnDir.visibility = View.GONE
            btnImg.visibility = View.GONE
            tvHelp.visibility = View.GONE

            val lp = cardMap.layoutParams
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT
            cardMap.layoutParams = lp
        } else {
            // Restaurar UI
            title.visibility = View.VISIBLE
            btnDir.visibility = View.VISIBLE
            btnImg.visibility = View.VISIBLE
            tvHelp.visibility = View.VISIBLE

            val lp = cardMap.layoutParams
            lp.height = if (defaultMapHeightPx > 0) defaultMapHeightPx
            else (400 * resources.displayMetrics.density).toInt()
            cardMap.layoutParams = lp
        }

        // (Opcional) cambiar icono de fullscreen según estado:
        // val btnFs = findViewById<ImageButton>(R.id.btnFullscreen)
        // btnFs.setImageResource(if (isMapFullscreen) R.drawable.ic_fullscreen_exit_24 else R.drawable.ic_fullscreen_24)
    }


    private fun updateSelectionPolygon() {
        if (selectionPoints.size < 2) {
            selectionPolygon?.remove()
            selectionPolygon = null
            return
        }
        val opts = com.google.android.gms.maps.model.PolygonOptions()
            .addAll(selectionPoints)
            .strokeColor(0xFF1976D2.toInt())   // azul
            .strokeWidth(5f)
            .fillColor(0x401976D2)             // azul translúcido
        selectionPolygon?.remove()
        selectionPolygon = map.addPolygon(opts)
    }

    private fun undoLastPoint() {
        if (selectionPoints.isNotEmpty()) {
            selectionPoints.removeAt(selectionPoints.lastIndex)
            updateSelectionPolygon()
            Snackbar.make(findViewById(R.id.map), "Último punto eliminado", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(findViewById(R.id.map), "No hay puntos para eliminar", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun confirmAreaSelection() {
        if (selectionPoints.size < 3) {
            Snackbar.make(findViewById(R.id.map), "Necesitás al menos 3 puntos", Snackbar.LENGTH_LONG).show()
            return
        }
        isSelectingArea = false
        Snackbar.make(findViewById(R.id.map), "Área seleccionada", Snackbar.LENGTH_LONG).show()

    }

    private fun enableMyLocation() {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fine || coarse) {
            map.isMyLocationEnabled = true
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}

// Utilidad para formatear doubles
private fun Double.format(digits: Int) = "%.${digits}f".format(this)

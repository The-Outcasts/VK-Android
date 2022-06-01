package com.theoutcasts.app.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import org.osmdroid.api.IMapController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.theoutcasts.app.R
import com.theoutcasts.app.databinding.ActivityMainBinding
import com.theoutcasts.app.location.LocationProviderChangedReceiver
import com.theoutcasts.app.location.MyEventLocationSettingsChange
import com.theoutcasts.app.location.PublicationOverlay
import com.theoutcasts.app.ui.createpublication.CreatePublicationActivity
import com.theoutcasts.app.ui.eventpublication.EventPublicationActivity
import com.theoutcasts.app.ui.map.model.EventUi
import com.theoutcasts.app.ui.map.vm.MapViewModel
import com.theoutcasts.app.ui.map.vm.MapViewModelFactory
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import timber.log.Timber
import org.osmdroid.views.overlay.Marker

/*
*
*  +---------------------------------- MESS ----------------------------------+
*                        Move setup, logic into ViewModel
*  +--------------------------------------------------------------------------+
*                        WARNING: Здесь всё очень костыльно
*/

class MainActivity : AppCompatActivity() {
    private lateinit var mMapViewModel: MapViewModel

    private var activityResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient //https://developer.android.com/training/location/retrieve-current
    private var lastLocation: Location? = null
    private var locationCallback: LocationCallback
    private var locationRequest: LocationRequest
    private var requestingLocationUpdates = false

    private lateinit var binding: ActivityMainBinding
    private lateinit var map: MapView
    private var startPoint: GeoPoint = GeoPoint(55.772932, 37.698825) //Москва
    private lateinit var mapController: IMapController
    private lateinit var currentPositionMarker: Marker

    private val zoomNumber = 15 //  Initial zoom

    init {
        locationRequest = LocationRequest.create()
            .apply { //https://stackoverflow.com/questions/66489605/is-constructor-locationrequest-deprecated-in-google-maps-v2
                interval = 1000 //can be much higher
                fastestInterval = 500
                smallestDisplacement = 10f //10m
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                maxWaitTime = 1000
            }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateLocation(location)
                }
            }
        }

        this.activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            var allAreGranted = true
            for (b in result.values) {
                allAreGranted = allAreGranted && b
            }

            Timber.d("Permissions granted $allAreGranted")
            if (allAreGranted) {
                initCheckLocationSettings()
                //initMap() if settings are ok
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance()
            .load(applicationContext, this.getPreferences(Context.MODE_PRIVATE))
        binding = ActivityMainBinding.inflate(layoutInflater) //ADD THIS LINE

        // Move to stateholder
        val br: BroadcastReceiver = LocationProviderChangedReceiver()
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(br, filter)


        mMapViewModel = ViewModelProvider(this, MapViewModelFactory())[MapViewModel::class.java]


        mMapViewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        mMapViewModel.events.observe(this) {
            drawEventMarkers()
        }

        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        mapController = map.controller
        setContentView(binding.root)
        val appPerms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
        )

        activityResultLauncher.launch(appPerms)

        binding.addPublicationButton.setOnClickListener {
            mMapViewModel.mapShouldBeUpdated = true
            val intent = Intent(this, CreatePublicationActivity::class.java)
            intent.putExtra("latitude", startPoint.latitude)
            intent.putExtra("longitude", startPoint.longitude)
            startActivity(intent)
        }

        mMapViewModel.loadEventsIfNotLoaded()
    }

    fun updateLocation(newLocation: Location) {
        lastLocation = newLocation

        startPoint.longitude = newLocation.longitude
        startPoint.latitude = newLocation.latitude
        mapController.setCenter(startPoint)

        currentPositionMarker = Marker(map)
        currentPositionMarker.position = startPoint

        drawEventMarkers()
        map.invalidate()
    }

    private fun drawEventMarkers() {

            mMapViewModel.events.value?.let {
                map.overlays.removeAll(map.overlays)
                map.overlays.add(currentPositionMarker)

                for (event in it) {
                    try {
                        drawEventMarker(event)
                        putClickableEventMarker(event)
                    } catch (e: IllegalArgumentException) {
                        val tmp = event.domain.longitude
                        event.domain.longitude = event.domain.latitude
                        event.domain.latitude = tmp
                        drawEventMarker(event)
                        putClickableEventMarker(event)
                    }
                }
            }
            map.invalidate()
    }

    private fun drawEventMarker(eventUi: EventUi) {
        val notLoadedPictureBitmap = ContextCompat.getDrawable(this, R.drawable.logo_black)!!.toBitmap()

        val publication = PublicationOverlay()

        publication.setEventId(eventUi.domain.id!!)
        publication.setPosition(GeoPoint(eventUi.domain.latitude!!, eventUi.domain.longitude!!))
        publication.setIcon(ContextCompat.getDrawable(this, R.drawable.icon)!!.toBitmap())

        if (eventUi.pictureBitmap == null) {
            publication.setImage(notLoadedPictureBitmap)
        } else {
            publication.setImage(eventUi.pictureBitmap!!)
        }

       map.overlayManager.add(publication)
    }

    private fun putClickableEventMarker(eventUi: EventUi) {
        val marker = EventMarker(map, eventUi.domain.id!!)
        marker.position = GeoPoint(eventUi.domain.latitude!!, eventUi.domain.longitude!!) // TODO
        marker.setVisible(false)

        marker.setOnMarkerClickListener { marker, _ ->
            val intent = Intent(this, EventPublicationActivity::class.java)
            intent.putExtra("EVENT_ID", eventUi.domain.id!!)
            startActivity(intent)
            true
        }

        map.overlays.add(marker)
    }

    private fun initCheckLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {it
            Timber.d("Settings Location IS OK")
            MyEventLocationSettingsChange.globalState = true //default
            initMap()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                Timber.d("Settings Location addOnFailureListener call settings")
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this@MainActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Timber.d("Settings Location sendEx??")
                }
            }
        }

    }

    @SuppressLint("MissingPermission") //permission are checked before
    fun readLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    updateLocation(it) }
            }
    }

    private fun initLocation() { //call in create
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        readLastKnownLocation()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() { //onResume
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() { //onPause
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun initMap() {
        initLocation()
        if (!requestingLocationUpdates) {
            requestingLocationUpdates = true
            startLocationUpdates()
        }
        mapController.setZoom(zoomNumber)
        mapController.setCenter(startPoint)
        map.invalidate()

    }

    override fun onResume() {
        super.onResume()
        initLocation()
        startLocationUpdates()

        if (mMapViewModel.mapShouldBeUpdated) {
            mMapViewModel.mapShouldBeUpdated = false
            mMapViewModel.reloadAllEvents()
            drawEventMarkers()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }


    companion object {
        const val REQUEST_CHECK_SETTINGS = 20202
    }
}

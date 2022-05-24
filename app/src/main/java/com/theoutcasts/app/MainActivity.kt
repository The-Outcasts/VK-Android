package com.theoutcasts.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.CalendarContract
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.osmdroid.api.IMapController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.theoutcasts.app.data.repository.firebase.EventRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.ImageRepositoryImpl
import com.theoutcasts.app.databinding.ActivityMainBinding
import com.theoutcasts.app.domain.interactor.EventInteractor
import com.theoutcasts.app.domain.interactor.InvalidLoginOrPasswordException
import com.theoutcasts.app.domain.model.Event
import com.theoutcasts.app.domain.repository.EventRepository
import com.theoutcasts.app.location.LocationProviderChangedReceiver
import com.theoutcasts.app.location.MyEventLocationSettingsChange
import com.theoutcasts.app.location.PublicationOverlay
import com.theoutcasts.app.location.TestActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import timber.log.Timber
import java.io.*
import java.util.*
import kotlinx.coroutines.*
import org.osmdroid.views.overlay.Marker

class MainActivity : AppCompatActivity() {

    private var activityResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient //https://developer.android.com/training/location/retrieve-current
    private var lastLocation: Location? = null
    private var locationCallback: LocationCallback
    private var locationRequest: LocationRequest
    private var requestingLocationUpdates = false
    private var events: List<Event> = listOfNotNull()
    private var userImages: List<Bitmap> = emptyList()
    companion object {
        const val REQUEST_CHECK_SETTINGS = 20202
    }

    private val zoomNumber = 15 //  Initial zoom

    init {
        readBD()
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
                    // Update UI with location data
                    updateLocation(location) //MY function
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

    private lateinit var binding: ActivityMainBinding
    private lateinit var map: MapView
    private var startPoint: GeoPoint = GeoPoint(55.772932, 37.698825) //Москва
    private lateinit var mapController: IMapController
    private lateinit var currentPositionMarker: Marker
//    lateinit var pPositions:Array<GeoPoint>     //Test
//    private val p1:GeoPoint = GeoPoint(55.772932, 37.698825)    //Test
//    private val p2:GeoPoint = GeoPoint(52.772932, 37.698825)    //Test
//    private val point: Array<GeoPoint> = arrayOf(p1,p2)                           //Test

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree()) //Init report type
        }
        val br: BroadcastReceiver = LocationProviderChangedReceiver()
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(br, filter)

        //LocalBroadcastManager.getInstance(this).registerReceiver(locationProviderChange)
        Configuration.getInstance()
            .load(applicationContext, this.getPreferences(Context.MODE_PRIVATE))
        binding = ActivityMainBinding.inflate(layoutInflater) //ADD THIS LINE

//        pPositions = point  //Тест

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

    }

    fun updateLocation(newLocation: Location) {
        lastLocation = newLocation
        //GUI, MAP TODO
        //var currentPoint: GeoPoint = GeoPoint(newLocation.latitude, newLocation.longitude);
        startPoint.longitude = newLocation.longitude
        startPoint.latitude = newLocation.latitude
        mapController.setCenter(startPoint)

        currentPositionMarker = Marker(map)
        currentPositionMarker.position = startPoint
        map.overlayManager.add(currentPositionMarker)
        drawPublicationOverlays(events)
        map.invalidate()
    }

    private fun drawPublicationOverlays(events: List<Event>)    {
        for (n in events.indices) {       //Цикл отрисовки оверлеев

            getPositionOverlay(n)
        }
        map.invalidate()
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
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
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

    private fun getPositionOverlay(n: Int) { //Функция отрисовка оверлеев

        val publication = PublicationOverlay(this, this, intent.getStringExtra(SignUpActivity.MESSAGE_USERID))
        publication.setIcon(ContextCompat.getDrawable(this,R.drawable.icon)!!.toBitmap())
        publication.setImage(userImages[n])
        events[n].id?.let { publication.setEventId(it) }
        events[n].longitude?.let {longitude ->
            events[n].latitude?.let { latitude ->
            GeoPoint(
                longitude.toDouble(),
                latitude.toDouble())
        } }
            ?.let { publication.setPosition(it) }
        map.overlayManager.add(publication)
    }

    // Чтение Базы данных
    private fun readBD()  {
        lateinit var image: Bitmap
        GlobalScope.launch(Dispatchers.IO) {
            val eventsResult = EventInteractor(eventRepository = EventRepositoryImpl())
            eventsResult.getAll(100).fold(
                onSuccess = {
                    events = it

                    for (idx in events.indices) {
                        if (events[idx].pictureURL != null) {
                            ImageRepositoryImpl().downloadImage(events[idx].pictureURL!!).fold(
                                onSuccess = { bitmap ->
                                    userImages += bitmap
                                }, onFailure = { error ->
                                    val errorMessage = "Ошибка базы медиаданных: $error"
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                })
                        }
                    }

                },
                onFailure = {
                    val errorMessage = "Ошибка базы данных: $it"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, errorMessage,Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }
    }

    fun watchPublication(view: View)    {
        val intent = Intent(this, PublicationActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        initLocation()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}



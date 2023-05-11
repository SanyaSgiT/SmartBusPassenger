package com.example.smartbuspassenger

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbuspassenger.ui.bottombar.InfoActivity
import com.example.smartbuspassenger.ui.bottombar.UserActivity
import com.example.smartbuspassenger.ui.routeList.RouteAdapter
import com.example.smartbuspassenger.ui.routeList.RouteListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.traffic.TrafficColor
import com.yandex.mapkit.traffic.TrafficLayer
import com.yandex.mapkit.traffic.TrafficLevel
import com.yandex.mapkit.traffic.TrafficListener
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationView
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.app.Activity
import android.graphics.Color
import android.graphics.PointF
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.runtime.image.ImageProvider
import java.util.*

class MapActivity : AppCompatActivity(), TrafficListener, UserLocationObjectListener {

    private var mapview: MapView? = null
    private var levelText: TextView? = null
    private var levelIcon: ImageButton? = null
    private lateinit var recyclerGetter: ImageButton
    private lateinit var recyclerView: RecyclerView
    private var trafficLevel: TrafficLevel? = null
    private var trafficFreshness: TrafficFreshness? = null
    private var traffic: TrafficLayer? = null
    private lateinit var searchEditText: EditText
    private lateinit var bottomBar: BottomNavigationView

    var tvStatusGPS: String? = null
    var tvLocationGPS: String? = null
    private var locationManager: LocationManager? = null
    var sbGPS = StringBuilder()

    private enum class TrafficFreshness {
        Loading, OK, Expired
    }

    private var userLocationLayer: UserLocationLayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("6af80549-c660-4137-8516-d039ad43dc6e")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_map)

        mapview = findViewById(R.id.mapview)
        mapview!!.map.move(
            CameraPosition(Point(55.0415, 82.9346), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0.0f), null
        )

        levelText = findViewById(R.id.traffic_light_text)
        levelIcon = findViewById(R.id.traffic_light)
        traffic = MapKitFactory.getInstance().createTrafficLayer(mapview!!.mapWindow)
        traffic!!.isTrafficVisible = true
        traffic!!.addTrafficListener(this)
        updateLevel()

        setupBindings()
        setupUi()
        setupSubscriptions()


        requestLocationPermission()
        val mapKit = MapKitFactory.getInstance()
        mapKit.resetLocationManagerToDefault()
        userLocationLayer = mapKit.createUserLocationLayer(mapview!!.getMapWindow())
        userLocationLayer!!.isVisible = true
        userLocationLayer!!.isHeadingEnabled = true
        userLocationLayer!!.setObjectListener(this)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    }

    override fun onStop() {
        mapview?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview?.onStart()
    }

    private fun updateLevel() {
        val iconId: Int
        var level: String? = ""
        if (!traffic!!.isTrafficVisible) {
            iconId = R.drawable.rec_grey
        } else if (trafficFreshness == TrafficFreshness.Loading) {
            iconId = R.drawable.rec_violet
        } else if (trafficFreshness == TrafficFreshness.Expired) {
            iconId = R.drawable.rec_blue
        } else if (trafficLevel == null) {  // state is fresh but region has no data
            iconId = R.drawable.rec_grey
        } else {
            when (trafficLevel!!.color) {
                TrafficColor.RED -> iconId = R.drawable.rec_red
                TrafficColor.GREEN -> iconId = R.drawable.rec_green
                TrafficColor.YELLOW -> iconId = R.drawable.rec_yellow
                else -> iconId = R.drawable.rec_grey
            }
            level = trafficLevel!!.level.toString()
        }
        levelIcon!!.setImageBitmap(BitmapFactory.decodeResource(resources, iconId))
        levelText!!.text = level
    }

    fun onLightClick(view: View?) {
        traffic!!.isTrafficVisible = !traffic!!.isTrafficVisible
        updateLevel()
    }

    override fun onTrafficChanged(trafficLevel: TrafficLevel?) {
        this.trafficLevel = trafficLevel
        trafficFreshness = TrafficFreshness.OK
        updateLevel()
    }

    override fun onTrafficLoading() {
        trafficLevel = null
        trafficFreshness = TrafficFreshness.Loading
        updateLevel()
    }

    override fun onTrafficExpired() {
        trafficLevel = null
        trafficFreshness = TrafficFreshness.Expired
        updateLevel()
    }


    private val vm: RouteListViewModel by viewModel()
    private lateinit var adapter: RouteAdapter

    private fun setupBindings() {
        bottomBar = findViewById(R.id.bottomNavigationView)
        searchEditText = findViewById(R.id.searchEditText)
        recyclerView = findViewById(R.id.recycler)
        recyclerGetter = findViewById(R.id.recyclerGetter)
    }

    private fun setupUi() {
        bottomBar.selectedItemId = R.id.menu_map
        bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, UserActivity::class.java))
                    finish()
                    true
                }
                R.id.menu_info -> {
                    startActivity(Intent(this, InfoActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
        var recyclerVisible: Boolean = false
        recyclerView.isVisible = false
        recyclerGetter.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.arrow_up))

        recyclerGetter.setOnClickListener {
            if (!recyclerVisible) {
                recyclerGetter.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.arrow_down))
                recyclerView.isVisible = true
                recyclerVisible = true
            } else if (recyclerVisible) {
                recyclerGetter.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.arrow_up))
                recyclerView.isVisible = false
                recyclerVisible = false
            }
        }
        searchEditText.addTextChangedListener {
            it?.let {
                vm.search(Integer.parseInt(it.toString()))
            }
        }
        setupRecycler()
    }

    private fun setupSubscriptions() {
        vm.routes.observe(this) { list ->
            adapter.updateList(list)
        }
    }

    private fun setupRecycler() {
        adapter = RouteAdapter {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Выбран маршрут")
            builder.setMessage("Маршрут номер" + it.id.toString())

            builder.setPositiveButton("OK") { dialog, which ->
                Toast.makeText(applicationContext,
                    "YES", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(applicationContext,
                    "NO", Toast.LENGTH_SHORT).show()
            }

            builder.setNeutralButton("Maybe") { dialog, which ->
                Toast.makeText(applicationContext,
                    "Maybe", Toast.LENGTH_SHORT).show()
            }
            builder.show()
            vm.drawRoute(it.id)
        }
        recyclerView.adapter = adapter
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                "android.permission.ACCESS_FINE_LOCATION"
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf("android.permission.ACCESS_FINE_LOCATION"),
                PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationLayer!!.setAnchor(
            PointF((mapview!!.width * 0.5).toFloat(), (mapview!!.height * 0.5).toFloat()),
            PointF((mapview!!.width * 0.5).toFloat(), (mapview!!.height * 0.83).toFloat())
        )
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                this, R.drawable.user_arrow
            )
        )
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(view: UserLocationView) {}
    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}
    companion object {
        const val PERMISSIONS_REQUEST_FINE_LOCATION = 1
    }






    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            (
                    1000 * 10).toLong(), 10f, locationListener
        )
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, (1000 * 10).toLong(), 10f,
            locationListener
        )
    }

    override fun onPause() {
        super.onPause()
        locationManager!!.removeUpdates(locationListener)
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            showLocation(location)
            println(tvLocationGPS)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            if (provider == LocationManager.GPS_PROVIDER) {
                tvStatusGPS = "Status: $status"
            }
        }
    }

    private fun showLocation(location: Location?) {
        if (location == null) return
        if (location.provider == LocationManager.GPS_PROVIDER) {
            tvLocationGPS = formatLocation(location)
            println(tvLocationGPS)
        }
    }

    private fun formatLocation(location: Location?): String {
        return if (location == null) "" else String.format(
            "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3\$tF %3\$tT",
            location.latitude, location.longitude, Date(
                location.time
            )
        )
    }
}

package com.example.smartbuspassenger

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbuspassenger.data.models.MarkersResponse
import com.example.smartbuspassenger.domain.Route
import com.example.smartbuspassenger.domain.Trace
import com.example.smartbuspassenger.domain.Transport
import com.example.smartbuspassenger.domain.TransportType
import com.example.smartbuspassenger.ui.PayActivity
import com.example.smartbuspassenger.ui.bottombar.InfoActivity
import com.example.smartbuspassenger.ui.bottombar.UserActivity
import com.example.smartbuspassenger.ui.routeList.RouteAdapter
import com.example.smartbuspassenger.ui.routeList.RouteListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.traffic.TrafficColor
import com.yandex.mapkit.traffic.TrafficLayer
import com.yandex.mapkit.traffic.TrafficLevel
import com.yandex.mapkit.traffic.TrafficListener
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.pow
import com.example.smartbuspassenger.listPoint_bus_8

class MapActivity : AppCompatActivity(), TrafficListener, UserLocationObjectListener {

    private val vm: RouteListViewModel by viewModel()
    private lateinit var adapter: RouteAdapter

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

    private lateinit var mapObjects: MapObjectCollection

    private var userLocationLayer: UserLocationLayer? = null

    private var myLatitude: Double = 0.0
    private var myLongitude: Double = 0.0
    private var _myLatitude: Double = 0.0
    private var _myLongitude: Double = 0.0
    private lateinit var listRouteToIdentification: List<Route>

    var count = 0
    var countPoints = 0
    var p = 0

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            showLocation(location)
            myLatitude = location.latitude
            myLongitude = location.longitude
            println(tvLocationGPS)
            println("Широта $myLatitude")
            println("Долгота $myLongitude")
//            identificationRoute(listPoint_bus_8, myLatitude, myLongitude)

            var route = ""
            println(p)
//            outerLoop@for(p in listPoint_bus_8.indices){
                if ((myLatitude - listPoint_bus_8[p].latitude).pow(2.0)+(myLongitude - listPoint_bus_8[p].longitude).pow(2.0)<=(0.015).pow(2.0)){
                    count++
                    println("Найдена точка соприкосновения")
                    if (count == 10){
                        route = testData("bus_8")
                        println("Вы движетесь по маршруту $route")
                        Toast.makeText(this@MapActivity, "Движение по маршруту $route", Toast.LENGTH_LONG).show()
                        //break@outerLoop
                        val builder = AlertDialog.Builder(this@MapActivity)
                        builder.setTitle("ОПЛАТА")
                        builder.setMessage("Это замена активити оплаты")

                        builder.setPositiveButton("OK") { dialog, which ->
                            Toast.makeText(
                                applicationContext,
                                "OK", Toast.LENGTH_SHORT
                            ).show()
                        }
                        builder.show()
                    }
                }else{
                    println("Точка не прошла проверку")
                }
            p = countPoints++
//            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            if (provider == LocationManager.GPS_PROVIDER) {
                tvStatusGPS = "Status: $status"
            }
        }
    }
    private fun identificationRoute(driver:List<Point>, myLatitude: Double, myLongitude: Double){
        var flag = false
        var route = ""
        outerLoop@for(p in driver.indices){
//            val value = (myLatitude - driver[p].latitude).pow(2.0)
//            val value2 = (myLongitude - driver[p].longitude).pow(2.0)
//            println("Широта $myLatitude Долгота $myLongitude")
            if ((myLatitude - driver[p].latitude).pow(2.0)+(myLongitude - driver[p].longitude).pow(2.0)<=(0.001).pow(2.0)){
                flag = true
                count++
                println("Найдена точка соприкосновения")
                if (count > 10){
                    route = testData("bus_8")
                    println("Вы движетесь по маршруту $route")
                    Toast.makeText(this@MapActivity, "Движение по маршруту $route", Toast.LENGTH_LONG).show()
                    break@outerLoop
                }
            }else{
                println("Точка не прошла проверку")
            }
        }
//        lifecycleScope.launch {
//            withContext(Dispatchers.Main) {
//                val intent = Intent(this@MapActivity, PayActivity::class.java)
//                    .putExtra("Route", route)
//                startActivity(intent)
//            }
//        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("")
        builder.setMessage("Маршрут номер")

        builder.setPositiveButton("OK") { dialog, which ->
            Toast.makeText(
                applicationContext,
                "OK", Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
    }

    private fun testData(name: String): String{
        if(name == "bus_8"){
            return "Автобус 8"
        }else if(name == "tram_18"){
            return "Трамвай 18"
        }else if(name == "bus_1"){
            return "Автобус 1"
        }else if(name == "bus_18"){
            return "Автобус 18"
        }else if(name == "tram_3"){
            return "Трамвай 3"
        }else if(name == "minibus_9"){
            return "Маршрутка 9"
        }else if(name == "minibus_72"){
            return "Маршрутка 72"
        }else if(name == "troll_4"){
            return "Троллейбус 4"
        }else if(name == "troll_13"){
            return "Троллейбус 13"
        }else if(name == "troll_10"){
            return "Троллейбус 10"
        }
        return ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapview = findViewById(R.id.mapview)
        mapview!!.map.move(
            CameraPosition(Point(55.0415, 82.9346), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0.0f), null
        )

        mapObjects = mapview!!.map.mapObjects.addCollection()

        levelText = findViewById(R.id.traffic_light_text)
        levelIcon = findViewById(R.id.traffic_light)
        traffic = MapKitFactory.getInstance().createTrafficLayer(mapview!!.mapWindow)
        traffic!!.isTrafficVisible = true
        traffic!!.addTrafficListener(this)
        updateLevel()

        setupBindings()
        setupUi()
        setupSubscriptions()

        val mapKit = MapKitFactory.getInstance()
        mapKit.resetLocationManagerToDefault()
        userLocationLayer = mapKit.createUserLocationLayer(mapview!!.mapWindow)
        userLocationLayer!!.isVisible = true
        userLocationLayer!!.isHeadingEnabled = true
        userLocationLayer!!.setObjectListener(this)
        userLocationLayer!!.isAutoZoomEnabled = false

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
            iconId = when (trafficLevel!!.color) {
                TrafficColor.RED -> R.drawable.rec_red
                TrafficColor.GREEN -> R.drawable.rec_green
                TrafficColor.YELLOW -> R.drawable.rec_yellow
                else -> R.drawable.rec_grey
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

        recyclerView.isVisible = false

        recyclerGetter.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.arrow_up))
        recyclerGetter.setOnClickListener {
            if (recyclerView.isVisible) {
                recyclerGetter.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.arrow_up))
                recyclerView.isVisible = false
            } else {
                recyclerGetter.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.arrow_down))
                recyclerView.isVisible = true
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
            listRouteToIdentification = list
            lifecycleScope.launch {
                if(myLatitude != _myLatitude && myLongitude != _myLongitude){
                    myLatitude = _myLatitude
                    myLongitude = _myLongitude
                    Toast.makeText(this@MapActivity, "Старт идентификации", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this@MapActivity, "Пользователь не движется", Toast.LENGTH_LONG).show()
                }
                println("Выводим список полученных маршрутов")
                println(listRouteToIdentification)
            }
        }

        vm.traces.observe(this) { traces ->
            traces?.let {
                drawTrace(it)
            }
        }
    }

    var routeNum: String = ""

    private fun setupRecycler() {
        adapter = RouteAdapter {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Выбран маршрут")
            builder.setMessage("Маршрут номер" + it.id.toString())

            builder.setPositiveButton("OK") { dialog, which ->
                Toast.makeText(
                    applicationContext,
                    "OK", Toast.LENGTH_SHORT
                ).show()
            }

            builder.setNegativeButton("Отмена") { dialog, which ->
                Toast.makeText(
                    applicationContext,
                    "Отмена", Toast.LENGTH_SHORT
                ).show()
            }
            builder.show()
            vm.drawRoute(it.id)

            val tType: String = newTransportType(it.transportType)
            routeNum = routeNum + tType + " " + it.name
        }
        recyclerView.adapter = adapter
    }

    private fun newTransportType(type: TransportType): String {
        var tType = ""
        if (type == TransportType.BUS) {
            tType = "Автобус"
        } else if (type == TransportType.TROLLEY_BUS) {
            tType = "Троллейбус"
        } else if (type == TransportType.Minibus) {
            tType = "Маршрутка"
        } else if (type == TransportType.TRAM) {
            tType = "Трамвай"
        } else if (type == TransportType.MINIBUS) {
            tType = "Маршрутка"
        }
        return tType
    }

    private val json = Json { ignoreUnknownKeys = true }

    private fun drawTrace(traces: List<Trace>) {

        mapObjects.clear()

        val points = traces.map {
            Point(it.latLng.lat, it.latLng.lng)
        }
        println("Точки маршрута")
        println(points)

//        for(p in points.indices){
//            val value = (myLatitude - points[p].latitude).pow(2.0)
//            val value2 = (myLongitude - points[p].longitude).pow(2.0)
////            println("Широта $myLatitude Долгота $myLongitude")
//            if ((myLatitude - points[p].latitude).pow(2.0)+(myLongitude - points[p].longitude).pow(2.0)<=(0.15).pow(2.0)){
//                println("Движение по маршруту $routeNum")
////                Toast.makeText(this@MapActivity, "Движение по маршруту $routeNum", Toast.LENGTH_LONG).show()
//            }else{
//                println("Вы не на маршруте $routeNum")
//                println("Широта $value Долгота $value2")
////                Toast.makeText(this@MapActivity, "Вы не на маршруте $routeNum", Toast.LENGTH_LONG).show()
//            }
//        }

        val polyline: PolylineMapObject = mapObjects.addPolyline(Polyline(points))
        polyline.setStrokeColor(Color.BLUE)
        polyline.setZIndex(100.0f)

        for(i in traces.indices){
            if(traces[i].stop != null){
                val stop = mapObjects.addPlacemark(Point(traces[i].latLng.lat, traces[i].latLng.lng))
                stop.opacity = 0.5f
                stop.setIcon(ImageProvider.fromResource(this, R.drawable.stop))
                stop.isDraggable = true
            }
        }

        val client = HttpClient(CIO) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }

        lifecycleScope.launchWhenCreated {
            client.webSocket(method = HttpMethod.Get, host = "37.194.210.121", port = 4721, path = "/markers/json") {
                while (isActive){
                    val transport = receiveDeserialized<MarkersResponse>()
                    println(transport)
                    val mark = transport.markers
                    addBusOnMap(mark, routeNum)
                }
            }
        }

//        Toast.makeText(this@LoginActivity, "Нет такого пользователя", Toast.LENGTH_LONG).show()
//        Toast.makeText(this@LoginActivity, "Нет такого пользователя", Toast.LENGTH_LONG).show()
//        lifecycleScope.launch {
//            if(myLatitude != _myLatitude && myLongitude != _myLongitude){
//                myLatitude = _myLatitude
//                myLongitude = _myLongitude
//                Toast.makeText(this@MapActivity, "Старт идентификации", Toast.LENGTH_LONG).show()
//            }else{
//                Toast.makeText(this@MapActivity, "Пользователь не движется", Toast.LENGTH_LONG).show()
//            }
//            println("Выводим список полученных маршрутов")
//            println(listRouteToIdentification)
//        }
        
//        lifecycleScope.launch {
//            withContext(Dispatchers.Main) {
//                val intent = Intent(this@MapActivity, PayActivity::class.java)
//                    .putExtra("Route", routeNum)
//                startActivity(intent)
//            }
//        }
    }

    fun onMyLocationChange(location: Location) {

    }

    private fun addBusOnMap(mark: List<Transport>, routeNum:String){
        for(i in mark.indices){
            if(routeNum == mark[i].route){
                val bus = mapObjects.addPlacemark(Point(mark[i].latLng.lat, mark[i].latLng.lng))
                bus.opacity = 0.5f
                if(mark[i].transportType == TransportType.BUS){
                    bus.setIcon(ImageProvider.fromResource(this, R.drawable.bus))
                }else if(mark[i].transportType == TransportType.TROLLEY_BUS){
                    bus.setIcon(ImageProvider.fromResource(this, R.drawable.trollbus))
                }else if(mark[i].transportType == TransportType.MINIBUS || mark[i].transportType == TransportType.Minibus){
                    bus.setIcon(ImageProvider.fromResource(this, R.drawable.minibus))
                }else if(mark[i].transportType == TransportType.TRAM){
                    bus.setIcon(ImageProvider.fromResource(this, R.drawable.tram))
                }
                bus.isDraggable = true
            }
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                (1000 * 10).toLong(),
                10f,
                locationListener
            )
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                this, R.drawable.user_arrow
            )
        )
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(view: UserLocationView) {}
    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                500.toLong(),
                10f,
                locationListener
            )
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager!!.removeUpdates(locationListener)
    }

    private fun showLocation(location: Location) {
        if (location.provider == LocationManager.GPS_PROVIDER) {
            tvLocationGPS = formatLocation(location)
            println(tvLocationGPS)
        }
    }

    private fun formatLocation(location: Location?): String {
        return if (location == null){
            ""
        }else{
            myLatitude = location.latitude
            myLongitude = location.longitude
            String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3\$tF %3\$tT",
                location.latitude, location.longitude, Date(location.time)
            )
        }
//        return if (location == null) "" else String.format(
//            "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3\$tF %3\$tT",
//            location.latitude, location.longitude, Date(location.time)
//        )
    }

    private enum class TrafficFreshness {
        Loading, OK, Expired
    }

    companion object {
        const val PERMISSIONS_REQUEST_FINE_LOCATION = 1
    }
}

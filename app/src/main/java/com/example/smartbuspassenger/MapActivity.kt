package com.example.smartbuspassenger

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.traffic.TrafficColor
import com.yandex.mapkit.traffic.TrafficLayer
import com.yandex.mapkit.traffic.TrafficLevel
import com.yandex.mapkit.traffic.TrafficListener

class MapActivity : AppCompatActivity(), TrafficListener {

    private var mapview: MapView? = null
    private var levelText: TextView? = null
    private var levelIcon: ImageButton? = null
    private var trafficLevel: TrafficLevel? = null
    private var trafficFreshness: MapActivity.TrafficFreshness? = null
    private var traffic: TrafficLayer? = null

    private enum class TrafficFreshness {
        Loading, OK, Expired
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("6af80549-c660-4137-8516-d039ad43dc6e")
        MapKitFactory.initialize(this)
        setContentView(R.layout.map)

        mapview = findViewById(R.id.mapview)
        mapview!!.map.move(
            CameraPosition(Point(55.0415, 82.9346), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0.0f), null
        )

        levelText = findViewById<TextView>(R.id.traffic_light_text)
        levelIcon = findViewById<ImageButton>(R.id.traffic_light)
        traffic = MapKitFactory.getInstance().createTrafficLayer(mapview!!.getMapWindow())
        traffic!!.isTrafficVisible = true
        traffic!!.addTrafficListener(this)
        updateLevel()
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
        } else if (trafficFreshness == MapActivity.TrafficFreshness.Loading) {
            iconId = R.drawable.rec_violet
        } else if (trafficFreshness == MapActivity.TrafficFreshness.Expired) {
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
            level = Integer.toString(trafficLevel!!.level)
        }
        levelIcon!!.setImageBitmap(BitmapFactory.decodeResource(resources, iconId))
        levelText!!.text = level
        setupBindings()
        setupUi()
    }

    fun onLightClick(view: View?) {
        traffic!!.isTrafficVisible = !traffic!!.isTrafficVisible
        updateLevel()
    }

    fun onClickBack(view: View?) {
        finish()
    }

    override fun onTrafficChanged(trafficLevel: TrafficLevel?) {
        this.trafficLevel = trafficLevel
        trafficFreshness = MapActivity.TrafficFreshness.OK
        updateLevel()
    }

    override fun onTrafficLoading() {
        trafficLevel = null
        trafficFreshness = MapActivity.TrafficFreshness.Loading
        updateLevel()
    }

    override fun onTrafficExpired() {
        trafficLevel = null
        trafficFreshness = MapActivity.TrafficFreshness.Expired
        updateLevel()
    }

    private lateinit var searchEditText: EditText
    private lateinit var bottomBar: BottomNavigationView
    private lateinit var recycler: RecyclerView

    private fun setupBindings() {
//        recycler = findViewById(R.id.recycler)
        bottomBar = findViewById(R.id.bottomNavigationView)
        searchEditText = findViewById(R.id.searchEditText)
    }

    private fun setupUi() {
        bottomBar.selectedItemId = R.id.menu_list
        bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_profile -> {
//                    startActivity(Intent(this, UserActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        searchEditText.addTextChangedListener {
            it?.let {
//                vm.search(it.toString())
            }
        }

//        setupRecycler()
    }

}
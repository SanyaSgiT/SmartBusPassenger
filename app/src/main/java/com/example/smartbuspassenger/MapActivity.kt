package com.example.smartbuspassenger

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbuspassenger.ui.bottombar.InfoActivity
import com.example.smartbuspassenger.ui.bottombar.UserActivity
import com.example.smartbuspassenger.ui.routeList.RouteAdapter
import com.example.smartbuspassenger.ui.routeList.RouteListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.traffic.TrafficColor
import com.yandex.mapkit.traffic.TrafficLayer
import com.yandex.mapkit.traffic.TrafficLevel
import com.yandex.mapkit.traffic.TrafficListener
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapActivity : AppCompatActivity(), TrafficListener {

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

    private enum class TrafficFreshness {
        Loading, OK, Expired
    }

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
//        setupSearhc()
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

//    var recyclerVisible = false
//    private fun setupSearhc() {
//        recyclerView.isVisible = false
//        recyclerGetter.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.arrow_up))
//
//        recyclerGetter.setOnClickListener {
//            if (recyclerVisible == false) {
//                recyclerGetter.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.arrow_down))
//                recyclerView.isVisible = true
//                recyclerVisible = true
//            } else if (recyclerVisible == true) {
//                recyclerGetter.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.arrow_up))
//                recyclerView.isVisible = false
//                recyclerVisible = false
//            }
//        }
//    }
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
                vm.search(it.toString())
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
//            startActivity(
//                Intent(
//                    this,
//                    BookActivity::class.java
//                ).putExtra("bookId", it.id)
//            )
        }
        recyclerView.adapter = adapter
//        (recycler.layoutManager as GridLayoutManager).spanCount = 2
    }
}

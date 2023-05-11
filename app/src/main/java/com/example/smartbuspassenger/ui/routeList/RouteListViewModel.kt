package com.example.smartbuspassenger.ui.routeList

import android.graphics.Paint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartbuspassenger.data.models.TracesResponse
import com.example.smartbuspassenger.data.repository.RoutesRepository
import com.example.smartbuspassenger.domain.Route
import com.example.smartbuspassenger.domain.Trace
import kotlinx.coroutines.launch
import com.example.smartbuspassenger.ui.routeList.RouteRendering

class RouteListViewModel(
    private val routesRepository: RoutesRepository
) : ViewModel() {
    private val _routes = MutableLiveData<List<Route>>()
    val routes: LiveData<List<Route>> = _routes

    var p: Paint = Paint()
    var routePoints = MutableLiveData<List<Trace>>()
    var tracePoints: List<Double> = emptyList()
    var trace: FloatArray = floatArrayOf()

    init {
        viewModelScope.launch {
            _routes.postValue(routesRepository.getAllRoutes())
            //routePoints.postValue(routesRepository.getAllRoutes())
        }
    }

    fun search(id: Int) {
        viewModelScope.launch {
            val result = routesRepository.findRouteByName(id)
            _routes.postValue(result)
        }
    }

    fun drawRoute(id: Int){
        viewModelScope.launch {
            val traces = routesRepository.getTrace(id)
            println(traces)
        }
    }
}
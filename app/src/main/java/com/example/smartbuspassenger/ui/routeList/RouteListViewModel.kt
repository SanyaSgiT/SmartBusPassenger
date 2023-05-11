package com.example.smartbuspassenger.ui.routeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartbuspassenger.data.repository.RoutesRepository
import com.example.smartbuspassenger.domain.Route
import com.example.smartbuspassenger.domain.Trace
import kotlinx.coroutines.launch

class RouteListViewModel(
    private val routesRepository: RoutesRepository
) : ViewModel() {
    private val _routes = MutableLiveData<List<Route>>()
    val routes: LiveData<List<Route>> = _routes

    private val _traces = MutableLiveData<List<Trace>>()
    val traces: LiveData<List<Trace>> = _traces

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

    fun drawRoute(id: Int) {
        viewModelScope.launch {
            val traces = routesRepository.getTrace(id)
            println(traces)
            _traces.value = traces
        }
    }
}
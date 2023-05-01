package com.example.smartbuspassenger.ui.routeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartbuspassenger.data.functional.RoutesFunctions
import com.example.smartbuspassenger.domain.Route
import kotlinx.coroutines.launch

class RouteListViewModel(
    private val routesFunctions: RoutesFunctions
) : ViewModel() {
    private val _routes = MutableLiveData<List<Route>>()
    val routes: LiveData<List<Route>> = _routes

    init {
        viewModelScope.launch {
            _routes.postValue(routesFunctions.getAllRoutes())
        }
    }

//    fun search(query: String) {
//        viewModelScope.launch {
//            val result = if (query.isBlank()) {
//                routesFunctions.getAllRoutes()
//            } else {
//                routesFunctions.findRouteById(query)
//            }
//
//            _routes.postValue(result)
//        }
//    }
}
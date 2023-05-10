package com.example.smartbuspassenger.ui.routeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartbuspassenger.data.repository.RoutesRepository
import com.example.smartbuspassenger.domain.Route
import kotlinx.coroutines.launch

class RouteListViewModel(
    private val routesRepository: RoutesRepository
) : ViewModel() {
    private val _routes = MutableLiveData<List<Route>>()
    val routes: LiveData<List<Route>> = _routes

    init {
        viewModelScope.launch {
            _routes.postValue(routesRepository.getAllRoutes())
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            val result = if (query.isBlank()) {
                routesRepository.getAllRoutes()
            } else {
                routesRepository.findRouteByName(query)
            }
            _routes.postValue(result)
        }
    }
}
package com.example.reactivearch.ui.feature.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reactivearch.core.common.BaseViewModel
import com.example.reactivearch.core.common.DataState
import com.example.reactivearch.domain.dto.LocationDto
import com.example.reactivearch.domain.dto.RequestDto
import com.example.reactivearch.domain.entity.Restaurant
import com.example.reactivearch.domain.interactor.GetRestaurantsUseCase
import com.google.android.gms.maps.model.Marker
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val getRestaurantsUseCase: GetRestaurantsUseCase) :
    BaseViewModel() {
    private val _restaurantsState = MutableLiveData<DataState<List<Restaurant>>>()

    val restaurantsState: LiveData<DataState<List<Restaurant>>>
        get() = _restaurantsState

    val markers = HashMap<Marker, Restaurant>()

    var fragmentRecreated = false

    fun getRestaurants(requestDto: RequestDto) {
        if (_restaurantsState.value != null) return
        _restaurantsState.value = DataState.Loading
        getRestaurantsUseCase.execute(requestDto)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { restaurants -> _restaurantsState.value = restaurants }
            .also { compositeDispoable.add(it) }
    }

    fun resetRestaurantState() {
        _restaurantsState.value = null
    }

    fun getNewRestaurants(restaurants: List<Restaurant>): ArrayList<Restaurant> {
        val markersToBeDisplayed = ArrayList<Restaurant>()
        val mainList = markers.values
        if (mainList.isNotEmpty()) {
            restaurants.forEach {
                if (!mainList.contains(it))
                    markersToBeDisplayed.add(it)
            }
        } else {
            markersToBeDisplayed.addAll(restaurants)
        }

        return markersToBeDisplayed
    }
}
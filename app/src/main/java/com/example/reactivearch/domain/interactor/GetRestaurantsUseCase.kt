package com.example.reactivearch.domain.interactor

import com.example.reactivearch.core.common.DataState
import com.example.reactivearch.domain.dto.LocationDto
import com.example.reactivearch.domain.dto.RequestDto
import com.example.reactivearch.domain.entity.Restaurant
import com.example.reactivearch.domain.error.ErrorHandler
import com.example.reactivearch.domain.error.Failure
import com.example.reactivearch.domain.repository.RestaurantRepository
import io.reactivex.Single
import retrofit2.HttpException
import java.net.HttpURLConnection
import java.net.UnknownHostException
import javax.inject.Inject

class GetRestaurantsUseCase  @Inject constructor(private  val repository: RestaurantRepository) :
    Usecase <RequestDto , Single<DataState<List<Restaurant>>>> , ErrorHandler {
    override fun execute(param: RequestDto): Single<DataState<List<Restaurant>>>  {
        return  repository.getRestaurant(param).onErrorReturn { DataState.Error(getError(it)) }
    }

    override fun getError(throwable: Throwable): Failure {
        return when(throwable){
            is UnknownHostException -> Failure.NetworkConnectionError
            is HttpException -> {
                when(throwable.code()){
                    // not found
                    HttpURLConnection.HTTP_NOT_FOUND -> Failure.ServerError.NotFound
                    // access denied
                    HttpURLConnection.HTTP_FORBIDDEN -> Failure.ServerError.AccessDenied
                    // unavailable service
                    HttpURLConnection.HTTP_UNAVAILABLE -> Failure.ServerError.ServiceUnavailable
                    // all the others will be treated as unknown error
                    else -> Failure.UnknownError
                }
            }
            else -> Failure.UnknownError
        }
    }

}
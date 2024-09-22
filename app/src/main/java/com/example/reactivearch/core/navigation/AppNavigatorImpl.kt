package com.example.reactivearch.core.navigation

import androidx.fragment.app.FragmentActivity
import com.example.reactivearch.R
import com.example.reactivearch.domain.entity.Restaurant
import com.example.reactivearch.ui.feature.map.RestaurantMapsFragment
import com.example.reactivearch.ui.feature.restaurant.RestaurantDetailsFragment
import javax.inject.Inject

class AppNavigatorImpl @Inject constructor(private val activity: FragmentActivity) : AppNavigator {

    override fun navigateTo(screen: Screen, restaurant: Restaurant?) {
        val fragment = when(screen){
            Screen.MAP -> RestaurantMapsFragment()
            Screen.RESTAURANT -> RestaurantDetailsFragment.newInstance(restaurant)
        }

        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.home_container, fragment)
            .addToBackStack(fragment.javaClass.canonicalName)
            .commit()
    }
}
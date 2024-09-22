package com.example.reactivearch.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reactivearch.R
import com.example.reactivearch.core.navigation.AppNavigator
import com.example.reactivearch.core.navigation.Screen
import com.example.reactivearch.ui.feature.map.RestaurantMapsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appNavigator: AppNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            appNavigator.navigateTo(Screen.MAP)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(supportFragmentManager.backStackEntryCount == 0)
            finish()
    }
}
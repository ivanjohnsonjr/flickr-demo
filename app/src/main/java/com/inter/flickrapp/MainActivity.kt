package com.inter.flickrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.inter.flickrapp.ui.nav.MainNavHost
import com.inter.flickrapp.ui.theme.FlickrAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlickrAppTheme {
                MainNavHost(
                    navController = rememberNavController(),
                )
            }
        }
    }
}
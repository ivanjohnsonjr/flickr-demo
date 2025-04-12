package com.inter.flickrapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.inter.flickrapp.ui.detail.DetailScreen
import com.inter.flickrapp.ui.home.HomeScreen
import com.inter.flickrapp.ui.utils.toPhotoDetail
import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable
    object Home: Screens

    @Serializable
    data class PhotoDetail(
        val id: String,
        val secret: String,
        val server: String,
        val title: String,
    ): Screens
}

@Composable
fun MainNavHost(
    modifier: Modifier,
    navController: NavHostController
) {
    NavHost(navController, Screens.Home) {
        composable<Screens.Home> { backStackEntry ->
            HomeScreen(
                modifier = modifier,
                showDetail = {
                    navController.navigate(it.toPhotoDetail)
                }
            )
        }

        composable<Screens.PhotoDetail> {
            DetailScreen(
                modifier
            )
        }
    }
}
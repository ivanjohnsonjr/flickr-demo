package com.inter.flickrapp.ui.nav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.inter.flickrapp.ui.UiConstants.TWEEN_DURATION
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
    navController: NavHostController
) {
    NavHost(navController, Screens.Home) {
        composable<Screens.Home> { backStackEntry ->
            HomeScreen(
                showDetail = {
                    navController.navigate(it.toPhotoDetail)
                }
            )
        }

        composable<Screens.PhotoDetail>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(TWEEN_DURATION, easing = LinearEasing)
                ) + slideIntoContainer(
                    animationSpec = tween(TWEEN_DURATION, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(TWEEN_DURATION, easing = LinearEasing)
                ) + slideOutOfContainer(
                    animationSpec = tween(TWEEN_DURATION, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            DetailScreen(
                navController = navController
            )
        }
    }
}
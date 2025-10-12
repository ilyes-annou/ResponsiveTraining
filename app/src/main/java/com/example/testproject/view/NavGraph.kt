package com.example.testproject.view


import androidx.compose.runtime.Composable

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.platform.LocalSpatialConfiguration
import com.example.testproject.viewModel.VideoListViewModel
import kotlinx.serialization.Serializable


@Serializable
object VideoList

//Is a data class to allow receiving videoUrl from VideoList
@Serializable
data class Player(val videoUrl: String)

//Heart of the UI displays 2D UI for Mobile & XR HouseSpace mode and 3D UI for XR FullSpace mde
@Composable
fun NavGraph(navController: NavHostController, vm: VideoListViewModel = viewModel() ) {

    val spatialConfiguration = LocalSpatialConfiguration.current
    val isSpatialUiEnabled = LocalSpatialCapabilities.current.isSpatialUiEnabled

    if (isSpatialUiEnabled) { //checks if XR FullSpace mode is activated
        VideoListView3D(vm,spatialConfiguration)
    }

    else {
        //For 2D UI we have 2 screens, this allows switching
        NavHost(navController, startDestination = VideoList) {
            composable<VideoList> {
                VideoListView2D(
                    onNavigateToPlayer = { url -> navController.navigate(Player(videoUrl = url )) },
                    vm = vm,
                    spatialConfiguration = spatialConfiguration
                )
            }
            composable<Player> { backStackEntry ->
                val args = backStackEntry.toRoute<Player>()
                VideoPlayerScreen(
                    onNavigateToList = { navController.navigate(VideoList) },
                    videoUrl = args.videoUrl,
                    spatialConfiguration = spatialConfiguration
                )
            }
        }
    }
}











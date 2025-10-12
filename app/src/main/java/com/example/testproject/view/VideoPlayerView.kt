package com.example.testproject.view

import android.app.Activity
import android.content.pm.ActivityInfo
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testproject.viewModel.VideoListViewModel
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.platform.SpatialConfiguration
import androidx.xr.compose.spatial.ContentEdge
import androidx.xr.compose.spatial.Orbiter
import com.example.testproject.ui.theme.LightBlue

//Composable called by the NavHost, decides which UI to show
@Composable
fun VideoPlayerScreen(onNavigateToList: () -> Unit, videoUrl: String, vm: VideoListViewModel = viewModel(), spatialConfiguration: SpatialConfiguration) {
    //Setting the Video Player for all UI modes
    val context = LocalContext.current
    val activity = context as? Activity
    val spacialCapabilities = LocalSpatialCapabilities.current

    //The actual video player
    val videoView = remember(videoUrl) {
        VideoView(context).apply {
            vm.playVideo(this, videoUrl)
        }
    }

    //Puts mobile UI in landscape on launch
    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    //Restores mobile UI and stops the video on quit
    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            videoView.stopPlayback()
            videoView.suspend()
        }
    }

    //If 3D UI for FullSpace Mode, 2D UI for HouseSpace and Mobile modes
    if (spacialCapabilities.isSpatialUiEnabled) {
        VideoPlayer3D(videoView, onNavigateToList, vm)
    }
    else {
        VideoPlayer2D(videoView, onNavigateToList, vm)
    }

}

//2D UI video player, uses basic controls
@Composable
fun VideoPlayer2D(videoView: VideoView, onNavigateToList: () -> Unit, vm: VideoListViewModel){
    videoView.apply{
        //Basic video controller
        vm.setVideoController(this, LocalContext.current)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        //Hosts video player in compose
        AndroidView(
            factory = { videoView },
            modifier = Modifier.fillMaxSize()
        )

        //Quit button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
        ) {
            Button(
                onClick = onNavigateToList,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xAA000000))
            ) {
                Icon(
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = "Play",
                    tint = Color.White
                )
            }

        }
    }
}

//3D UI video player, uses Orbiters for control
@Composable
fun VideoPlayer3D(videoView: VideoView, onNavigateToList: () -> Unit, vm: VideoListViewModel){
    AndroidView(
        factory = { videoView },
        modifier = Modifier.fillMaxSize()
    )

    //Values for custom 3d controls
    val progress by vm.videoProgress.collectAsState()
    val duration by vm.videoDuration.collectAsState()
    val isPlaying by vm.isPlaying.collectAsState()

    //Quit button
    Orbiter(position = ContentEdge.Top,
        alignment = Alignment.End,
        offset = 60.dp){
        // Bouton pour quitter la vidéo

        Button(
            onClick = { onNavigateToList() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0x97FF0000)),
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }


    //Slider and pause button
    Orbiter(
        position = ContentEdge.Bottom,
        alignment = Alignment.CenterHorizontally,
        offset = 80.dp
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = Color(0x99000000),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {vm.playPause(videoView) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1958e6))
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White
                )
            }

            //Uses viewModel to check what timespace is pointed in order to reach it
            Slider(
                value = progress.toFloat(),
                onValueChange = { vm.seekTo(videoView, it.toInt()) },
                valueRange = 0f..duration.toFloat(),
                modifier = Modifier.width(250.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = LightBlue,
                    inactiveTrackColor = Color.Gray
                )
            )

        }
    }

}
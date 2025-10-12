package com.example.testproject.viewModel

import android.content.Context
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testproject.network.VideoRepository
import com.example.testproject.model.Video
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.xr.compose.platform.SpatialConfiguration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive

//ViewModel for video views
class VideoListViewModel(): ViewModel() {


    private val _videoProgress = MutableStateFlow(0)
    val videoProgress = _videoProgress.asStateFlow()

    private val _videoDuration = MutableStateFlow(1)
    val videoDuration = _videoDuration.asStateFlow()

    private var progressJob: Job? = null

    private var isFullSpaceMode = false

    //Checks initial launching of the video
    private val _isVideoLaunched = MutableStateFlow(false)
    val isVideoLaunched = _isVideoLaunched.asStateFlow()

    private val _currentVideoUrl = MutableStateFlow<String?>(null)

    val currentVideoUrl = _currentVideoUrl.asStateFlow()
    private val _videos = mutableStateListOf<Video>()
    val videos: List<Video> get() = _videos

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    init{
        loadVideos()
    }

    fun loadVideos() {
        viewModelScope.launch {
            val fetchedVideos = VideoRepository.getVideos()
            if(fetchedVideos!=null) {
                _videos.addAll(fetchedVideos)
            }
        }
    }

    //Used in 2D UI to set basic android controller
    fun setVideoController(videoView: VideoView, context: Context){
        val mediaController = MediaController(context).apply {
            setAnchorView(videoView)
        }
        videoView.setMediaController(mediaController)
    }

    fun setVideoLaunched(launched: Boolean) {
        _isVideoLaunched.value = launched
    }

    fun playVideo(videoView: VideoView, videoUrl: String): View {
        videoView.apply{
            setVideoURI(videoUrl.toUri())
            requestFocus()
            start()
            _isPlaying.value=true
        }

        _videoDuration.value = videoView.duration.takeIf { it > 0 } ?: 1

        //Checks on the video progression for slider every 500ms
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                if (videoView.isPlaying) {
                    _videoProgress.value = videoView.currentPosition
                    _videoDuration.value = videoView.duration.takeIf { it > 0 } ?: 1
                }
                delay(500)
            }
        }
        return videoView
    }

    fun playPause(videoView: VideoView){
        if(videoView.isPlaying){
            videoView.pause()
            _isPlaying.value = false
        }
        else{
            videoView.start()
            _isPlaying.value=true
        }
    }

    //On XR device switches between HomeSpace and FullSpace modes
    fun switchXRMode(spatialConfiguration: SpatialConfiguration){
        if (isFullSpaceMode) {
            spatialConfiguration.requestHomeSpaceMode()
        }
        else {
            spatialConfiguration.requestFullSpaceMode()
        }
        isFullSpaceMode = !isFullSpaceMode
    }

    fun showVideoPanelAndFootballField(videoUrl: String){
        _isVideoLaunched.value = true
        _currentVideoUrl.value=videoUrl
    }

    //Called by slider, goes to pointed timestamp
    fun seekTo(videoView: VideoView, progress: Int) {
        videoView.seekTo(progress)
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
    }

}
package com.example.testproject.network

import android.util.Log
import com.example.testproject.model.Video

//Static object to call the Video API
object VideoRepository {

    private val videoList = mutableListOf<Video>()
    private val api = RetrofitInstance.api

    private suspend fun fetchVideos(){
        val response = api.getVideos()
        videoList.addAll(response)

    }

    suspend fun getVideos(): MutableList<Video>? {
        try {
            if (videoList.isEmpty()) {
                fetchVideos()
            }
            return videoList
        }
        catch (e: Exception){
            Log.e("VideoRepository","Error ${e.message}")
        }
        return null
    }
}
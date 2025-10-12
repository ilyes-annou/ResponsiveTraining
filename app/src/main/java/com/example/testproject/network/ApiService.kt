package com.example.testproject.network

import com.example.testproject.model.Video
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
interface VideoApiService {

    //HTTP GET call to retrieve JSON of videos
    @GET("videos.json")
    suspend fun getVideos(): List<Video>
}

object RetrofitInstance {
    val api: VideoApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://hls-video-samples.r2.immersiv.cloud/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VideoApiService::class.java)
    }
}
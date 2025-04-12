package com.inter.flickrapp.core.repo

import com.inter.flickrapp.core.data.GetPhotoInfoResponse
import com.inter.flickrapp.core.data.PhotosResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrServiceApi {

    @GET("?method=flickr.photos.getRecent")
    fun getRecentPhotos(@Query("page") page: Int): Call<PhotosResponse>

    @GET("?method=flickr.photos.search")
    fun searchPhotos(@Query("text") text: String, @Query("page") page: Int): Call<PhotosResponse>

    @GET("?method=flickr.photos.getInfo")
    fun getPhotoInfo(@Query("photo_id") id: String, @Query("secret") secret: String): Call<GetPhotoInfoResponse>
}
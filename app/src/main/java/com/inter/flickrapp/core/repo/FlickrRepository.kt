package com.inter.flickrapp.core.repo

import com.inter.flickrapp.core.data.PhotoInfo
import com.inter.flickrapp.core.data.PhotosResponse.PhotosResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface FlickrRepository {

    fun getRecentPhotos(page: Int): Flow<PhotosResult>

    fun searchPhotos(query: String, page: Int): Flow<PhotosResult>

    fun getPhotoInfo(id: String, secret: String): Flow<PhotoInfo>
}

class FlickrRepositoryImpl(
    private val api: FlickrServiceApi
) : FlickrRepository {

    override fun getRecentPhotos(page: Int): Flow<PhotosResult> {
        return flow {
            val response = api.getRecentPhotos(page).execute()
            if (response.isSuccessful) {
                emit(response.body()!!.result)
            } else {
                error(response.message())
            }
        }
    }

    override fun searchPhotos(query: String, page: Int): Flow<PhotosResult> {
        return flow {
            val response = api.searchPhotos(query, page).execute()
            if (response.isSuccessful) {
                emit(response.body()!!.result)
            } else {
                error(response.message())
            }
        }
    }

    override fun getPhotoInfo(id: String, secret: String): Flow<PhotoInfo> {
        return flow {
            val response = api.getPhotoInfo(id, secret).execute()
            if (response.isSuccessful) {
                val body = response.body()!!
                if (body.stat == "ok") {
                    emit(body.result!!)
                } else {
                    error(body.message ?: "")
                }
            } else {
                error(response.message())
            }
        }
    }
}
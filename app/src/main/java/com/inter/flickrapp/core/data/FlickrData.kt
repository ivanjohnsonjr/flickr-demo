package com.inter.flickrapp.core.data

import com.google.gson.annotations.SerializedName

data class PhotosResponse(
    @SerializedName("photos")
    val result: PhotosResult,
    val stat: String,
    val message: String? = null
) {

    data class PhotosResult(
        val page: Int,
        val pages: Int,
        @SerializedName("perpage")
        val perPage: Int,
        val total: Int,
        @SerializedName("photo")
        val photos: List<Photo>
    )
}

data class Photo(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val title: String,
)

data class GetPhotoInfoResponse(
    @SerializedName("photo")
    val result: PhotoInfo? = null,
    val stat: String,
    val message: String? = null
)

data class PhotoInfo(
    val id: String,
    val secret: String,
    val server: String,
    val owner: Owner,
    val title: Content?,
    val description: Content?,
    val views: Int,
    val comments: Content?
) {

    data class Owner(
        @SerializedName("nsid")
        val id: String,
        val username: String,
        @SerializedName("realname")
        val realName: String,
        val location: String,
        @SerializedName("iconserver")
        val iconServer: String,
        @SerializedName("iconfarm")
        val iconFarm: Int,
    )

    data class Content(
        @SerializedName("_content")
        val content: String
    )

}


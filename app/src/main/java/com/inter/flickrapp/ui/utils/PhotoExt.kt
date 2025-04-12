package com.inter.flickrapp.ui.utils

import com.inter.flickrapp.core.data.Photo
import com.inter.flickrapp.ui.UiConstants.baseUrl
import com.inter.flickrapp.ui.nav.Screens.PhotoDetail
import kotlin.String

val Photo.imageUrl: String
    get() {
        return baseUrl.replace("{server-id}", server)
            .replace("{id}", id)
            .replace("{secret}", secret)
    }


val Photo.toPhotoDetail: PhotoDetail
    get() = PhotoDetail(
        id = id,
        secret = secret,
        server = server,
        title = title
    )


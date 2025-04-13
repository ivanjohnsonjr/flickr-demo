package com.inter.flickrapp.ui.utils

import com.inter.flickrapp.core.data.Photo
import com.inter.flickrapp.ui.UiConstants.PHOTO_URL
import com.inter.flickrapp.ui.nav.Screens.PhotoDetail
import kotlin.String

val Photo.imageUrl: String
    get() {
        return PHOTO_URL.replace("{server-id}", server)
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


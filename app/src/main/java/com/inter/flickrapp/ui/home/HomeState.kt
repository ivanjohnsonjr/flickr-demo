package com.inter.flickrapp.ui.home

import com.inter.flickrapp.core.data.Photo

data class HomeState(
    val query: String = "",
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val pageState: PagingState = PagingState()
) {
    data class PagingState(
        val prevPage: Int? = null,
        val currentPage: Int = 0,
        val nextPage: Int? = 1
    ) {
        val hasNextPage: Boolean
            get() = nextPage != null
    }

    val supportLoadMore: Boolean
        get() = !isLoading && pageState.hasNextPage
}

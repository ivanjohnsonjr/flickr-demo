package com.inter.flickrapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inter.flickrapp.core.data.Photo
import com.inter.flickrapp.core.repo.FlickrRepository
import com.inter.flickrapp.ui.home.HomeState.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repo: FlickrRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    private val _nextPageAction = MutableStateFlow(false)
    private val _pullToRefreshAction = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _photosDataFlow: Flow<List<Photo>> =
        combine(
            _nextPageAction,
            _pullToRefreshAction,
            _uiState.distinctUntilChanged { old, new -> old.query == new.query }
        ) { _, _, f2 -> _uiState.value } // Make sure we are operating with the latest state value
            .filter { it.pageState.hasNextPage } // If there not next page do nothing
            .map { Pair(it.query, it.pageState) }
            .map {
                //Indicate loading
                _uiState.update { it.copy( isLoading = true ) }
                it
            }
            .flatMapLatest {
                val query = it.first
                val page = it.second.nextPage!!

                //Fetch recent photos if there is no query string provided
                if (query.isEmpty()) {
                    repo.getRecentPhotos(page)
                } else {
                    repo.searchPhotos(query, page)
                }
            }
            .flowOn(Dispatchers.IO)
            .onEach { result ->
                //Process the Result and update paging information
                val totalPages = result.pages

                val currentPage = result.page
                val nextPage = if (currentPage < totalPages) {
                    currentPage + 1
                } else {
                    null
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        pageState = PagingState (
                            currentPage = currentPage,
                            prevPage = currentPage -1,
                            nextPage = nextPage
                        )
                    )
                }
            }
            .catch {
                _uiState.update { it.copy( isLoading = false ) }
            }
            .map { it.photos }

    init {
        viewModelScope.launch {
            _photosDataFlow
                .catch { ex ->
                    Timber.e(ex, "Error while fetching photos")
                    _uiState.update {
                        it.copy(
                            errorMessage = ex.message
                        )
                    }
                }.collect { newPhotos ->
                    _uiState.update {
                        it.copy(
                            photos = it.photos + newPhotos
                        )
                    }
                }
        }
    }

    /**
     * Search for photo. If the query string is empty it will return the recent photos, in addition
     * if refresh is true function refresh the photo list from the top
     *
     * @param query search string
     * @param refresh indicating if refresh is happening
     */
    fun search(query: String, refresh: Boolean = false) {
        //If not refreshing and the query is the same ignore
        if (!refresh && query == _uiState.value.query)
            return

        //Since the query changes reset the photo list and paging state
        _uiState.update {
            it.copy(
                query = query,
                photos = emptyList(),
                pageState = PagingState()
            )
        }

        if(refresh) {
            _pullToRefreshAction.update { !it }
        }
    }

    /**
     * Trigger to fetch the next page
     */
    fun getNextPage() {
        _nextPageAction.update { !it }
    }
}
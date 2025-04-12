package com.inter.flickrapp.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inter.flickrapp.core.data.PhotoInfo
import com.inter.flickrapp.core.repo.FlickrRepository
import com.inter.flickrapp.ui.detail.DetailViewModel.DetailState.Id
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repo: FlickrRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    data class DetailState(
        val id: Id? = null,
        val owner: PhotoInfo.Owner? = null,
        val title: String? = null,
        val description: String? = null,
        val commentCount: Int? = null,
        val viewCount: Int = 0,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) {
        data class Id(
            val id: String,
            val secret: String,
            val server: String,
        )
    }

    private val _uiState = MutableStateFlow(DetailState())
    val uiState = _uiState.asStateFlow()

    init {
        val id = savedStateHandle.get<String>("id") ?: throw IllegalArgumentException("Id is required")
        val secret = savedStateHandle.get<String>("secret") ?: throw IllegalArgumentException("Secret is required")
        val server = savedStateHandle.get<String>("server") ?: throw IllegalArgumentException("Server is required")
        val title = savedStateHandle.get<String>("title")

        _uiState.update {
            it.copy(
                id = Id(
                    id = id,
                    secret = secret,
                    server = server
                ),
                title = title
            )
        }

        //Load start loading data when view model is initialized
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val photo = _uiState.value.id!!

            repo.getPhotoInfo(photo.id, photo.secret)
                .flowOn(Dispatchers.IO)
                .catch { ex ->
                    Timber.e(ex, "Got exception when fetching photoInfo")
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = ex.message
                    ) }
                }
                .collect { result ->
                    Timber.i("I got the information back: $result")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            title = result.title?.content,
                            owner = result.owner,
                            commentCount = result.comments?.content?.toInt(),
                            viewCount = result.views,
                            description = result.description?.content
                        )
                    }
                }
        }
    }

}
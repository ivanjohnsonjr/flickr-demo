package com.inter.flickrapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.inter.flickrapp.R
import com.inter.flickrapp.core.data.Photo
import com.inter.flickrapp.ui.UiConstants
import com.inter.flickrapp.ui.theme.Dimen
import com.inter.flickrapp.ui.utils.imageUrl
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    showDetail: (Photo) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        InternalHomeScreen(
            modifier = Modifier.padding(innerPadding)
                .padding(horizontal = Dimen.unit),
            query = state.query,
            photos = state.photos,
            isLoading = state.isLoading,
            handleLoadMore = state.supportLoadMore,
            errorMessage = state.errorMessage,
            onSearch = { query, refresh -> viewModel.search(query, refresh) },
            onSelect = { showDetail(it) },
            onLoadMore = { viewModel.getNextPage() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InternalHomeScreen(
    query: String,
    photos: List<Photo>,
    isLoading: Boolean,
    handleLoadMore: Boolean,
    errorMessage: String?,
    onSearch: (String, Boolean) -> Unit,
    onSelect: (Photo) -> Unit,
    modifier: Modifier = Modifier,
    onLoadMore: () -> Unit
) {
    val gridState = rememberLazyStaggeredGridState()

    if (handleLoadMore) {
        LoadMoreHandler(
            listState = gridState,
            onLoadMore = onLoadMore
        )
    }

    Column (
        modifier = modifier.fillMaxSize()
    ) {
        FlickrSearchBar(
            modifier = Modifier.fillMaxWidth().padding(bottom = Dimen.unitx2),
            query = query,
            isLoading = isLoading
        ) {
            onSearch(it, false)
        }

        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = isLoading,
            onRefresh = { onSearch(query, true) }

        ) {

            //Error message display for first load scenarios
            errorMessage?.let {
                ErrorMessage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimen.unit),
                    text = it
                )
            }

            LazyVerticalStaggeredGrid(
                state = gridState,
                columns = StaggeredGridCells.Fixed(UiConstants.GRID_COLUMN),
                verticalItemSpacing = Dimen.unit,
                horizontalArrangement = Arrangement.spacedBy(Dimen.unit),
                modifier = Modifier.fillMaxSize()
            ) {
                items(count = photos.size) { index ->
                    val photo = photos[index]

                    PhotoView(
                        photoUrl = photo.imageUrl
                    ) {
                        onSelect(photo)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlickrSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    isLoading: Boolean,
    onSearch: (String) -> Unit
) {
    var text by remember { mutableStateOf(query) }

    SearchBar(
        modifier = modifier,
        inputField = {
            SearchBarDefaults.InputField(
                query = text,
                onQueryChange = { text = it },
                onSearch = { onSearch(it) },
                expanded = false,
                onExpandedChange = { },
                placeholder = { Text("Search") },
                leadingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(Dimen.unit),
                            strokeWidth = Dimen.unit,
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                trailingIcon = {},
            )
        },
        expanded = false,
        onExpandedChange = { },
        content = {}
    )
}

@Composable
private fun PhotoView(
    photoUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        AsyncImage(
            modifier = Modifier
                .defaultMinSize(minHeight = Dimen.imageMinSize, minWidth = Dimen.imageMinSize)
                .fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.photo_placeholder),
            contentDescription = "",
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ErrorMessage(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Image(
            modifier = modifier.size(32.dp),
            imageVector = Icons.Filled.Warning,
            colorFilter = ColorFilter.tint(Color.Red),
            contentDescription = text
        )

        Text(
            modifier = Modifier.padding(start = Dimen.unit),
            color = Color.Red,
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun LoadMoreHandler(
    listState: LazyStaggeredGridState,
    buffer: Int = UiConstants.BOTTOM_BUFFER,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex =
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            // Check if items are in buffer zone
            lastVisibleIndex >= (totalItems - buffer)
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadMore() }
    }

}


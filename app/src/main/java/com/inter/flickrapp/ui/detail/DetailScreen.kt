package com.inter.flickrapp.ui.detail

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.inter.flickrapp.R
import com.inter.flickrapp.core.data.PhotoInfo
import com.inter.flickrapp.ui.UiConstants.PHOTO_URL
import com.inter.flickrapp.ui.detail.DetailViewModel.DetailState
import com.inter.flickrapp.ui.home.ErrorMessage
import com.inter.flickrapp.ui.theme.Dimen

private val DetailState.imageUrl: String
    get() {
        return PHOTO_URL.replace("{server-id}", id!!.server)
            .replace("{id}", id.id)
            .replace("{secret}", id.secret)
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    state.title?.let {
                        Text(
                            modifier = Modifier.padding(start = Dimen.unit),
                            text = stringResource(R.string.detail_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton (onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.nav_back_description)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        InternalDetailScreen(
            modifier = Modifier.padding(innerPadding)
                .padding(horizontal = Dimen.unit),
            title = state.title,
            imageUrl = state.imageUrl,
            owner = state.owner,
            description = state.description,
            isRefreshing = state.isLoading,
            errorMessage = state.errorMessage
        ) {
            viewModel.load()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InternalDetailScreen(
    modifier: Modifier,
    title: String?,
    imageUrl: String,
    owner: PhotoInfo.Owner?,
    description: String?,
    isRefreshing: Boolean,
    errorMessage: String?,
    scrollState: ScrollState = rememberScrollState(),
    onRefresh: () -> Unit
) {

    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .padding(Dimen.unitx2),
            verticalArrangement = spacedBy(Dimen.unit),
        ) {

            errorMessage?.let {
                ErrorMessage(
                    modifier = Modifier.padding(bottom = Dimen.unitx2),
                    text = it
                )
            }

            title?.let {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            AsyncImage(
                modifier = Modifier
                    .defaultMinSize(minHeight = Dimen.imageMinSize, minWidth = Dimen.imageMinSize)
                    .fillMaxWidth(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.photo_placeholder),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )

            owner?.let {
                Row {
                   Column(
                       verticalArrangement = spacedBy(Dimen.unit)
                   ) {
                       if (it.realName.isNotEmpty()) {
                           Text(
                               text = it.realName,
                               style = MaterialTheme.typography.labelLarge,
                               fontWeight = FontWeight.Bold
                           )
                       }

                       Text(
                           text = it.username,
                           style = MaterialTheme.typography.labelMedium,
                           fontStyle = FontStyle.Italic
                       )
                   }
                }
            }

            description?.let {
                Row {
                    Text(
                        modifier = Modifier.padding(vertical = Dimen.unit),
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


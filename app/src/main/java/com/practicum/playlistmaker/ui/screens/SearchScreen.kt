package com.practicum.playlistmaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.screens.search.SearchState
import com.practicum.playlistmaker.ui.screens.search.SearchViewModel
import com.practicum.playlistmaker.ui.screens.search.components.TrackListItem
import com.practicum.playlistmaker.ui.theme.AppColors
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    // Получаем ViewModel через фабрику
    val viewModel: SearchViewModel = viewModel(
        factory = SearchViewModel.getViewModelFactory()
    )

    // Состояние экрана из ViewModel
    val screenState by viewModel.searchScreenState.collectAsState()

    // Локальное состояние для текстового поля
    var searchText by remember { mutableStateOf("") }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Белый контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(
                    color = AppColors.white,
                    shape = RoundedCornerShape(0.dp)
                )
        ) {
            // Контент экрана поиска
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp)
            ) {
                // Поисковая строка
                SearchTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    onSearchClick = {
                        if (searchText.isNotEmpty()) {
                            viewModel.search(searchText)
                        }
                    },
                    onClearClick = {
                        searchText = ""
                        viewModel.clearSearch()
                                   },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Отображение состояния поиска
                when (screenState) {
                    is SearchState.Initial -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.search_initial_text),
                                fontSize = 16.sp,
                                color = AppColors.gray
                            )
                        }
                    }

                    is SearchState.Searching -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is SearchState.Success -> {
                        val tracks = (screenState as SearchState.Success).foundList
                        if (tracks.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.search_empty_results),
                                    fontSize = 16.sp,
                                    color = AppColors.gray
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp)
                            ) {
                                items(tracks) { track ->
                                    TrackListItem(
                                        track = track,
                                        onClick = { onTrackClick(track) }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    is SearchState.Fail -> {
                        val error = (screenState as SearchState.Fail).error
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${stringResource(R.string.search_error_prefix)} $error",
                                fontSize = 16.sp,
                                color = AppColors.red
                            )
                        }
                    }
                }
            }
        }

        // Синяя шапка поверх контента
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(AppColors.white)
                .windowInsetsPadding(WindowInsets.statusBars),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBackClick() },
                    tint = AppColors.black
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.search_screen_title),
                    color = AppColors.black,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
                    )
                )
            }
        }
    }
}

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = {
            Text(
                text = stringResource(R.string.search_hint),
                color = AppColors.gray,
                fontSize = 16.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_icon),
                tint = AppColors.gray,
                modifier = Modifier.clickable { onSearchClick() }
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.clear_search),
                    modifier = Modifier
                        .clickable { onClearClick() },
                    tint = AppColors.gray
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.gray,
            unfocusedBorderColor = AppColors.lightGray,
            focusedContainerColor = AppColors.searchBackground,
            unfocusedContainerColor = AppColors.searchBackground,
            cursorColor = AppColors.black,
            focusedTextColor = AppColors.black,
            unfocusedTextColor = AppColors.black
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    PlaylistMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = AppColors.white) {
            SearchScreen(
                onBackClick = { },
                onTrackClick = { }
            )
        }
    }
}
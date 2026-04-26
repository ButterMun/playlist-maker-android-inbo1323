package com.practicum.playlistmaker.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.creator.Creator
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val tracksRepository: TracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    val searchTextState = queryFlow.asStateFlow()

    private val _historyState = MutableStateFlow<List<String>>(emptyList())
    val historyState = _historyState.asStateFlow()

    init {
        viewModelScope.launch {
            searchHistoryRepository.getSearchHistory().collect { history ->
                _historyState.value = history
            }
        }

        // Поиск с дебаунсом
        viewModelScope.launch {
            queryFlow
                .debounce(1200)
                .collectLatest { raw ->
                    val trimmed = raw.trim()
                    if (trimmed.isEmpty()) {
                        _searchScreenState.update { SearchState.Initial }
                    } else {
                        search(trimmed)
                        searchHistoryRepository.addSearchQuery(trimmed)
                        // История автоматически обновится через Flow выше
                    }
                }
        }
    }

    fun onQueryChanged(q: String) {
        queryFlow.value = q
    }

    fun clearSearch() {
        _searchScreenState.update { SearchState.Initial }
        queryFlow.value = ""
    }

    suspend fun searchQueryFromUI(query: String) = search(query)

    private suspend fun search(query: String) {
        try {
            _searchScreenState.update { SearchState.Searching }
            val list = tracksRepository.searchTracks(query)
            _searchScreenState.update { SearchState.Success(list) }
        } catch (_: IOException) {
            _searchScreenState.update { SearchState.Fail("Ошибка сети") }
        } catch (_: Throwable) {
            _searchScreenState.update { SearchState.Fail("Неизвестная ошибка") }
        }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        Creator.getTracksRepository(),
                        Creator.getSearchHistoryRepository()
                    ) as T
                }
            }
    }
}

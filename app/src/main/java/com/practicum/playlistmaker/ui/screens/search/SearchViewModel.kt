package com.practicum.playlistmaker.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {

    private val database = (tracksRepository as? TracksRepositoryImpl)?.database

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    val searchTextState = queryFlow.asStateFlow()

    private val _historyState = MutableStateFlow<List<String>>(emptyList())
    val historyState = _historyState.asStateFlow()

    init {
        // История
        viewModelScope.launch {
            database?.getHistory()?.collect { _historyState.value = it }
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
                        database?.addToHistory(trimmed)
                    }
                }
        }
    }

    fun onQueryChanged(q: String) {
        queryFlow.value = q
    }

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

    fun clearSearch() {
        _searchScreenState.update { SearchState.Initial }
        queryFlow.value = ""
    }

    suspend fun searchQueryFromUI(query: String) = search(query)

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(Creator.getTracksRepository()) as T
                }
            }
    }
}

package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.preferences.SearchHistoryPreferences
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepositoryImpl(
    private val searchHistoryPreferences: SearchHistoryPreferences
) : SearchHistoryRepository {

    override fun addSearchQuery(query: String) {
        searchHistoryPreferences.addEntry(query)
    }

    override fun getSearchHistory(): Flow<List<String>> {
        return searchHistoryPreferences.getEntries()
    }
}

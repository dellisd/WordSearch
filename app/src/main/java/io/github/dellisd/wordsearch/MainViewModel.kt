package io.github.dellisd.wordsearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(private val generator: WordSearchFactory) : ViewModel() {
    private val words = listOf("Kotlin", "Swift", "ObjectiveC", "Variable", "Java", "Mobile")

    private val _wordSearch = MutableLiveData<WordSearch>()
    val wordSearch: LiveData<WordSearch> = _wordSearch

    fun generateWordSearch() {
        _wordSearch.value = generator.generateWordSearch(words)
    }

}
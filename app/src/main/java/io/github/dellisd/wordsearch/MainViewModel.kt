package io.github.dellisd.wordsearch

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(private val generator: WordSearchFactory) : ViewModel() {
    private val words = listOf("Kotlin", "Swift", "ObjectiveC", "Variable", "Java", "Mobile")

    private var firstSelectedCell: Cell? = null

    private val _wordSearch = MutableLiveData<WordSearch>()
    val wordSearch: LiveData<WordSearch> = _wordSearch

    fun generateWordSearch() {
        _wordSearch.value = generator.generateWordSearch(words)
    }

    fun selectCell(cell: Cell): Boolean {
        Log.i("ViewModel", "Selected $cell")
        if (firstSelectedCell == null) {
            firstSelectedCell = cell
            return true
        }

        val firstCell = firstSelectedCell!!
        firstSelectedCell = null

        val search = wordSearch.value!!
        val words = search.words
        words.forEach { word ->
            if (word.start == firstCell && word.end == cell) {
                val newList = search.words.map {
                    if (it.text == word.text) it.copy(found = true) else it
                }

                Log.i("WordSearch", "Found $word")

                _wordSearch.value = search.copy(words = newList)
                return@forEach
            }
        }

        return true
    }

}
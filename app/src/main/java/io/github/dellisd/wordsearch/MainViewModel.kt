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

    private val _textGrid = MutableLiveData<Array<Array<Char>>>()
    val textGrid: LiveData<Array<Array<Char>>> = _textGrid

    var allowReverseWords = true

    fun generateWordSearch() {
        _wordSearch.value = generator.generateWordSearch(words, allowReverseWords)
        _textGrid.value = _wordSearch.value!!.grid
    }

    fun selectCell(cell: Cell) {
        if (firstSelectedCell == null) {
            firstSelectedCell = cell
            return
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

                _wordSearch.value = search.copy(words = newList)
                return@forEach
            }
        }
    }

}
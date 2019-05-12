package io.github.dellisd.wordsearch

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

    fun selectCell(row: Int, col: Int): Boolean {
        if (firstSelectedCell == null) {
            firstSelectedCell = Cell(row, col)
            return true
        }

        val firstCell = firstSelectedCell!!
        firstSelectedCell = null
        // Make sure that the second selected cell is either directly horizontal, vertical, or diagonal from the first cell
        if (firstCell.row != row && firstCell.col != col && firstCell.row - row != firstCell.col - col) return false

        val selectionDirection: PlacementDirection = when {
            firstCell.row == row && firstCell.row - row < 0 -> PlacementDirection.HORIZONTAL
            firstCell.row == row && firstCell.row - row > 0 -> PlacementDirection.HORIZONTAL_REVERSE
            firstCell.col == col && firstCell.col - col < 0 -> PlacementDirection.VERTICAL
            firstCell.col == col && firstCell.col - col > 0 -> PlacementDirection.VERTICAL_REVERSE
            firstCell.col - col > 0 -> PlacementDirection.DIAGONAL_REVERSE
            else -> PlacementDirection.DIAGONAL
        }

        val search = wordSearch.value!!
        val words = search.words
        words.forEach { word ->
            var found = true
            word.text.forEachIndexed { index, letter ->
                val nRow = when (selectionDirection) {
                    PlacementDirection.HORIZONTAL, PlacementDirection.DIAGONAL -> firstCell.row + index
                    PlacementDirection.HORIZONTAL_REVERSE, PlacementDirection.DIAGONAL_REVERSE -> firstCell.row - index
                    else -> firstCell.row
                }

                val nCol = when (selectionDirection) {
                    PlacementDirection.VERTICAL, PlacementDirection.DIAGONAL -> firstCell.col + index
                    PlacementDirection.VERTICAL_REVERSE, PlacementDirection.DIAGONAL_REVERSE -> firstCell.col - index
                    else -> firstCell.col
                }

                if (letter != search.grid[nRow][nCol]) {
                    found = false
                    return@forEachIndexed
                }
            }

            if (found) {
                val newList = search.words.map {
                    if (it.text == word.text) it.copy(found = true) else it
                }

                _wordSearch.value = search.copy(words = newList)
            }
        }

        return true
    }

}
package io.github.dellisd.wordsearch

import java.util.*
import kotlin.random.Random

// "Kotlin", "Swift", "ObjectiveC", "Variable", "Java", "Mobile"
class WordSearchFactory(
    var gridSize: Int = 10
) {
    private val randomInstance = Random(Date().time)
    private val letterPool = 'A'..'Z'

    fun generateWordSearch(words: List<String>): WordSearch {
        val output = Array(gridSize) { Array(gridSize) { Char.MIN_VALUE } }
        val placedWords = mutableListOf<Word>()

        val sortedWords = words
            .sortedByDescending(String::length)
            .map(String::toUpperCase)

        sortedWords.forEach { word ->
            var placed = false
            while (!placed) {
                val start = Cell(
                    (0 until gridSize).random(randomInstance),
                    (0 until gridSize).random(randomInstance)
                )

                val placement = PlacementDirection.values().random(randomInstance)

                if (!checkPlacement(word, start, placement)) continue
                if (checkOverlaps(output, word, start, placement)) {
                    placed = true
                    placedWords.add(placeWord(output, word, start, placement))
                }
            }
        }

        // Fill the remaining spaces on the grid with random letters
        output.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                if (cell == Char.MIN_VALUE) {
                    output[y][x] = letterPool.random(randomInstance)
                }
            }
        }

        return WordSearch(output, placedWords)
    }

    /**
     * Check that a word can be validly placed on the word search grid without going out of the
     * bounds of the grid.
     *
     * @param word The word being placed
     * @param start The cell where the word starts
     * @param placement The placement direction for the word
     * @see PlacementDirection
     *
     * @return Whether or not the word can be placed on the board
     */
    private fun checkPlacement(word: String, start: Cell, placement: PlacementDirection): Boolean {
        val (row, col) = start
        return when (placement) {
            PlacementDirection.VERTICAL -> row + word.length < gridSize
            PlacementDirection.VERTICAL_REVERSE -> row - word.length > 0
            PlacementDirection.HORIZONTAL -> col + word.length < gridSize
            PlacementDirection.HORIZONTAL_REVERSE -> col - word.length > 0
            PlacementDirection.DIAGONAL -> row + word.length < gridSize && col + word.length < gridSize
            PlacementDirection.DIAGONAL_REVERSE -> row - word.length > 0 && col - word.length > 0
        }
    }

    /**
     * Checks for illegal overlaps in the grid when placing a new word. Words are only allowed to overlap if they
     * share a common letter at the overlap position.
     *
     * @param grid The grid the word is being placed on
     * @param start The cell where the word starts
     * @param placement The placement direction for the word
     * @see PlacementDirection
     *
     * @return Whether the new word would be in a valid position that contains only valid overlaps
     */
    private fun checkOverlaps(
        grid: Array<Array<Char>>,
        word: String,
        start: Cell,
        placement: PlacementDirection
    ): Boolean {
        // Iterate through each letter of the word and check for overlap at the corresponding grid position
        word.forEachIndexed { index, c ->
            val (row, col) = start.plus(index, placement)

            if (grid[row][col] != Char.MIN_VALUE && grid[row][col] != c) {
                return false
            }
        }

        return true
    }

    private fun placeWord(grid: Array<Array<Char>>, word: String, start: Cell, placement: PlacementDirection): Word {
        word.forEachIndexed { index, c ->
            val (row, col) = start.plus(index, placement)

            grid[row][col] = c
        }

        return Word(word, start, start.plus(word.length - 1, placement), placement)
    }

}


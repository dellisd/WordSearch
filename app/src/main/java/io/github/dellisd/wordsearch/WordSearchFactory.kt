package io.github.dellisd.wordsearch

import java.util.*
import kotlin.random.Random

// "Kotlin", "Swift", "ObjectiveC", "Variable", "Java", "Mobile"
class WordSearchFactory(
    var words: List<String> = listOf("Kotlin", "Swift", "ObjectiveC", "Variable", "Java", "Mobile"),
    var gridSize: Int = 10
) {
    private val randomInstance = Random(Date().time)
    private val letterPool = 'A'..'Z'

    fun generateWordSearch(): WordSearch {
        val output = Array(gridSize) { Array(gridSize) { Char.MIN_VALUE } }
        val placedWords = mutableListOf<Word>()

        val sortedWords = words
            .sortedByDescending(String::length)
            .map(String::toUpperCase)

        sortedWords.forEach { word ->
            var placed = false
            while (!placed) {
                val x = (0 until gridSize).random(randomInstance)
                val y = (0 until gridSize).random(randomInstance)
                val placement = PlacementDirection.values().random(randomInstance)

                if (!checkPlacement(word, x, y, placement)) continue
                if (checkOverlaps(output, word, x, y, placement)) {
                    placed = true
                    placedWords.add(placeWord(output, word, x, y, placement))
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
     * @param x The target x position of the beginning of the word
     * @param y The target y position of the beginning of the word
     * @param placement The placement direction for the word
     * @see PlacementDirection
     *
     * @return Whether or not the word can be placed on the board
     */
    private fun checkPlacement(word: String, x: Int, y: Int, placement: PlacementDirection): Boolean {
        return when (placement) {
            PlacementDirection.VERTICAL -> y + word.length < gridSize
            PlacementDirection.VERTICAL_REVERSE -> y - word.length > 0
            PlacementDirection.HORIZONTAL -> x + word.length < gridSize
            PlacementDirection.HORIZONTAL_REVERSE -> x - word.length > 0
            PlacementDirection.DIAGONAL -> y + word.length < gridSize && x + word.length < gridSize
            PlacementDirection.DIAGONAL_REVERSE -> y - word.length > 0 && x - word.length > 0
        }
    }

    /**
     * Checks for illegal overlaps in the grid when placing a new word. Words are only allowed to overlap if they
     * share a common letter at the overlap position.
     *
     * @param grid The grid the word is being placed on
     * @param x The target x position of the beginning of the word
     * @param y The target y position of the beginning of the word
     * @param placement The placement direction for the word
     * @see PlacementDirection
     *
     * @return Whether the new word would be in a valid position that contains only valid overlaps
     */
    private fun checkOverlaps(
        grid: Array<Array<Char>>,
        word: String,
        x: Int,
        y: Int,
        placement: PlacementDirection
    ): Boolean {
        // Iterate through each letter of the word and check for overlap at the corresponding grid position
        word.forEachIndexed { index, c ->
            val nX = when (placement) {
                PlacementDirection.HORIZONTAL, PlacementDirection.DIAGONAL -> x + index
                PlacementDirection.HORIZONTAL_REVERSE, PlacementDirection.DIAGONAL_REVERSE -> x - index
                else -> x
            }

            val nY = when (placement) {
                PlacementDirection.VERTICAL, PlacementDirection.DIAGONAL -> y + index
                PlacementDirection.VERTICAL_REVERSE, PlacementDirection.DIAGONAL_REVERSE -> y - index
                else -> y
            }

            if (grid[nY][nX] != Char.MIN_VALUE && grid[nY][nX] != c) {
                return false
            }
        }

        return true
    }

    private fun placeWord(grid: Array<Array<Char>>, word: String, x: Int, y: Int, placement: PlacementDirection): Word {
        word.forEachIndexed { index, c ->
            val nX = when (placement) {
                PlacementDirection.HORIZONTAL, PlacementDirection.DIAGONAL -> x + index
                PlacementDirection.HORIZONTAL_REVERSE, PlacementDirection.DIAGONAL_REVERSE -> x - index
                else -> x
            }

            val nY = when (placement) {
                PlacementDirection.VERTICAL, PlacementDirection.DIAGONAL -> y + index
                PlacementDirection.VERTICAL_REVERSE, PlacementDirection.DIAGONAL_REVERSE -> y - index
                else -> y
            }

            grid[nY][nX] = c
        }

        return Word(word, x, y, placement)
    }

}


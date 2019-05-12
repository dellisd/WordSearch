package io.github.dellisd.wordsearch

data class WordSearch(
    val grid: Array<Array<Char>>,
    val words: List<Word>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WordSearch

        if (!grid.contentDeepEquals(other.grid)) return false
        if (words != other.words) return false

        return true
    }

    override fun hashCode(): Int {
        var result = grid.contentDeepHashCode()
        result = 31 * result + words.hashCode()
        return result
    }
}

data class Word(
    val text: String,
    val x: Int,
    val y: Int,
    val placementDirection: PlacementDirection
)

enum class PlacementDirection {
    VERTICAL,
    VERTICAL_REVERSE,
    HORIZONTAL,
    HORIZONTAL_REVERSE,
    DIAGONAL,
    DIAGONAL_REVERSE
}
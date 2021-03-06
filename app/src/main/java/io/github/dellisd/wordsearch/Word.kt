package io.github.dellisd.wordsearch

data class Word(
    val text: String,
    val start: Cell,
    val end: Cell,
    val placementDirection: PlacementDirection,
    val found: Boolean = false
)

enum class PlacementDirection {
    VERTICAL,
    VERTICAL_REVERSE,
    HORIZONTAL,
    HORIZONTAL_REVERSE,
    DIAGONAL,
    DIAGONAL_REVERSE
}
package io.github.dellisd.wordsearch

data class Cell (val row: Int, val col: Int) {

    /**
     * Gets a cell a certain amount of units away from this cell in a specified direction
     *
     * @param amount The number of units away the new cell will be
     * @param direction The direction away that the new cell should be in
     */
    fun plus(amount: Int, direction: PlacementDirection): Cell {
        val nCol = when (direction) {
            PlacementDirection.HORIZONTAL, PlacementDirection.DIAGONAL -> col + amount
            PlacementDirection.HORIZONTAL_REVERSE, PlacementDirection.DIAGONAL_REVERSE -> col - amount
            else -> col
        }

        val nRow = when (direction) {
            PlacementDirection.VERTICAL, PlacementDirection.DIAGONAL -> row + amount
            PlacementDirection.VERTICAL_REVERSE, PlacementDirection.DIAGONAL_REVERSE -> row - amount
            else -> row
        }

        return Cell(nRow, nCol)
    }
}
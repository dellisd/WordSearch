package io.github.dellisd.wordsearch

import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var textGrid: Array<Array<TextView>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createTextGrid()
    }

    private fun createTextGrid() {
        val grid = findViewById<GridLayout>(R.id.verticalContainer)

        textGrid =
            Array(10) { i ->
                Array(10) { j ->
                    createTextCell(i, j, grid)
                }
            }
    }

    private fun createTextCell(row: Int, col: Int, gridLayout: GridLayout): TextView = TextView(this).apply {
        gridLayout.addView(this)
        text = "$row,$col"
        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        gravity = Gravity.CENTER

        layoutParams = GridLayout.LayoutParams().apply {
            height = GridLayout.LayoutParams.WRAP_CONTENT
            width = GridLayout.LayoutParams.WRAP_CONTENT
            setGravity(Gravity.CENTER)
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
        }
    }
}

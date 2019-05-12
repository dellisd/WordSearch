package io.github.dellisd.wordsearch

import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class MainActivity : AppCompatActivity() {

    private lateinit var textViewGrid: Array<Array<TextView>>
    private val gridSize: Int = 12

    private lateinit var viewModel: MainViewModel
    private val viewModelFactory: MainViewModelFactory by lazy { MainViewModelFactory(WordSearchFactory(gridSize = gridSize)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<GridLayout>(R.id.verticalContainer).apply {
            rowCount = gridSize
            columnCount = gridSize
        }

        createTextGrid()

        viewModel = ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]

        viewModel.generateWordSearch()
        viewModel.wordSearch.observe(this, Observer { (letterGrid, _) ->
            if (letterGrid.size != textViewGrid.size) return@Observer

            letterGrid.forEachIndexed { i, row ->
                row.forEachIndexed { j, col ->
                    textViewGrid[i][j].text = col.toString()
                }
            }
        })
    }

    private fun createTextGrid() {
        val grid = findViewById<GridLayout>(R.id.verticalContainer)

        textViewGrid =
            Array(gridSize) { i ->
                Array(gridSize) { j ->
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

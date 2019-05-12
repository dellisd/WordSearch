package io.github.dellisd.wordsearch

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

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
        viewModel.wordSearch.observe(this, Observer { (letterGrid, words) ->
            if (letterGrid.size != textViewGrid.size) return@Observer

            val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
            chipGroup.removeAllViews()

            val overlay = findViewById<SelectedWordsOverlay>(R.id.selectedWordsOverlay)
            overlay.clearWords()
            words.forEach { word ->
                val (text, _, _, _, found) = word
                val chip = Chip(this)
                chip.text = text
                chip.isCheckable = true
                chip.isChecked = found
                if (found) {
                    overlay.addWord(word)
                }

                chipGroup.addView(chip)
            }
        })
        viewModel.textGrid.observe(this, Observer { letterGrid ->
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

        findViewById<SelectedWordsOverlay>(R.id.selectedWordsOverlay).attachedGrid = textViewGrid
    }

    private fun createTextCell(row: Int, col: Int, gridLayout: GridLayout): TextView = TextView(this).apply {
        gridLayout.addView(this)
        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        gravity = Gravity.CENTER
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        tag = Cell(row, col)
        setOnClickListener { gridInteraction(this) }
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                cellHighlight(this)
            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_OUTSIDE) {
                cellUnHighlight(this)
                gridInteraction(this)
            }

            true
        }

        layoutParams = GridLayout.LayoutParams().apply {
            height = GridLayout.LayoutParams.WRAP_CONTENT
            width = GridLayout.LayoutParams.WRAP_CONTENT
            setGravity(Gravity.CENTER)
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
        }
    }

    private fun gridInteraction(cell: TextView) {
        viewModel.selectCell(cell.tag as Cell)

        // TODO: Handle word selection UI
    }

    private fun cellHighlight(cell: TextView) {
        cell.setBackgroundColor(Color.parseColor("#FF0000"))
    }

    private fun cellUnHighlight(cell: TextView) {
        cell.setBackgroundColor(Color.parseColor("#00FF0000"))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.main, menu)

        // By default, reverse words are allowed
        menu?.get(1)?.isChecked = true

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.refresh -> viewModel.generateWordSearch()
            R.id.reversible -> {
                viewModel.allowReverseWords = !item.isChecked
                item.isChecked = !item.isChecked
            }
            else -> {}
        }

        return super.onOptionsItemSelected(item)
    }
}

package io.github.dellisd.wordsearch

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
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

    private var firstTouchedCell: Cell? = null
    private var lastTouchedCell: Cell? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<GridLayout>(R.id.verticalContainer).apply {
            rowCount = gridSize
            columnCount = gridSize
        }

        createTextGrid()

        viewModel = ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]

        if (viewModel.wordSearch.value == null) {
            viewModel.generateWordSearch()
        }

        viewModel.wordSearch.observe(this, Observer { (letterGrid, words) ->
            if (letterGrid.size != textViewGrid.size) return@Observer

            val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
            chipGroup.removeAllViews()

            val overlay = findViewById<SelectedWordsOverlay>(R.id.selectedWordsOverlay)
            overlay.clearWords()
            words.forEach { word ->
                val (wordText, _, _, _, found) = word
                Chip(this).apply {
                    text = wordText
                    isCheckable = true
                    isChecked = found
                    chipGroup.addView(this)
                    // Disable all touch events on the chips
                    setOnTouchListener { _, _ -> true }
                }

                if (found) {
                    overlay.addWord(word)
                }
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

    @SuppressLint("ClickableViewAccessibility")
    private fun createTextGrid() {
        val grid = findViewById<GridLayout>(R.id.verticalContainer)

        textViewGrid =
            Array(gridSize) { i ->
                Array(gridSize) { j ->
                    createTextCell(i, j, grid)
                }
            }

        findViewById<SelectedWordsOverlay>(R.id.selectedWordsOverlay).attachedGrid = textViewGrid

        grid.setOnTouchListener { v, event ->
            // Check if the user highlighted a word when they lift their finger
            if (event.action == MotionEvent.ACTION_UP) {
                if (viewModel.verifySelection(firstTouchedCell!!, lastTouchedCell!!)) {
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                }

                lastTouchedCell = null
                firstTouchedCell = null

                findViewById<SelectedWordsOverlay>(R.id.selectedWordsOverlay).apply {
                    previewEndCell = null
                    previewStartCell = null
                }
                return@setOnTouchListener true
            }

            val x = Math.round(event.x)
            val y = Math.round(event.y)

            // Get the TextView cell that is currently under the user's finger
            grid.children.forEach { child ->
                if (x > child.left && x < child.right && y > child.top && y < child.bottom) {
                    val overlay = findViewById<SelectedWordsOverlay>(R.id.selectedWordsOverlay)
                    if (lastTouchedCell !== child.tag) {
                        child.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        // Set the end cell posiition for the preview
                        overlay.previewEndCell = child.tag as Cell
                    }

                    if (firstTouchedCell == null) {
                        firstTouchedCell = child.tag as Cell
                        // Set the start cell position for the preview overlay
                        overlay.previewStartCell = firstTouchedCell
                    } else {
                        lastTouchedCell = child.tag as Cell
                    }
                }
            }

            true
        }
    }

    private fun createTextCell(row: Int, col: Int, gridLayout: GridLayout): TextView = TextView(this).apply {
        gridLayout.addView(this)
        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        gravity = Gravity.CENTER
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        tag = Cell(row, col)

        layoutParams = GridLayout.LayoutParams().apply {
            height = GridLayout.LayoutParams.WRAP_CONTENT
            width = GridLayout.LayoutParams.WRAP_CONTENT
            setGravity(Gravity.CENTER or Gravity.FILL)
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
        }
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
            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }
}

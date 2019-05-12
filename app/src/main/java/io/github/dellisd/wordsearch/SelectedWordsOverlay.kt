package io.github.dellisd.wordsearch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.TextView

class SelectedWordsOverlay @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    View(context, attrs, defStyle) {

    var attachedGrid: Array<Array<TextView>> = emptyArray()
    private val words: MutableList<Word> = mutableListOf()

    // This view's position on the screen to calculate relative draw positions
    private val positionOnScreen = IntArray(2)

    // The start/end points of draw lines
    private val startPoint = IntArray(2)
    private val endPoint = IntArray(2)

    // Draw parameters
    var highlightColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            paint.color = value
            paint.alpha = (255 * highlightAlpha).toInt()
        }
    var highlightWidth: Float = 48f
        set (value) {
            field = value
            paint.strokeWidth = value
        }
    var highlightAlpha: Float = 0.5f
        set (value) {
            field = value
            paint.alpha = (255 * value).toInt()
        }

    private val paint = Paint().apply {
        color = highlightColor
        isAntiAlias = true
        strokeWidth = highlightWidth
        style = Paint.Style.FILL_AND_STROKE
        strokeCap = Paint.Cap.ROUND
        alpha = (255 * highlightAlpha).toInt()
    }

    init {
        setWillNotDraw(false)

        context.theme.obtainStyledAttributes(attrs, R.styleable.SelectedWordsOverlay, 0, 0).apply {
            try {
                highlightColor = getColor(R.styleable.SelectedWordsOverlay_highlightColor, Color.TRANSPARENT)
                highlightWidth = getDimension(R.styleable.SelectedWordsOverlay_highlightWidth, 48f)
                highlightAlpha = getDimension(R.styleable.SelectedWordsOverlay_highlightAlpha, 0.5f)
            } finally {
                recycle()
            }
        }
    }

    fun addWord(vararg word: Word) {
        words.addAll(word)
        invalidate()
    }

    fun clearWords() {
        words.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        getLocationOnScreen(positionOnScreen)

        words.forEach { word ->
            attachedGrid[word.start.row][word.start.col].getLocationOnScreen(startPoint)
            attachedGrid[word.end.row][word.end.col].getLocationOnScreen(endPoint)

            val startX = startPoint[0] + (attachedGrid[word.start.row][word.start.col].width / 2f) - positionOnScreen[0]
            val startY =
                startPoint[1] + (attachedGrid[word.start.row][word.start.col].height / 2f) - positionOnScreen[1]
            val endX = endPoint[0] + (attachedGrid[word.end.row][word.end.col].width / 2f) - positionOnScreen[0]
            val endY = endPoint[1] + (attachedGrid[word.end.row][word.end.col].height / 2f) - positionOnScreen[1]

            canvas?.drawLine(startX, startY, endX, endY, paint)
        }
    }

}
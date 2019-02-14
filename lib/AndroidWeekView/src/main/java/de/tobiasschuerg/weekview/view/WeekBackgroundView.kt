package de.tobiasschuerg.weekview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.View
import de.tobiasschuerg.weekview.util.DayHelper
import de.tobiasschuerg.weekview.util.dipToPixelF
import de.tobiasschuerg.weekview.util.dipToPixelI
import de.tobiasschuerg.weekview.util.toLocalString
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit
import java.text.DateFormatSymbols
import java.util.Calendar
import kotlin.math.roundToInt

internal class WeekBackgroundView constructor(context: Context) : View(context) {

    private val accentPaint: Paint by lazy {
        Paint().apply { strokeWidth = DIVIDER_WIDTH_PX.toFloat() * 2 }
    }

    private val paintDivider: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth = DIVIDER_WIDTH_PX.toFloat()
            color = DIVIDER_COLOR
        }
    }
    private val mPaintLabels: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.GRAY
            textSize = context.dipToPixelF(12f)
            textAlign = Paint.Align.CENTER
        }
    }

    private var isInScreenshotMode = false

    val topOffsetPx: Int = context.dipToPixelI(32f)
    private val leftOffset: Int = context.dipToPixelI(48f)

    private var drawCount = 0

    val days: MutableList<Int> = DayHelper.createListStartingOn()
        .toMutableList()
        .apply {
            remove(Calendar.SATURDAY)
            remove(Calendar.SUNDAY)
        }

    var startTime: LocalTime = LocalTime.of(12, 0)
        private set
    private var endTime: LocalTime = LocalTime.of(12, 0)

    var scalingFactor = 1f
        /**
         * Updated the scaling factor and redraws the view.
         */
        set(value) {
            field = value
            requestLayout()
            // invalidate()
        }

    fun setAccentColor(color: Int) {
        accentPaint.color = color
    }

    override fun onDraw(canvas: Canvas) {
        Log.d(TAG, "Drawing background for the ${++drawCount}. time.")
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)

        canvas.drawHorizontalDividers()
        canvas.drawColumnsWithHeaders()

        Log.d(TAG, "Screenshot mode? $isInScreenshotMode")
        if (!isInScreenshotMode && !isInEditMode) {
            drawNowIndicator(canvas)
        }
        Log.d(TAG, "Drawing background completed.")
    }

    private fun drawNowIndicator(canvas: Canvas) {
        if (startTime.isBefore(LocalTime.now()) && endTime.isAfter(LocalTime.now())) {
            Log.v(TAG, "Drawing now indicator")
            val nowOffset = Duration.between(startTime, LocalTime.now())

            val minutes = nowOffset.toMinutes()
            val y = topOffsetPx + context.dipToPixelF(minutes * scalingFactor)
            accentPaint.alpha = 200
            canvas.drawLine(0f, y.toFloat(), canvas.width.toFloat(), y.toFloat(), accentPaint)
        }
    }

    private fun Canvas.drawHorizontalDividers() {
        Log.d(TAG, "Drawing horizontal dividers")
        var localTime = startTime
        var last = LocalTime.MIN
        while (localTime.isBefore(endTime) && !last.isAfter(localTime)) {
            val offset = Duration.between(startTime, localTime)
            Log.v(TAG, "Offset $offset")
            val y = topOffsetPx + context.dipToPixelF(offset.toMinutes() * scalingFactor)
            drawLine(0f, y, width.toFloat(), y, paintDivider)

            // final String timeString = localTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
            val timeString = localTime.toLocalString(context)
            drawMultiLineText(this, timeString, context.dipToPixelF(25f), y + context.dipToPixelF(20f), mPaintLabels)

            last = localTime
            localTime = localTime.plusHours(1)
        }
        val offset = Duration.between(startTime, localTime)
        Log.v(TAG, "Offset + $offset")
        drawLine(0f, bottom.toFloat(), width.toFloat(), bottom.toFloat(), paintDivider)
    }

    private fun Canvas.drawColumnsWithHeaders() {
        Log.v(TAG, "Drawing vertical dividers on canvas")
        val todayDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        for ((column, dayId) in days.withIndex()) {
            drawLeftColumnDivider(column)
            drawWeekDayName(dayId, column)
            if (dayId == todayDay) {
                drawDayHighlight(column)
            }
        }
    }

    private fun Canvas.drawLeftColumnDivider(column: Int) {
        val left: Int = getColumnStart(column, false)
        drawLine(left.toFloat(), 0f, left.toFloat(), bottom.toFloat(), paintDivider)
    }

    private fun Canvas.drawDayHighlight(column: Int) {
        val left2: Int = getColumnStart(column, true)
        val right: Int = getColumnEnd(column, true)
        val rect = Rect(left2, 0, right, bottom)
        accentPaint.alpha = 32
        drawRect(rect, accentPaint)
    }

    private fun Canvas.drawWeekDayName(dayId: Int, column: Int) {
        val shortName = DateFormatSymbols().shortWeekdays[dayId]
        val xLabel = (getColumnStart(column, false) + getColumnEnd(column, false)) / 2
        drawText(shortName, xLabel.toFloat(), topOffsetPx / 2 + mPaintLabels.descent(), mPaintLabels)
    }

    private fun drawMultiLineText(canvas: Canvas, text: String, initialX: Float, initialY: Float, paint: Paint) {
        var currentY = initialY
        text.split(" ")
            .dropLastWhile(String::isEmpty)
            .forEach {
                canvas.drawText(it, initialX, currentY, paint)
                currentY += (-paint.ascent() + paint.descent()).toInt()
            }
    }

    /**
     * Returns the offset (px!) from left for a given column.
     * First column is the first weekday.
     *
     * @param column starting to count at 0
     * @return offset in px
     */
    internal fun getColumnStart(column: Int, considerDivider: Boolean): Int {
        val contentWidth: Int = width - leftOffset
        var offset: Int = leftOffset + contentWidth * column / days.size
        if (considerDivider) {
            offset += (DIVIDER_WIDTH_PX / 2)
        }
        return offset
    }

    internal fun getColumnEnd(column: Int, considerDivider: Boolean): Int {
        val contentWidth: Int = width - leftOffset
        var offset: Int = leftOffset + contentWidth * (column + 1) / days.size
        if (considerDivider) {
            offset -= (DIVIDER_WIDTH_PX / 2)
        }
        return offset
    }

    override fun onMeasure(widthMeasureSpec: Int, hms: Int) {
        val height = topOffsetPx + context.dipToPixelF(getDurationMinutes() * scalingFactor) + paddingBottom
        val heightMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(height.roundToInt(), View.MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec2)
    }

    fun setScreenshotMode(screenshotMode: Boolean) {
        isInScreenshotMode = screenshotMode
    }

    fun updateTimes(startTime: LocalTime, endTime: LocalTime) {
        if (startTime.isAfter(endTime)) throw IllegalArgumentException()
        if (startTime.isBefore(this.startTime)) {
            this.startTime = startTime.truncatedTo(ChronoUnit.HOURS)
        }
        if (endTime.isAfter(this.endTime)) {
            this.endTime = endTime.truncatedTo(ChronoUnit.HOURS).plusHours(1)
        }
    }

    private fun getDurationMinutes(): Long {
        return Duration.between(startTime, endTime).toMinutes()
    }

    companion object {
        /** Thickness of the grid.
         * Should be a multiple of 2 because of rounding. */
        private const val DIVIDER_WIDTH_PX: Int = 2
        private const val DIVIDER_COLOR = Color.LTGRAY
        private const val TAG = "WeekBackgroundView"
    }
}

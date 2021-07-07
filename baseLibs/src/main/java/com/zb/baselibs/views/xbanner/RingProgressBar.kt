package com.zb.baselibs.views.xbanner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntDef
import com.zb.baselibs.R

class RingProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    View(context, attrs, defStyle) {
    private val paint: Paint = Paint()
    private var mWidth = 0
    private var mHeight = 0
    private val result: Int
    private val padding: Float
    private var ringColor: Int
    private var ringProgressColor: Int
    private var textColor: Int
    private var textSize: Float
    private var ringWidth: Float
    private var max: Int
    private var progress: Int
    private val textIsShow: Boolean
    private var style: Int
    private var mOnProgressListener: OnProgressListener? = null
    private var centre = 0
    private var radius = 0

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        centre = width / 2
        radius = (centre - ringWidth / 2).toInt()
        drawCircle(canvas)
        drawTextContent(canvas)
        drawProgress(canvas)
    }

    private fun drawCircle(canvas: Canvas) {
        paint.color = ringColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = ringWidth
        paint.isAntiAlias = true
        canvas.drawCircle(centre.toFloat(), centre.toFloat(), radius.toFloat(), paint)
    }

    private fun drawTextContent(canvas: Canvas) {
        paint.strokeWidth = 0f
        paint.color = textColor
        paint.textSize = textSize
        paint.typeface = Typeface.DEFAULT
        val percent = (progress.toFloat() / max.toFloat() * 100).toInt()
        val textWidth = paint.measureText("$percent%")
        if (textIsShow && percent != 0 && style == STROKE) {
            canvas.drawText("$percent%", centre - textWidth / 2, centre + textSize / 2, paint)
        }
    }

    private fun drawProgress(canvas: Canvas) {
        paint.strokeWidth = ringWidth
        paint.color = ringProgressColor
        val strokeOval = RectF(
            (centre - radius).toFloat(), (centre - radius).toFloat(), (centre + radius).toFloat(),
            (centre + radius).toFloat()
        )
        val fillOval = RectF(
            centre - radius + ringWidth + padding,
            centre - radius + ringWidth + padding, centre + radius - ringWidth - padding,
            centre + radius - ringWidth - padding
        )
        when (style) {
            STROKE -> {
                paint.style = Paint.Style.STROKE
                paint.strokeCap = Paint.Cap.ROUND
                canvas.drawArc(strokeOval, -90f, 360f * progress / max, false, paint)
            }
            FILL -> {
                paint.style = Paint.Style.FILL_AND_STROKE
                paint.strokeCap = Paint.Cap.ROUND
                if (progress != 0) {
                    canvas.drawArc(fillOval, -90f, 360f * progress / max, true, paint)
                }
            }
            else -> {
            }
        }
    }

    @IntDef(STROKE, FILL)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class PROGRESS_STYLE

    fun setStyle(@PROGRESS_STYLE style: Int) {
        this.style = style
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        mWidth = if (widthMode == MeasureSpec.AT_MOST) {
            result
        } else {
            widthSize
        }
        mHeight = if (heightMode == MeasureSpec.AT_MOST) {
            result
        } else {
            heightSize
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    fun getMax(): Int {
        return max
    }

    fun setMax(max: Int) {
        require(max >= 0) { "The max progress of 0" }
        this.max = max
    }

    fun getProgress(): Int {
        return progress
    }

    fun setProgress(progress: Int) {
        var progress = progress
        require(progress >= 0) { "The progress of 0" }
        if (progress > max) {
            progress = max
        }
        this.progress = progress
        postInvalidate()
        if (progress == max) {
            if (mOnProgressListener != null) {
                mOnProgressListener!!.progressToComplete()
            }
        }
    }

    private fun dp2px(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }

    interface OnProgressListener {
        fun progressToComplete()
    }

    fun setOnProgressListener(mOnProgressListener: OnProgressListener?) {
        this.mOnProgressListener = mOnProgressListener
    }

    companion object {
        const val STROKE = 0
        const val FILL = 1
    }

    init {
        result = dp2px(100)
        val mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RingProgressBar)
        ringColor = mTypedArray.getColor(R.styleable.RingProgressBar_ringColor, Color.BLACK)
        ringProgressColor = mTypedArray.getColor(
            R.styleable.RingProgressBar_ringProgressColor,
            Color.WHITE
        )
        textColor = mTypedArray.getColor(R.styleable.RingProgressBar_textColor, Color.BLACK)
        textSize = mTypedArray.getDimension(R.styleable.RingProgressBar_textSize, 16f)
        ringWidth = mTypedArray.getDimension(R.styleable.RingProgressBar_ringWidth, 5f)
        max = mTypedArray.getInteger(R.styleable.RingProgressBar_max, 100)
        textIsShow = mTypedArray.getBoolean(R.styleable.RingProgressBar_textIsShow, true)
        style = mTypedArray.getInt(R.styleable.RingProgressBar_style, 0)
        progress = mTypedArray.getInteger(R.styleable.RingProgressBar_progress, 0)
        padding = mTypedArray.getDimension(R.styleable.RingProgressBar_ringPadding, 5f)
        mTypedArray.recycle()
    }
}
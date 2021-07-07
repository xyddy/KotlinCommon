package com.zb.baselibs.utils.glide

import android.content.res.Resources
import android.graphics.*
import androidx.annotation.IntDef
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.dip2px
import java.security.MessageDigest

class GlideRoundTransform @JvmOverloads constructor(
    dpRadius: Float,
    marginDp: Int,
    cornerType: Int = CORNER_ALL,
    @ScaleType scaleType: Int = FIT_CENTER,
) :
    BitmapTransformation() {
    @IntDef(FIT_CENTER, CENTER_CROP, CENTER_INSIDE)
    annotation class ScaleType

    private val radius: Float = BaseApp.context.dip2px(dpRadius).toFloat()
    private val diameter: Float
    private val margin: Float
    private var cornerType: Int = CORNER_ALL

    @ScaleType
    private val scaleType: Int
    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int,
    ): Bitmap {
        val bitmap: Bitmap = when (scaleType) {
            CENTER_CROP -> TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight)
            CENTER_INSIDE -> TransformationUtils.centerInside(
                pool,
                toTransform,
                outWidth,
                outHeight
            )
            FIT_CENTER -> TransformationUtils.fitCenter(pool, toTransform, outWidth, outHeight)
            else -> TransformationUtils.fitCenter(pool, toTransform, outWidth, outHeight)
        }
        return roundCrop(pool, bitmap)!!
    }

    private fun roundCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) return null
        val result = pool[source.width, source.height, Bitmap.Config.ARGB_8888]
        result.setHasAlpha(true)
        val canvas = Canvas(result)
        canvas.drawFilter =
            PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        drawRoundRect(canvas, paint, source.width.toFloat(), source.height.toFloat())
        return result
    }

    private fun drawRoundRect(canvas: Canvas, paint: Paint, width: Float, height: Float) {
        val right = width - margin
        val bottom = height - margin
        canvas.drawRoundRect(RectF(margin, margin, right, bottom), radius, radius, paint)

        //把不需要的圆角去掉
        val notRoundedCorners = cornerType xor CORNER_ALL
        if (notRoundedCorners and CORNER_TOP_LEFT != 0) {
            clipTopLeft(canvas, paint, radius)
        }
        if (notRoundedCorners and CORNER_TOP_RIGHT != 0) {
            clipTopRight(canvas, paint, radius, right)
        }
        if (notRoundedCorners and CORNER_BOTTOM_LEFT != 0) {
            clipBottomLeft(canvas, paint, radius, bottom)
        }
        if (notRoundedCorners and CORNER_BOTTOM_RIGHT != 0) {
            clipBottomRight(canvas, paint, radius, right, bottom)
        }
    }

    override fun toString(): String {
        return ("RoundedTransformation(radius=" + radius + ", margin=" + margin + ", diameter="
                + diameter + ", cornerType=" + cornerType + ")")
    }

    override fun equals(o: Any?): Boolean {
        return o is GlideRoundTransform && o.radius == radius && o.diameter == diameter && o.margin == margin && o.cornerType == cornerType
    }

    override fun hashCode(): Int {
        return (ID.hashCode() + radius * 10000 + diameter * 1000 + margin * 100 + cornerType * 10).toInt()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + radius + diameter + margin + cornerType).toByteArray(CHARSET))
    }

    companion object {
        private const val VERSION = 1

        //这个ID值 随意
        private const val ID =
            "jp.wasabeef.glide.transformations.RoundedCornersTransformation.$VERSION"
        const val CORNER_NONE = 0
        const val CORNER_TOP_LEFT = 1
        const val CORNER_TOP_RIGHT = 1 shl 1
        const val CORNER_BOTTOM_LEFT = 1 shl 2
        const val CORNER_BOTTOM_RIGHT = 1 shl 3
        const val CORNER_ALL =
            CORNER_TOP_LEFT or CORNER_TOP_RIGHT or CORNER_BOTTOM_LEFT or CORNER_BOTTOM_RIGHT
        const val CORNER_TOP = CORNER_TOP_LEFT or CORNER_TOP_RIGHT
        const val CORNER_BOTTOM = CORNER_BOTTOM_LEFT or CORNER_BOTTOM_RIGHT
        const val CORNER_LEFT = CORNER_TOP_LEFT or CORNER_BOTTOM_LEFT
        const val CORNER_RIGHT = CORNER_TOP_RIGHT or CORNER_BOTTOM_RIGHT
        const val FIT_CENTER = 1
        const val CENTER_CROP = 2
        const val CENTER_INSIDE = 3
        private fun clipTopLeft(canvas: Canvas, paint: Paint, offset: Float) {
            val block = RectF(0F, 0F, offset, offset)
            canvas.drawRect(block, paint)
        }

        private fun clipTopRight(canvas: Canvas, paint: Paint, offset: Float, width: Float) {
            val block = RectF(width - offset, 0F, width, offset)
            canvas.drawRect(block, paint)
        }

        private fun clipBottomLeft(canvas: Canvas, paint: Paint, offset: Float, height: Float) {
            val block = RectF(0F, height - offset, offset, height)
            canvas.drawRect(block, paint)
        }

        private fun clipBottomRight(
            canvas: Canvas,
            paint: Paint,
            offset: Float,
            width: Float,
            height: Float,
        ) {
            val block = RectF(width - offset, height - offset, width, height)
            canvas.drawRect(block, paint)
        }
    }

    init {
        diameter = radius * 2
        margin = Resources.getSystem().displayMetrics.density * marginDp
        when (cornerType) {
            0 -> this.cornerType = CORNER_ALL
            1 -> this.cornerType = CORNER_NONE
            2 -> this.cornerType = CORNER_TOP_LEFT
            3 -> this.cornerType = CORNER_TOP_RIGHT
            4 -> this.cornerType = CORNER_BOTTOM_LEFT
            5 -> this.cornerType = CORNER_BOTTOM_RIGHT
            6 -> this.cornerType = CORNER_TOP
            7 -> this.cornerType = CORNER_BOTTOM
            8 -> this.cornerType = CORNER_LEFT
            9 -> this.cornerType = CORNER_RIGHT
        }
        this.scaleType = scaleType
    }
}
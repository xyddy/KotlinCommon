package com.zb.baselibs.views.imagebrowser.transforms

import android.view.View

class DepthPageTransformer : ABaseTransformer() {
    override fun onTransform(page: View, position: Float) {
        if (position <= 0f) {
            page.translationX = 0f
            page.scaleX = 1f
            page.scaleY = 1f
        } else if (position <= 1f) {
            val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position))
            page.alpha = 1 - position
            page.pivotY = 0.5f * page.height
            page.translationX = page.width * -position
            page.scaleX = scaleFactor
            page.scaleY = scaleFactor
        }
    }

    override val isPagingEnabled: Boolean
        get() = true

    companion object {
        private const val MIN_SCALE = 0.75f
    }
}

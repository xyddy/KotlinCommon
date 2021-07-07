package com.zb.baselibs.views.imagebrowser.transforms

import android.view.View

class RotateDownTransformer : ABaseTransformer() {
    override fun onTransform(page: View, position: Float) {
        val width = page.width.toFloat()
        val height = page.height.toFloat()
        val rotation = ROT_MOD * position * -1.25f
        page.pivotX = width * 0.5f
        page.pivotY = height
        page.rotation = rotation
    }

    override val isPagingEnabled: Boolean
        get() = true

    companion object {
        private const val ROT_MOD = -15f
    }
}

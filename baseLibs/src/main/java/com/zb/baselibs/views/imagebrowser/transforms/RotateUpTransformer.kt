package com.zb.baselibs.views.imagebrowser.transforms

import android.view.View

class RotateUpTransformer : ABaseTransformer() {

    override fun onTransform(page: View, position: Float) {
        val width = page.width.toFloat()
        val rotation = ROT_MOD * position
        page.pivotX = width * 0.5f
        page.pivotY = 0f
        page.translationX = 0f
        page.rotation = rotation
    }

    override val isPagingEnabled: Boolean
        get() = true

    companion object {
        private const val ROT_MOD = -15f
    }
}
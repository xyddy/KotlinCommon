package com.zb.baselibs.views.imagebrowser.transforms

import android.view.View

class DefaultTransformer : ABaseTransformer() {
    override fun onTransform(page: View, position: Float) {}
    override val isPagingEnabled: Boolean
        get() = true
}

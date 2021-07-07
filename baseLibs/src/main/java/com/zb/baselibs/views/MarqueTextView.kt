package com.zb.baselibs.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


class MarqueTextView : AppCompatTextView {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?) : super(context!!) {}

    override fun isFocused(): Boolean {
        return true
    }
}
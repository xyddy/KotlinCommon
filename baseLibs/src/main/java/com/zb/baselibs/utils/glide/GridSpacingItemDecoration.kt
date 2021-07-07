package com.zb.baselibs.utils.glide

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridSpacingItemDecoration(//列数
    private val spanCount: Int, //间隔
    private val spacing: Int, //是否包含边缘
    private val includeEdge: Boolean
) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        //这里是关键，需要根据你有几列来判断
        val params = view.layoutParams as GridLayoutManager.LayoutParams
        val position = params.spanIndex
        val column = position % spanCount
        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
        }
        outRect.top = 0
        outRect.bottom = spacing
    }
}
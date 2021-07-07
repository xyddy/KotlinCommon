package com.zb.baselibs.views.touch

import android.content.Context
import android.os.Vibrator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.zb.baselibs.app.BaseApp

class SimpleItemTouchHelperCallback(adapter: ItemTouchHelperAdapter) :
    ItemTouchHelper.Callback() {
    private val mAdapter: ItemTouchHelperAdapter = adapter
    private var sort = false
    private var dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN //允许上下左右的拖动
    private var swipeFlags = 0 //不允许侧滑'
    private var swipeEnabled = false
    private val vibrator: Vibrator =
        BaseApp.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator // 震动

    private var isVibrator = false
    fun setDragFlags(dragFlags: Int) {
        this.dragFlags = dragFlags
    }

    fun setSwipeFlags(swipeFlags: Int) {
        this.swipeFlags = swipeFlags
    }

    fun setSwipeEnabled(swipeEnabled: Boolean) {
        this.swipeEnabled = swipeEnabled
    }

    /*
     * 用于返回可以滑动的方向
     * */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    /**
     * 长按选中Item的时候开始调用
     *
     * @param viewHolder
     * @param actionState
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (sort) {
            isVibrator = if (!isVibrator) {
                vibrator.vibrate(200)
                true
            } else {
                false
            }
        }
    }

    /**
     * 手指松开的时候还原
     *
     * @param recyclerView
     * @param viewHolder
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
    }

    /*
     * 当用户拖动一个Item进行上下移动从旧的位置到新的位置的时候会调用该方法
     * */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    /*
     * 当用户左右滑动Item达到删除条件时，会调用该方法
     * */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mAdapter.onItemDelete(viewHolder.adapterPosition)
    }

    //滑动消失的距离，当滑动小于这个值的时候会删除这个item，否则不会视为删除
    // 返回值作为用户视为拖动的距离
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.3f
    }

    //设置item是否可以拖动
    fun setSort(sort: Boolean) {
        this.sort = sort
    }

    /*
     * 该方法返回true时，表示支持长按拖动，即长按ItemView后才可以拖动  默认返回true
     * */
    override fun isLongPressDragEnabled(): Boolean {
        return sort
    }

    /*
     * 该方法返回true时，表示如果用户触摸并左右滑动了View，
     * 那么可以执行滑动删除操作，即可以调用到onSwiped()方法。默认是返回true
     * */
    override fun isItemViewSwipeEnabled(): Boolean {
        return swipeEnabled
    }

}
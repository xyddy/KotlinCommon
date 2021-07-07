package com.zb.baselibs.adapter

interface ItemTouchHelperAdapter {
    //数据交换前后位置
    fun onItemMove(fromPosition: Int, toPosition: Int)

    //滑动删除
    fun onItemDelete(position: Int)
}

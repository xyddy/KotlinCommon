package com.zb.baselibs.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.jakewharton.rxbinding2.view.RxView
import com.library.flowlayout.FlowLayoutManager
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.zb.baselibs.R
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.glide.*
import java.util.concurrent.TimeUnit

// 按钮防抖
@SuppressLint("CheckResult")
@BindingAdapter("onClickDelayed")
fun View.onClickDelayed(listener: View.OnClickListener) {
    RxView.clicks(this) //两秒钟之内只取一个点击事件，防抖操作
        .throttleFirst(1, TimeUnit.SECONDS)
        .subscribe { listener.onClick(this) }
}

// 计算view大小
@BindingAdapter(value = ["viewWidthSize", "viewHeightSize"], requireAll = false)
fun View.viewSize(widthSize: Int, heightSize: Int) {
    val para = this.layoutParams
    para.width = widthSize
    para.height = heightSize
    this.layoutParams = para
}

// RecyclerView
@BindingAdapter(
    value = ["adapter", "recyclerType", "size", "color", "gridNum", "includeEdge"],
    requireAll = false
)
fun <T> initAdapter(
    view: RecyclerView,
    adapter: BindingItemAdapter<T>, recyclerType: Int, size: Int, color: Int,
    gridNum: Int, includeEdge: Boolean
) {
    view.adapter = adapter
    view.itemAnimator = MyItemAnimator()
    if (recyclerType == 0) {
        // 竖向列表
        view.layoutManager = LinearLayoutManager(view.context)
        if (size != 0) {
            if (view.itemDecorationCount == 0) {
                view.addItemDecoration(
                    MyDecoration(
                        view.context, LinearLayoutManager.HORIZONTAL,
                        size, view.context.resources.getColor(color)
                    )
                )
            }
        }
    } else if (recyclerType == 1) {
        // 横向列表
        val manager = LinearLayoutManager(view.context)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        view.layoutManager = manager
    } else if (recyclerType == 2) {
        // 九宫格
        view.layoutManager = GridLayoutManager(view.context, gridNum)
        if (view.itemDecorationCount == 0 && size > 0) {
            view.addItemDecoration(GridSpacingItemDecoration(gridNum, size, includeEdge))
        }
    } else if (recyclerType == 3) {
        // 瀑布流
        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        view.itemAnimator = null
        view.layoutManager = manager
        // 防止顶部item出现空白
        view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val first = IntArray(2)
                manager.findFirstCompletelyVisibleItemPositions(first)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (first[0] == 1 || first[1] == 1)) {
                    manager.invalidateSpanAssignments()
                }
            }
        })
    } else if (recyclerType == 4) {
        // 横向排列自动换行
        view.layoutManager = FlowLayoutManager()
    }
    try {
        if (view.itemAnimator != null) view.itemAnimator!!.changeDuration = 0
    } catch (ignored: Exception) {
    }
}

// 下拉刷新
@BindingAdapter("onRefreshListener")
fun SmartRefreshLayout.onRefreshListener(onRefreshListener: OnRefreshListener?) {
    this.setOnRefreshListener(onRefreshListener)
}

// 上拉加载更多
@BindingAdapter("onLoadMoreListener")
fun SmartRefreshLayout.onLoadMoreListener(onLoadMoreListener: OnLoadMoreListener?) {
    this.setOnLoadMoreListener(onLoadMoreListener)
}

// 加载图片
@SuppressLint("CheckResult")
@BindingAdapter(
    value = ["imageUrl", "imageRes", "defaultRes", "viewWidthSize", "viewHeightSize", "isCircle",
        "roundSize", "isBitmap", "cornerType", "isBlur", "scale"],
    requireAll = false
)
fun loadImage(
    view: ImageView, imageUrl: String?, imageRes: Int, defaultRes: Int, widthSize: Int,
    heightSize: Int, isCircle: Boolean, roundSize: Float,
    isBitmap: Boolean, cornerType: Int, isBlur: Boolean, scale: Float
) {
    var imageUrl = imageUrl
    try {
        var multiTransformation: MultiTransformation<Bitmap>?
        val transformationList = mutableListOf<Transformation<Bitmap>>()

        val cropOptions = RequestOptions().centerCrop()
        val defaultOptions = RequestOptions().centerCrop()
        transformationList.add(CenterCrop())
        if (isBlur) {
            transformationList.add(BlurTransformation())
        }
        if (isCircle) {
            transformationList.add(CircleCrop())
        }
        if (roundSize > 0) {
            transformationList.add(GlideRoundTransform(roundSize, 0, cornerType))
        }

        multiTransformation = MultiTransformation(transformationList)
        cropOptions.transform(multiTransformation)
        defaultOptions.transform(multiTransformation)

        if (isBitmap) {
            Glide.with(view.context).asBitmap().load(imageUrl).apply(RequestOptions().centerCrop())
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap?>?
                    ) {
                        val width = resource.width.toFloat()
                        val height = resource.height.toFloat()
                        view.viewSize(
                            (BaseApp.W * scale).toInt(),
                            (BaseApp.W * scale * height / width).toInt()
                        )
                        Glide.with(view.context).load(resource).apply(cropOptions).into(view)
                    }
                })
        } else {
            if (widthSize != 0)
                view.viewSize(widthSize, heightSize)
            val builder: RequestBuilder<Drawable>
            builder = if (defaultRes == 0) {
                Glide.with(view.context).asDrawable().apply(cropOptions)
            } else {
                val thumb = Glide.with(view.context).asDrawable().apply(defaultOptions)
                Glide.with(view.context).asDrawable().thumbnail(thumb.load(defaultRes))
                    .apply(cropOptions)
            }
            if (imageRes != 0) {
                builder.load(imageRes).into(view)
            } else {
                if (imageUrl != null && imageUrl == "QQ") {
                    builder.load(R.drawable.share_qq_ico).into(view)
                } else if (imageUrl != null && imageUrl == "QQ空间") {
                    builder.load(R.drawable.share_qqzore_ico).into(view)
                } else if (imageUrl != null && imageUrl == "微信") {
                    builder.load(R.drawable.share_wx_ico).into(view)
                } else if (imageUrl != null && imageUrl == "朋友圈") {
                    builder.load(R.drawable.share_wxcircle_ico).into(view)
                } else {
                    if (imageUrl != null && imageUrl.contains(".mp3"))
                        imageUrl = ""
                    builder.load(imageUrl).into(view)
                }
            }
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}
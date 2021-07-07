package com.zb.baselibs.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerAdapter<T, B : ViewDataBinding?>(
    var mContext: AppCompatActivity?,
    private var mList: MutableList<T>?,
    @param:LayoutRes private val layoutId: Int
) :
    RecyclerView.Adapter<RecyclerHolder<B>>() {
    private var isMax = false
    fun setMax(max: Boolean) {
        isMax = max
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder<B> {
        val bing: B = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            layoutId, parent, false
        )
        return RecyclerHolder(bing)
    }

    override fun onBindViewHolder(holder: RecyclerHolder<B>, position: Int) {
        if (isMax) {
            if (mList!!.size != 0) {
                if (mList!!.size < 3) {
                    val t = mList!![position]
                    onBind(holder, t, position)
                } else {
                    val index = position % mList!!.size
                    val t = mList!![index]
                    onBind(holder, t, index)
                }
            }
        } else {
            val t = mList!![position]
            onBind(holder, t, position)
        }
    }

    override fun getItemCount(): Int {
        return if (mList == null) 0 else if (isMax) {
            if (mList!!.size < 3) {
                mList!!.size
            } else {
                Int.MAX_VALUE
            }
        } else mList!!.size
    }

    var list: MutableList<T>?
        get() = mList
        set(mList) {
            this.mList = mList
            notifyDataSetChanged()
        }

    protected abstract fun onBind(holder: RecyclerHolder<B>?, t: T, position: Int)

    /*
     Recylerview的item是 ImageView 和  TextView构成，当数据改变时，我们会调用 notifyDataSetChanged，这个时候列表会刷新，
     为了使 url 没变的 ImageView 不重新加载（图片会一闪）我们可以用 setHasStableIds(true);
   */
    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    /*
      使用这个，相当于给ImageView加了一个tag，tag不变的话，不用重新加载图片。
      但是加了这句话，会使得 列表的 数据项 重复！！ 我们需要在我们的Adapter里面重写 getItemId就好了。
    */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}
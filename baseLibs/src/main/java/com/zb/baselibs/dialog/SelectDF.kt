package com.zb.baselibs.dialog

import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.zb.baselibs.R
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.databinding.DfSelectBinding
import org.jaaksi.pickerview.adapter.WheelAdapter
import org.jaaksi.pickerview.widget.BasePickerView
import org.jaaksi.pickerview.widget.BasePickerView.OnSelectedListener
import org.jaaksi.pickerview.widget.DefaultCenterDecoration

class SelectDF(activity: AppCompatActivity) : BaseDialogFragment(activity), OnSelectedListener {

    private lateinit var binding: DfSelectBinding
    private var list = ArrayList<String>()
    private lateinit var selectBack: SelectBack
    private var position: Int = 0

    fun show(manager: FragmentManager?) {
        show(manager!!, "${BaseApp.projectName}_SelectDF")
    }

    fun setData(list: ArrayList<String>): SelectDF {
        this.list = list
        return this
    }

    fun setSelectBack(selectBack: SelectBack): SelectDF {
        this.selectBack = selectBack
        return this
    }

    override val layoutId: Int
        get() = R.layout.df_select

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfSelectBinding
    }

    override fun initUI() {
        binding.dialog = this
        binding.selectPv.visibleItemCount = if (list.size < 5) 3 else 5
        setAdapter()
    }

    override fun onSelected(pickerView: BasePickerView<*>?, position: Int) {
        this.position = position
    }

    private fun setAdapter() {
        val decoration = DefaultCenterDecoration(context)
        decoration.setLineColor(Color.parseColor("#eeeeee"))
        decoration.setLineWidth(0.1f)
        binding.selectPv.adapter = SelectAdapter(list)
        binding.selectPv.setCenterDecoration(decoration)
        binding.selectPv.selectedPosition = position
        binding.selectPv.setOnSelectedListener(this)
    }

    fun cancel(view: View) {
        dismiss()
    }

    fun sure(view: View) {
        selectBack.selectPosition(position)
        dismiss()
    }

    interface SelectBack {
        fun selectPosition(position: Int)
    }

    class SelectAdapter(var data: ArrayList<String>) : WheelAdapter<String> {

        override fun getItemCount(): Int {
            return data.size
        }

        override fun getItem(index: Int): String {
            return data[index]
        }
    }
}

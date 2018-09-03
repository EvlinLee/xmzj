package com.gxtc.huchuan.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import cn.carbswang.android.numberpickerview.library.NumberPickerView
import com.gxtc.commlibrary.base.BaseDialogFragment
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.R

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/11/21.
 * 统一封装 类似时间选择器的 数据选择器
 */
class NumberPickerDialog() : BaseDialogFragment() {

    lateinit var tvTitle: TextView
    lateinit var tvOk: TextView
    lateinit var pickerView: NumberPickerView


    private var title = ""

    var onValueChangeListener: NumberPickerView.OnValueChangeListener? = null
    var datas: MutableList<PickerBean> ? = null

    constructor(title: String, datas: MutableList<PickerBean>) : this() {
        this.datas = datas
        this.title = title
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        width = (WindowUtil.getScreenWidth(context) * 0.7).toInt()
        return super.onCreateDialog(savedInstanceState)
    }

    override fun initView(): View {
        val view = View.inflate(context, R.layout.dialog_number_picker, null)
        tvTitle = view.findViewById(R.id.tv_ts_title) as TextView
        tvOk = view.findViewById(R.id.btn_ts_ok) as TextView
        pickerView = view.findViewById(R.id.picker_position) as NumberPickerView

        if(!TextUtils.isEmpty(this.title)){
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = this.title
        }

        val arrText = arrayOfNulls<String>(datas?.size!!)
        datas?.let {
            for(i in datas!!.indices){
                arrText[i] = datas!![i].text
            }
            pickerView.refreshByNewDisplayedValues(arrText)

            for(i in datas!!.indices){
                val bean = datas!!.get(i)
                if(bean.select){
                    pickerView.value = i
                }
            }

        }

        return view
    }

    override fun initListener() {
        pickerView.setOnValueChangedListener { picker, oldVal, newVal ->
            datas?.let {
                for(i in datas!!.indices){
                    val bean = datas!!.get(i)
                    bean.select = i == newVal
                }
            }

            onValueChangeListener?.onValueChange(picker, oldVal, newVal)
        }

        tvOk.setOnClickListener{
            onValueChangeListener?.onValueChange(pickerView, pickerView.value, pickerView.value)
            dismiss()
        }
    }

    fun setValue(index: Int){
        pickerView.value = index
    }

    fun show(activity: FragmentActivity) {
        super.show(activity.supportFragmentManager, "charge")
    }

    companion object {

        /**
         * @param index 默认选中角标
         * @param text 要展示的数据
         */
        @JvmStatic
        fun convertData(index: Int = 0, text: Array<String>): MutableList<PickerBean>{
            val datas = mutableListOf<PickerBean>()
            for(i in text.indices){
                val bean = PickerBean(text[i], index == i)
                datas.add(bean)
            }
            return datas
        }

    }

    data class PickerBean(var text: String = "", var select: Boolean = false)

}
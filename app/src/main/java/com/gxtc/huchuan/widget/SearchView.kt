package com.gxtc.huchuan.widget

import android.content.Context
import android.support.v7.widget.SearchView
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.R

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/11/4.
 */
class SearchView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
    : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener, TextWatcher, View.OnFocusChangeListener, TextView.OnEditorActionListener {

    var imgClose: ImageView? = null
    var editSearch: EditText? = null
    var tvHint: TextView? = null

    var onQueryTextListener: SearchView.OnQueryTextListener? = null
    var onCloseListener: SearchView.OnCloseListener? = null

    constructor(context: Context?)
            : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?)
            : this(context, attrs, 0)


    init {
        LayoutInflater.from(context).inflate(R.layout.view_search, this)
        val param = MarginLayoutParams(-1, -2)
        val margin = WindowUtil.dip2px(context, 5F)
        param.setMargins(margin, margin, margin, margin)
        layoutParams = param
        val padding = WindowUtil.dip2px(context, 5F)
        setPadding(padding, padding, padding, padding)
        isFocusable = true
        isFocusableInTouchMode = true

        initView()
    }

    private fun initView() {
        imgClose = findViewById(R.id.img_close) as ImageView
        editSearch = findViewById(R.id.edit_search) as EditText
        tvHint = findViewById(R.id.tv_search) as TextView

        imgClose?.setOnClickListener(this)
        editSearch?.setOnClickListener(this)

        reset()

        editSearch?.setOnEditorActionListener(this)
        editSearch?.onFocusChangeListener = this
        editSearch?.addTextChangedListener(this)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            inEdit()
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
            onQueryTextListener?.onQueryTextSubmit(editSearch?.text.toString())
            return false
        }
        return false
    }

    override fun afterTextChanged(s: Editable?) = Unit

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onQueryTextListener?.onQueryTextChange(s.toString())
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_close -> {
                reset()
                onCloseListener?.onClose()
            }
        }
    }

    private fun inEdit() {
        editSearch?.hint = "搜索"
        imgClose?.visibility = View.VISIBLE
        tvHint?.visibility = View.INVISIBLE
    }

    private fun reset() {
        editSearch?.hint = ""
        editSearch?.clearFocus()
        editSearch?.setText("")
        imgClose?.visibility = View.INVISIBLE
        tvHint?.visibility = View.VISIBLE
        WindowUtil.closeInputMethod(editSearch, context)
    }
}
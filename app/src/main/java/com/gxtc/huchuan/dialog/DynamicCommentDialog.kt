package com.gxtc.huchuan.dialog

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.gxtc.commlibrary.utils.ClickUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.customemoji.fragment.EmotionMainFragment
import com.gxtc.huchuan.customemoji.utils.GlobalOnItemClickManagerUtils
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.ui.circle.home.CircleHomeFragment.COMMENT_MAX_LENGHT
import com.gxtc.huchuan.utils.KeyboardUtils
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/1/4.
 * 动态评论弹窗
 * 使用
 * @see com.gxtc.commlibrary.base.BaseDialogFragment  之后表情面板框架不能加载
 */
class DynamicCommentDialog : DialogFragment(), View.OnClickListener, TextWatcher{

    private lateinit var imgFace: ImageView
    private lateinit var btn: TextView
    private lateinit var layoutFace: View
    private lateinit var layoutContent: View

    private var emotionMainFragment: EmotionMainFragment ? = null

    var edit: EditText ? = null

    var sendListener: OnSendListener ? = null

    var text = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_Translucent_full)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = View.inflate(context, R.layout.dialog_dynamic_comment, null)
        imgFace = view.findViewById(R.id.img_face) as ImageView
        edit = view.findViewById(R.id.edit_content) as EditText
        btn = view.findViewById(R.id.tv_send) as TextView
        layoutFace = view.findViewById(R.id.layout_face)
        layoutContent = view.findViewById(R.id.layout_content)

        edit?.addTextChangedListener(this)
        btn.setOnClickListener(this)
        btn.isEnabled = false
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEmotionMainFragment()
        edit?.isFocusable = true
        edit?.isFocusableInTouchMode = true

        if(!text.isEmpty()){
            edit?.hint = text
        }
    }


    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val param = dialog.window.attributes
            param.windowAnimations = com.gxtc.commlibrary.R.style.mypopwindow_anim_style
            param.gravity = Gravity.BOTTOM
            param.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            param.dimAmount = 0f
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
            dialog.setCanceledOnTouchOutside(true)
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tv_send -> {
                val content = edit?.text.toString().trim()
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showShort(context, "请输入评论内容")
                    return
                }

                if (content.trim().length > COMMENT_MAX_LENGHT) {
                    ToastUtil.showShort(context, resources.getString(R.string.comment_max_lenght))
                    return
                }

                if (sendListener != null) {
                    sendListener?.sendComment(edit?.text.toString())
                    btn.isEnabled = false
                    btn.setOnClickListener(null)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        val sub =
            Observable.timer(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if(edit != null){
                            edit?.requestFocus()
                            KeyboardUtils.showSoftInput(MyApplication.getInstance().applicationContext, edit)
                        }
                    })

        RxTaskHelper.getInstance().addTask(this, sub)
    }


    override fun afterTextChanged(s: Editable?) = Unit

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
            if(TextUtils.isEmpty(s)){
                btn.setOnClickListener(this)
                btn.isEnabled = false

            }else{
                btn.isEnabled = true
                btn.setOnClickListener(this)
            }


    /**
     * 初始化表情面板
     */
    private fun initEmotionMainFragment() {
        val bundle = Bundle()
        bundle.putBoolean(EmotionMainFragment.BIND_TO_EDITTEXT, false)               //绑定主内容编辑框
        bundle.putBoolean(EmotionMainFragment.HIDE_BAR_EDITTEXT_AND_BTN, true)       //隐藏控件
        bundle.putBoolean(EmotionMainFragment.IS_SHOW_EMOTION_BAR, false)
        emotionMainFragment = EmotionMainFragment.newInstance<EmotionMainFragment>(EmotionMainFragment::class.java, bundle)
        emotionMainFragment?.bindToContentView(edit)
        emotionMainFragment?.bindToEmotionButton(imgFace)
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.layout_face, emotionMainFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    override fun show(manager: FragmentManager?, tag: String?) {
        if(!ClickUtil.isFastClick() && manager?.findFragmentByTag(tag) == null){
            manager?.beginTransaction()?.add(this,tag)?.commitAllowingStateLoss()//不要使用父类的commit方法，容易在Activity因异常销毁而报错
        }
    }


    override fun dismiss() {
        val view = dialog?.currentFocus
        if (view is EditText) {
            val mInputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN)
        }
        edit?.removeTextChangedListener(this)
        GlobalOnItemClickManagerUtils.getInstance().attachToEditText(null)
        RxTaskHelper.getInstance().cancelTask(this)
        super.dismiss()
    }


    override fun onDestroy() {
        super.onDestroy()
        GlobalOnItemClickManagerUtils.getInstance().attachToEditText(null)
    }

    fun clearContent(){
        edit?.setText("")
        btn.isEnabled = true
        btn.setOnClickListener(this)
    }


    fun reset() {
        btn.isEnabled = true
        btn.setOnClickListener(this)
    }


    interface OnSendListener{
        fun sendComment(content: String)
    }
}
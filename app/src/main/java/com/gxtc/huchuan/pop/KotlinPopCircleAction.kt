package com.gxtc.huchuan.pop

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.PopupWindow
import android.widget.TextView
import com.gxtc.huchuan.R
import java.security.AccessControlContext

/**
 * Created by zzg on 2017/8/22.
 */
class KotlinPopCircleAction() : PopupWindow(), OnClickListener{

    var isMy : Int? = null;
    var isTop : Int? = null;
    var isGood : Int? = null;
    var isforBident : Int? = null;
    var tvShieldDynamic: TextView? = null
    var tvShieldUser: TextView? = null
    var tvSetBest: TextView? = null
    var btnDelete: TextView? = null
    var dynamiReply: TextView? = null
    var tvTop: TextView? = null
    var tvForbident: TextView? = null
    var line: View? = null
    var lineReply: View? = null
    var deleteLine: View? = null
    var topLine: View? = null
    var dynamiLine: View? = null
    var conentView: View? = null
    var mClickListener : OnClickListener? = null

    constructor( mContext: Context, isMy:Int,isTop:Int,isforBident:Int,isGood:Int):this(){
        this.isMy = isMy
        this.isTop = isTop
        this.isforBident = isforBident
        this.isGood = isGood
        init(mContext)
        setView()
        setText()
    }

    constructor( mContext: Context):this(){
        init(mContext)
        setView()
    }

   private fun  init( mContext: Context){
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        conentView = inflater.inflate(R.layout.pop_circle_action,null)
        contentView = conentView
        tvShieldDynamic = conentView?.findViewById(R.id.tv_circle_copy) as TextView
        tvShieldUser = conentView?.findViewById(R.id.tv_circle_collect) as TextView
        tvSetBest = conentView?.findViewById(R.id.set_best) as TextView
        btnDelete = conentView?.findViewById(R.id.delete) as TextView
        tvTop = conentView?.findViewById(R.id.toTop) as TextView
        tvForbident = conentView?.findViewById(R.id.forbident_sent_dynamic) as TextView
        line = conentView?.findViewById(R.id.line)
        lineReply = conentView?.findViewById(R.id.line_reply)
        deleteLine = conentView?.findViewById(R.id.line_delete)
        topLine = conentView?.findViewById(R.id.line_top)
        dynamiLine = conentView?.findViewById(R.id.line_dynamic)
        dynamiReply = conentView?.findViewById(R.id.tv_reply)
        initListener()
        when(isMy){
            1 -> width = mContext.resources.getDimensionPixelOffset(R.dimen.px650dp)
            else -> width = mContext.resources.getDimensionPixelOffset(R.dimen.px300dp)
        }
        when(isforBident){
            1 -> tvForbident?.text = "解禁动态"
            else -> tvForbident?.text = "禁发动态"
        }
        when(isGood){
            1 -> tvSetBest?.text = "取消精华"
            else -> tvSetBest?.text = "设置精华"
        }
        height = mContext.resources.getDimensionPixelOffset(R.dimen.px78dp)
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())

    }

    private fun initListener(){
        tvShieldDynamic?.setOnClickListener(this)
        tvShieldUser?.setOnClickListener(this)
        tvSetBest?.setOnClickListener(this)
        btnDelete?.setOnClickListener(this)
        tvTop?.setOnClickListener(this)
        tvForbident?.setOnClickListener(this)
        dynamiReply?.setOnClickListener(this)
    }

    private fun setView(){
        when (isMy) {
             1 -> {
                tvSetBest?.setVisibility(View.VISIBLE)
                btnDelete?.setVisibility(View.VISIBLE)
                tvTop?.setVisibility(View.VISIBLE)
                tvForbident?.setVisibility(View.VISIBLE)//后台没有接口，暂时隐藏起来
                line?.setVisibility(View.VISIBLE)
                deleteLine?.setVisibility(View.VISIBLE)
                topLine?.setVisibility(View.VISIBLE)
                dynamiLine?.setVisibility(View.VISIBLE)//后台没有接口，暂时隐藏起来
            }
            else -> {
                tvSetBest?.setVisibility(View.GONE)
                btnDelete?.setVisibility(View.GONE)
                tvTop?.setVisibility(View.GONE)
                tvForbident?.setVisibility(View.GONE)
                line?.setVisibility(View.GONE)
                deleteLine?.setVisibility(View.GONE)
                topLine?.setVisibility(View.GONE)
                dynamiLine?.setVisibility(View.GONE)
            }
        }
    }

    fun setText(){
        when(isTop){
            1 -> tvTop?.text = "取消置顶"
            else -> tvTop?.text = "置顶"
        }
    }

    fun setisShowReply(isShowReply:Boolean){
        if(isShowReply){
            dynamiReply?.setVisibility(View.VISIBLE)
            lineReply?.setVisibility(View.VISIBLE)
        }else{
            dynamiReply?.setVisibility(View.GONE)
            lineReply?.setVisibility(View.GONE)
        }
    }

    fun setTextLeft(text:String){
        tvShieldDynamic?.text = text
    }

    fun setTextRight(text:String){
        tvShieldUser?.text = text
    }

    fun setOnClickListener(listener: OnClickListener){
        this.mClickListener = listener
    }

    fun setAtLocation(v:View,mContext:Context){
        var location = IntArray(2)
        v.getLocationOnScreen(location)
        var  dalta = (mContext.getResources().getDimensionPixelSize(R.dimen.px700dp) - v.getWidth()) / 2;
        showAtLocation(v, Gravity.CENTER or Gravity.TOP, location[0] - dalta, location[1] - 30)
    }

    override fun onClick(v: View?) {
        mClickListener?.onClick(v)
        dismiss()
    }
}
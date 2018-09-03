package com.gxtc.huchuan.ui.common

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import cn.forward.androids.utils.ImageUtils
import cn.hzw.graffiti.GraffitiListener
import cn.hzw.graffiti.GraffitiSelectableItem
import cn.hzw.graffiti.GraffitiText
import cn.hzw.graffiti.GraffitiView
import com.gxtc.commlibrary.utils.FileStorage
import com.gxtc.commlibrary.utils.FileUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.event.EventSelectFriendBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.dialog.GraffitiTextDialog
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.im.ui.ConversationActivity
import com.gxtc.huchuan.im.ui.ConversationListActivity
import com.gxtc.huchuan.im.utilities.OptionsPopupDialog
import com.gxtc.huchuan.utils.ImMessageUtils
import com.gxtc.huchuan.utils.RongIMTextUtil
import com.gxtc.huchuan.widget.ColorDotView
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.message.ImageMessage
import kotlinx.android.synthetic.main.activity_graffiti.*
import rx.Observable
import rx.schedulers.Schedulers
import java.io.File

/**
 * 图片编辑页面 伍玉南
 */
@Deprecated(message = "已经弃用，将此ui转到picture_library中去了")
class GraffitiActivity : AppCompatActivity(), View.OnClickListener {

    private val TAB_PEN = 1
    private val TAB_TEXT = 2
    private val TAB_MOSAIC = 3

    private var mColor = 0
    private var isSave = false
    private var savePath = ""

    private lateinit var colors: ArrayList<ColorDotView>

    private lateinit var graffitiView: GraffitiView

    private var textDialog: GraffitiTextDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graffiti)
        initView()
    }

    private fun initView() {
        img_pen.setOnClickListener(this)
        img_text.setOnClickListener(this)
        img_mosaic.setOnClickListener(this)
        img_undo.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
        btn_finish.setOnClickListener(this)

        colors = arrayListOf()
        for(i in 0..7){
            val id = resources.getIdentifier("color_dot$i", "id", packageName)
            val color = findViewById(id) as ColorDotView
            color.setOnClickListener{ view ->
                for(item in colors){
                    item.isSelected = false
                }
                view.isSelected = true
                mColor = (view as ColorDotView).color
                graffitiView.setColor(mColor)
            }
            colors.add(color)
        }
        mColor = colors[2].color
        colors[2].isSelected = true

        val path = intent.getStringExtra("data")
        val bitmap = ImageUtils.createBitmapFromPath(path, this)
        graffitiView = GraffitiView(this, bitmap, graffitiListener)
        graffitiView.setColor(mColor)

        layout_content?.addView(graffitiView)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_cancel -> { finish() }

            R.id.btn_finish -> { showSaveDialog() }

            R.id.img_pen -> {
                changeTab(TAB_PEN)
                tabToolsIn(layout_color_tools, true)
                graffitiView.pen = GraffitiView.Pen.HAND
            }

            R.id.img_text -> {
                changeTab(TAB_TEXT)
                tabToolsIn(layout_color_tools, true)
                graffitiView.pen = GraffitiView.Pen.TEXT
            }

            R.id.img_mosaic -> {
                changeTab(TAB_MOSAIC)
                tabToolsIn(layout_color_tools, false)
                graffitiView.pen = GraffitiView.Pen.MOSAIC
            }

            R.id.img_undo -> { graffitiView.undo() }
        }
    }


    private fun showSaveDialog() {
        val items = arrayOf("发送好友", "保存图片")
        val dialog = OptionsPopupDialog.newInstance(this, items).setOptionsPopupDialogListener { which ->
            when(which){
                0 -> {
                    if(UserManager.getInstance().isLogin(this)){
                        isSave = false
                        graffitiView.save()
                        ConversationListActivity.startActivity(this, ConversationActivity.REQUEST_SHARE_CONTENT, Constant.SELECT_TYPE_SHARE)
                    }
                }

                1 -> {
                    isSave = true
                    graffitiView.save()
                }
            }
        }
        dialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == Activity.RESULT_OK) {
            val bean = data?.getParcelableExtra<EventSelectFriendBean>(Constant.INTENT_DATA)
            val targetId = bean?.targetId
            val type = bean?.mType
            shareMessage(targetId, type, savePath,bean?.liuyan)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }


    //发送给好友
    private fun shareMessage(targetId: String?, type: Conversation.ConversationType?, url: String,liuyan: String?) {
        if(targetId != null && type != null){
            progress.visibility = View.VISIBLE
            val imgMessage = ImageMessage.obtain(Uri.fromFile(File(url)), Uri.fromFile(File(url)), true)
            val msg = ImMessageUtils.obtain(targetId, type, imgMessage)

            RongIM.getInstance().sendImageMessage(msg, null, null, object: RongIMClient.SendImageMessageCallback(){
                override fun onSuccess(p0: Message?) {
                    progress.visibility = View.GONE

                    //删除临时图片文件
                    Observable.just(savePath)
                            .subscribeOn(Schedulers.io())
                            .subscribe {
                                if(FileUtil.deleteFile(savePath)){
                                    //这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！
                                    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                                    val uri = Uri.fromFile(File(savePath))
                                    intent.data = uri
                                    sendBroadcast(intent)
                                }
                            }
                    if(!TextUtils.isEmpty(liuyan)){
                        RongIMTextUtil.relayMessage(liuyan,targetId,type)
                    }
                    ToastUtil.showShort(this@GraffitiActivity, "发送成功")

                    finish()
                }

                override fun onAttached(p0: Message?) {
                }

                override fun onProgress(p0: Message?, p1: Int) {
                    progress.setProgress(p1)
                }

                override fun onError(p0: Message?, p1: RongIMClient.ErrorCode?) {
                    progress.visibility = View.GONE
                    ToastUtil.showShort(this@GraffitiActivity, "分享消息失败: " + p1?.value)
                }
            })
        }
    }


    //保存编辑好的图片到手机
    private fun saveToDevice(bitmap: Bitmap){
        savePath = FileStorage.getImgCacheFile().toString() + System.currentTimeMillis() + ".jpg"
        com.gxtc.huchuan.utils.ImageUtils.saveImageToSD(this, savePath, bitmap, 100)
        if(isSave){
            ToastUtil.showLong(this, "图片已保存到: " + savePath)
        }
    }


    //底部工具栏的切换动画 ， true为入场动画  false 是出场动画
    private fun tabToolsIn(targetView: View, isIn: Boolean){
        if(isIn && targetView.visibility == View.VISIBLE){
            return
        }

        if(!isIn && targetView.visibility == View.INVISIBLE){
            return
        }

        val arrays = if(isIn) floatArrayOf(0f, 1f)else floatArrayOf(1f, 0f)
        val anim = ObjectAnimator.ofFloat(targetView, "alpha", *arrays)
        anim.duration = 150
        anim.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                if(!isIn){
                    targetView.visibility = View.INVISIBLE
                }
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                targetView.visibility = View.VISIBLE
            }
        })
        anim.start()
    }


    //切换画笔工具栏的选中图案
    private fun changeTab(index: Int){
        img_pen.setImageResource(if(index == TAB_PEN) R.drawable.compile_icon_bi_selected else R.drawable.compile_icon_bi)
        img_text.setImageResource(if(index == TAB_TEXT) R.drawable.compile_icon_wenzi_selected else R.drawable.compile_icon_wenzi)
        img_mosaic.setImageResource(if(index == TAB_MOSAIC) R.drawable.compile_icon_masaike_selected else R.drawable.compile_icon_masaike)
    }



    //显示添加文字弹窗
    private fun showCreateTextDialog(selectableItem: GraffitiSelectableItem?, x: Float, y: Float) {
        textDialog = GraffitiTextDialog()
        textDialog?.setOnTextListener(object: GraffitiTextDialog.OnTextListener{
            override fun textFinish(text: String) {
                addGraffitiText(selectableItem ,text, x, y)
            }
        })
        textDialog?.text = if(selectableItem == null) "" else (selectableItem as GraffitiText).text
        textDialog?.color = mColor
        textDialog?.show(supportFragmentManager, GraffitiTextDialog::class.java.simpleName)
    }


    private fun addGraffitiText(selectableItem: GraffitiSelectableItem?, text: String, x: Float, y: Float){
        val textSize = resources.getDimension(R.dimen.textSize_s)
        if(selectableItem == null){
            val graffitiText = GraffitiText(graffitiView.pen, text, textSize, graffitiView.color.copy(), 0, graffitiView.graffitiRotateDegree, x, y, graffitiView.originalPivotX, graffitiView.originalPivotY)
            graffitiView.addSelectableItem(graffitiText)
        }else{
            (selectableItem as GraffitiText).text = text
            graffitiView.invalidate()
        }
    }


    private var graffitiListener = object : GraffitiListener {
        override fun onSaved(bitmap: Bitmap?, bitmapEraser: Bitmap?) {
            bitmap?.let { saveToDevice(it) }
        }

        override fun onError(i: Int, msg: String?) {
            ToastUtil.showShort(this@GraffitiActivity, "发生未知错误:$i")
        }

        override fun onReady() {
            graffitiView.paintSize = 10f
        }

        override fun onSelectedItem(selectableItem: GraffitiSelectableItem?, selected: Boolean) {

        }

        override fun onCreateSelectableItem(pen: GraffitiView.Pen?, x: Float, y: Float) {
            if(pen == GraffitiView.Pen.TEXT){
                showCreateTextDialog(null, x, y)
            }
        }

    }


    companion object {
        @JvmStatic
        fun startActivity(activity: Activity, path: String){
            val intent = Intent(activity, GraffitiActivity::class.java)
            intent.putExtra("data", path)
            activity.startActivityForResult(intent, 101)
        }
    }

}

package com.gxtc.huchuan.ui.mine.collect

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cn.jzvd.JZMediaManager
import cn.jzvd.JZVideoPlayer
import cn.jzvd.JZVideoPlayerStandard
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.ConversationTextBean
import com.gxtc.huchuan.bean.event.EventSelectFriendBean
import com.gxtc.huchuan.helper.ShareHelper
import com.gxtc.huchuan.im.ui.ConversationActivity
import com.gxtc.huchuan.im.utilities.OptionsPopupDialog
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity
import com.gxtc.huchuan.utils.DateUtil
import kotlinx.android.synthetic.main.activity_cinversation_collect_detiel.*
import java.lang.Exception
import java.util.*

/**
 * Created by zzg on 2017/8/14.
 */
class ConversationCollectDeteilActivity : BaseTitleActivity() , View.OnClickListener{

    var headPic : ImageView? = null
    var tvInviteMember : TextView? = null
    var tvTime : TextView? = null
    var textviewContent : TextView? = null
    var imageviewContent : ImageView? = null
    var bean : ConversationTextBean? = null
    var type : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinversation_collect_detiel);
    }

    override fun initView() {
        super.initView()
        baseHeadView.showTitle("详情").showBackButton {
            finish()
        }
         headPic = findViewById(R.id.collect_image) as ImageView
         tvInviteMember = findViewById(R.id.tv_circle_info) as TextView
         tvTime = findViewById(R.id.time) as TextView
         textviewContent = findViewById(R.id.content)  as TextView
         imageviewContent = findViewById(R.id.iv_content)  as ImageView
         bean = intent.getSerializableExtra("bean") as ConversationTextBean;
         type = intent.getStringExtra("type")
    }

    override fun initListener() {
        super.initListener()
        imageviewContent?.setOnClickListener(this)
        textviewContent?.setOnLongClickListener {
            OptionsPopupDialog.newInstance(this@ConversationCollectDeteilActivity, arrayOf("复制"))
                    .setOptionsPopupDialogListener {
                        val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cmb.text = bean?.getContent()
                        ToastUtil.showShort(this@ConversationCollectDeteilActivity, "已复制")
                    }.show()
            true
        }
    }
    override fun initData() {
        super.initData()
        when(type){
            "6"-> {
                textviewContent?.visibility = View.VISIBLE
                imageviewContent?.visibility = View.GONE
                textviewContent?.text = (bean?.content)
                tvTime?.text ="收藏于${DateUtil.showTimeAgo(bean?.createTime)}"
                tvInviteMember?.text = bean?.userName
                ImageHelper.loadImage(applicationContext, headPic, bean?.userPic)
            }
            "7"-> {
                imageviewContent?.visibility = View.VISIBLE
                textviewContent?.visibility = View.GONE
                tvTime?.text = "收藏于${DateUtil.showTimeAgo(bean?.createTime)}"
                tvInviteMember?.text = bean?.userName
                ImageHelper.loadImage(applicationContext, headPic, bean?.userPic)
                setImageSise(bean?.content, imageviewContent)
                //ImageHelper.loadImage(applicationContext,imageviewContent, bean?.content, R.drawable.live_foreshow_img_temp)
            }

            //小视频
            "11"-> {
                layout_content?.visibility = View.GONE
                ImageHelper.loadImage(applicationContext, headPic, bean?.userPic)
                tvTime?.text = "收藏于${DateUtil.showTimeAgo(bean?.createTime)}"
                tvInviteMember?.text = bean?.userName
                setVideoSize()
            }
        }
    }

    private fun setImageSise(url: String?, imageView: ImageView?) {
        if (!url?.isEmpty()!! && imageView != null) {

            val whArray = bean?.title?.split("*")

            if (whArray != null && whArray.size >= 2) {
                val width = java.lang.Double.valueOf(whArray[0])!!
                val height = java.lang.Double.valueOf(whArray[1])!!
                val params = imageView.layoutParams

                val maxWidth = imageView.maxWidth
                val maxHeight = imageView.maxHeight

                if (width > height) {
                    val vw = maxWidth - imageView.paddingLeft - imageView.paddingRight
                    val scale = vw.toFloat() / width.toFloat()
                    if (width > maxWidth) {
                        val vh = Math.round(height * scale).toInt()
                        params.height = vh + imageView.paddingTop + imageView.paddingBottom
                        params.width = maxWidth

                    } else {
                        params.width = (scale * width).toInt()
                        params.height = (scale * height).toInt()
                    }

                } else {
                    val vh = maxHeight - imageView.paddingBottom - imageView.paddingTop
                    val scale = vh.toFloat() / height.toFloat()
                    if (height > maxHeight) {
                        val vw = Math.round(width * scale).toInt()
                        params.height = maxHeight
                        params.width = vw + imageView.paddingLeft + imageView.paddingRight

                    } else {
                        params.width = (scale * width).toInt()
                        params.height = (scale * height).toInt()
                    }
                }
                ImageHelper.loadImage(this, imageView, url)
                return
            }
        }
    }

    private fun setVideoSize() {
        val url = bean?.getTitle()
        try {
            if(!url.isNullOrEmpty()){
                val temp = url?.substring(url.indexOf("?") + 1, url.length)
                if(!temp.isNullOrEmpty() && temp!!.contains("*")){
                    val arr = temp.split("*")
                    if(arr.size >= 3){
                        var width = arr[0].toFloat()
                        var height = arr[1].toFloat()
                        val time = arr[2].toFloat()

                        val marginLeft = resources.getDimension(R.dimen.px180dp)
                        val marginRight = resources.getDimension(R.dimen.px180dp)
                        val size = FloatArray(2)
                        if (width < height) {
                            val videoWidth = WindowUtil.getScreenW(this).toFloat() - marginLeft - marginRight
                            //如果图片比设定最小宽度还小 就给最小宽度的值
                            if (width < videoWidth) {
                                width = videoWidth
                            }

                            val videoHeight = videoWidth * height / width
                            size[0] = videoWidth
                            size[1] = videoHeight

                        } else if (width == height) {
                            val videoWidth = resources.getDimension(R.dimen.px240dp)
                            size[0] = videoWidth
                            size[1] = videoWidth

                        } else {
                            val videoWidth = (WindowUtil.getScreenW(this).toFloat() - marginLeft - marginRight).toFloat()
                            val videoHeight = videoWidth * height / width
                            if (height < videoHeight) {
                                height = videoHeight
                            }
                            size[0] = videoWidth
                            size[1] = videoHeight
                        }

                        if (size.size >= 2 && play_video != null) {
                            val params = play_video.layoutParams as RelativeLayout.LayoutParams
                            params.width = size[0].toInt()
                            params.height = size[1].toInt()
                            initPlayer()
                        }
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            ToastUtil.showShort(this, "该视频地址不能播放")
            finish()
        }
    }

    private fun initPlayer() {
        if (play_video != null) {
            play_video.visibility = View.VISIBLE
            play_video.setUp(bean?.getContent(), JZVideoPlayerStandard.SCREEN_WINDOW_LIST, "", bean?.title)
            play_video.widthRatio = 0
            play_video.heightRatio = 0
            play_video.currentTimeTextView.visibility = View.INVISIBLE
            play_video.totalTimeTextView.visibility = View.GONE
            play_video.progressBar.visibility = View.INVISIBLE
            ImageHelper.loadImage(this, play_video.thumbImageView, bean?.getTitle(), R.color.grey_e5e5)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if("11" == type){
            JZMediaManager.instance().releaseMediaPlayer()     //释放视频资源
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (JZVideoPlayer.backPress()) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onClick(v: View?) {
       when(v?.id ){
        R.id.iv_content -> {
            val uris = ArrayList<Uri>()
            uris.add(Uri.parse(bean?.content))
            val intent = Intent(this, CommonPhotoViewActivity::class.java)
            intent.putExtra("photo", uris)
            intent.putExtra("position", 0)
            startActivity(intent)
        }
       }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //分享视频
        if (requestCode == ConversationActivity.REQUEST_SHARE_VIDEO && resultCode == Activity.RESULT_OK) {
            val bean = data?.getParcelableExtra<EventSelectFriendBean>(Constant.INTENT_DATA)
            if(bean != null){
                ShareHelper.builder!!.targetId(bean.targetId).type(bean.mType).liuyan(bean.liuyan).action(ConversationActivity.REQUEST_SHARE_VIDEO).toShare()
            }
        }
    }

}
package com.gxtc.huchuan.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CircleSignBean
import com.gxtc.huchuan.bean.SeriseCountBean
import com.gxtc.huchuan.utils.DateUtil
import org.jsoup.helper.DataUtil

/**
 * Created by zzg on 2017/12/20.
 */
class CircleSignAdater(context: Context, data:ArrayList<CircleSignBean>, res: Int,isHome:Boolean) : BaseRecyclerAdapter<CircleSignBean>(context,data,res) {

    var isHome:Boolean = false

    init {
        this.isHome = isHome
    }

    override fun getItemCount(): Int {
         //数据统计那里的新增用户列表只返回最多9条,点更多则查看全部的（原本最多返回10条，但是这个加载更多框架当大于等10条数据他会启动加载更多的，所以只返回9条）
        if(isHome){
            if (list != null && list.size > 0){
                if(list.size > 10){
                    return 9
                }else{
                    return  list.size
                }
            }else{
                return  0
            }
        }
        return super.getItemCount()
    }

    override fun bindData(holder: ViewHolder?, position: Int, bean: CircleSignBean?) {

        if(bean?.joinPrice != null && bean?.joinPrice != 0.0 ){
            holder?.setText(R.id.circle_status,"付费：￥ "+bean?.joinPrice.toString())
            holder?.getViewV2<TextView>(R.id.circle_status)?.setTextColor(context.resources.getColor(R.color.red))
        }else{
            holder?.setText(R.id.circle_status,"免费")
            holder?.getViewV2<TextView>(R.id.circle_status)?.setTextColor(context.resources.getColor(R.color.color_47abef))
        }
        if(!TextUtils.isEmpty(bean?.joinTime )){
            holder?.setText(R.id.start_time,"加入时间："+DateUtil.stampToDate(bean?.joinTime,"yyyy-MM-dd HH:mm:ss"))
        }else{
            holder?.getView(R.id.start_time)?.visibility = View.GONE
        }

        when(bean?.paySource){
            "0" -> holder?.setText(R.id.from,"来源：app")
            "1" -> holder?.setText(R.id.from,"来源：微信")
            "2" -> holder?.setText(R.id.from,"来源：网页")
            else ->  holder?.setText(R.id.from,"来源：其他")
        }
        holder?.setText(R.id.title,bean?.userName)?.setText(R.id.usercode,"新媒号："+bean?.userCode)
        ImageHelper.loadImage(context,holder?.getViewV2(R.id.image_head),bean?.userPic)
    }
}
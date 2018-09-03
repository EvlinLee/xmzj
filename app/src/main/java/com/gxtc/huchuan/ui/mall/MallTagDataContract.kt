package com.gxtc.huchuan.ui.mall

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUserView
import com.gxtc.huchuan.bean.CategoryBean
import com.gxtc.huchuan.bean.MallBean
import java.util.HashMap

/**
 * Created by zzg on 2017/10/24.
 */
interface MallTagDataContract {
    interface view :BaseUserView<presenter>{
        fun showTagDatas(datas:ArrayList<CategoryBean>)
        fun showAddShopCarResule(datas:Any)
        fun showHeadIcon(datas: List<MallBean>)
        fun showActivitysData(datas: List<MallBean>)
    }
    interface presenter : BasePresenter{
        fun getTagDatas(isLoadMore:Boolean,categoryId:String)
        fun addShopCar(map: HashMap<String, String>)
        fun getTags(token: String)
        fun getActivitysData(token: String, start: Int)
    }
}
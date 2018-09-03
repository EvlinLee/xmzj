package com.gxtc.huchuan.data

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.CategoryBean
import com.gxtc.huchuan.bean.MallBean

/**
 * Created by zzg on 2017/10/24.
 */
interface MallSearchContract {

    interface view :BaseUiView<Presenter>{
         fun  showSearchData(datas:ArrayList<CategoryBean>)
         fun showAddShopCarResule(datas:Any)
         fun showActivitysData(datas: List<MallBean>)
         fun showHeadIcon(datas: List<MallBean>)
    }
    interface Presenter : BasePresenter{
         fun getSearchData(isLoadMore:Boolean,map:HashMap<String,String>);
         fun addShopCar(map: HashMap<String, String>)
         fun getActivitysData(token: String, start: Int)
         fun getTags(token: String)
    }
}
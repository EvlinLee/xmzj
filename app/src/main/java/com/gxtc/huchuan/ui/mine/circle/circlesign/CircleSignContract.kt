package com.gxtc.huchuan.ui.mine.circle.circlesign

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.CircleSignBean

/**
 * Created by zzg on 2017/12/20.
 */
class CircleSignContract {
    interface View : BaseUiView<Presenter>{
        fun showData(datas:ArrayList<CircleSignBean>)
    }

    interface Presenter :BasePresenter{
        fun getData(groupId:String,start:String,joinType:String,userName:String)
    }
}
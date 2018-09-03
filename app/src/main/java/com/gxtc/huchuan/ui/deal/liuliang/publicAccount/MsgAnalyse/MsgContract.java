package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.MsgAnalyse;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;

/**
 * Created by Steven on 17/3/1.
 */

public interface MsgContract {

    /**
     * 消息分析
     */
    interface AnalyseView extends BaseUserView<MsgContract.AnalysePresenter>{

    }

    interface AnalysePresenter extends BasePresenter{

    }


    /**
     * 消息关键字
     */
    interface WordView extends BaseUserView<MsgContract.WordPresenter>{

    }

    interface WordPresenter extends BasePresenter{

    }


    interface Source extends BaseSource{

    }

}

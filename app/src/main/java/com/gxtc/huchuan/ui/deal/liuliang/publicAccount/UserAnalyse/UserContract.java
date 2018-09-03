package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.UserAnalyse;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;

/**
 * Created by Steven on 17/2/27.
 */

public interface UserContract {

    /**
     * 用户增长
     */
    interface RiseView extends BaseUserView<UserContract.RisePresenter>{

    }

    interface RisePresenter extends BasePresenter{

    }


    /**
     * 用户属性
     */
    interface PropView extends BaseUserView<UserContract.PropPresenter>{

    }

    interface PropPresenter extends BasePresenter{

    }

    interface UserSource extends BaseSource{

    }


}

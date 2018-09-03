package com.gxtc.huchuan.ui.special;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.data.SpecialBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sjr on 2017/5/25.
 * 专题详情
 */

public class SpecialDetailContract {


    public interface View extends BaseUserView<SpecialDetailContract.Presenter> {

        void showData(SpecialBean bean);

        void showSubscription();

        void collectSpeciaSucc();
    }

    public interface Presenter extends BasePresenter {
        void getData();

        void getSubscription();

        void collectSpecia();
    }

    /**
     * model层接口 实现类还需要继承
     *
     * @see BaseRepository
     */
    public interface Source extends BaseSource {
        void getData(String id, ApiCallBack<SpecialBean> callBack);

        void getSubscription(String id, ApiCallBack<Void> callBack);

        void collectSpecia( String bizId, ApiCallBack<Object> callBack);
    }
}

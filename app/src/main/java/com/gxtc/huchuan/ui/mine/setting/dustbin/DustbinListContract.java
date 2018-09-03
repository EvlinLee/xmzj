package com.gxtc.huchuan.ui.mine.setting.dustbin;

import com.gxtc.commlibrary.BaseListPresenter;
import com.gxtc.commlibrary.BaseListUiView;
import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.bean.DustbinListBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.event.EventDustbinRecoverBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.mine.deal.issueList.IssueListContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class DustbinListContract {


    public interface View extends BaseUserView<DustbinListContract.Presenter>,BaseListUiView<DustbinListBean>{
        void showData(List<DustbinListBean> datas);
        void showRefreshFinish(List<DustbinListBean> datas);
        void showLoadMore(List<DustbinListBean> datas);
        void showNoMore();
        void showHuifuSuccess(int id);
    }

    public interface Presenter extends BasePresenter,BaseListPresenter {
        void getData(boolean isRefresh);
        void HuiFuDustbin(int id,int type);
        void loadMrore();

    }
    /**
     * model层接口 实现类还需要继承
     *
     * @see BaseRepository
     */
     public interface Source extends BaseSource {
        void getData(String token, int start, ApiCallBack<List<DustbinListBean>> callBack);

        //恢复回收相
        void HuiFuDustbin(String token,int id,int type, ApiCallBack<Object> callBack);
    }

}

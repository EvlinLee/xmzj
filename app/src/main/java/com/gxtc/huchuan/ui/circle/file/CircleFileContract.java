package com.gxtc.huchuan.ui.circle.file;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.io.File;
import java.util.List;

import io.rong.message.VoiceMessage;

/**
 * Created by sjr on 2017/2/15.
 * File
 */

public class CircleFileContract {

    /**
     * view层接口
     * 如果涉及到用户相关的 那么这个接口应该继承
     *
     * @see BaseUserView  区别在于多了个token过期的回调方法
     * <p>
     * 如果没涉及到用户相关的操作 那么这个接口直接继承
     * @see BaseUiView
     */
    public interface View extends BaseUserView<CircleFileContract.Presenter> {
        /**
         * 展示数据
         *
         * @param datas
         */
        void showData(List<CircleFileBean> datas);

        /**
         * 刷新结束
         *
         * @param datas
         */
        void showRefreshFinish(List<CircleFileBean> datas);

        /**
         * 加载更多
         *
         * @param datas
         */
        void showLoadMore(List<CircleFileBean> datas);

        void updateByDeleteCircleFile(int groupid, String fileId);


        void showFolderFile(int start,List<CircleFileBean> datas);

        void showQueryFile(int start,List<CircleFileBean> datas);

        /**
         * 没有更多数据
         */
        void showNoMore();

        void showSaveCirccleFileResult(String message);
    }

    /**
     * presenter层接口
     */
    public interface Presenter extends BasePresenter {
        /**
         * @param isRefresh 是否刷新
         */
        void getData(boolean isRefresh);

        void queryFile(String keyWord,Integer groupId, Integer folderId, Integer start, Integer pageSize);

        void getFolderFile(int folderid, int start);

        void refreshData(int folderId);

        void loadMrore();

        void deleteCircleFile(String token, int groudId, String fileId);

        void saveCircleFile(String token, Integer circleId, File file, String fileUrl, Integer type,
                Integer folderId);
    }

    /**
     * model层接口 实现类还需要继承
     *
     * @see BaseRepository
     */
    public interface Source extends BaseSource {
        void getData(String token ,int groudId, int start, ApiCallBack<List<CircleFileBean>> callBack);

        void deleteCircleFile(String token, int groudId, String fileId, ApiCallBack<Void> callBack);

        void getFolderFile(int groupId,String token,int folderId, int start, ApiCallBack<List<CircleFileBean>> callBack);

        void queryFile(String token ,String keyWord,Integer groupId, Integer folderId, Integer start, Integer pageSize, ApiCallBack<List<CircleFileBean>> callBack);


        void saveCircleFile(String token, Integer circleId, File file, String fileUrl, Integer type,
                Integer folderId, ApiCallBack<Void> callBack);
    }
}

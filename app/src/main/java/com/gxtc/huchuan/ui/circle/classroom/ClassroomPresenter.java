package com.gxtc.huchuan.ui.circle.classroom;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassAndSeriseBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.data.SearchRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/5/12.
 */

public class ClassroomPresenter implements ClassroomContract.Presenter {
    private static final String TAG = "ClassroomPresenter";

    private HashMap<String, String> map;
    private SearchRepository        mSearchData;
    private ClassroomContract.View  mView;
    private ClassroomRepository     source;

    private boolean isRefresh = true;

    private int start;
    private int searchStart = 0;

    public ClassroomPresenter(ClassroomContract.View view) {
        mView = view;
        view.setPresenter(this);
        source = new ClassroomRepository();
        map = new HashMap<>();
        mSearchData = new SearchRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        source.destroy();
        mSearchData.destroy();
        mView = null;
    }

    @Override
    public void getDatas(HashMap<String, String> map) {
        if (isRefresh) {
            start = 0;
        } else {
            mView.showLoad();
        }
        map.put("start", "" + start);
        source.getDatas(map, new ApiCallBack<List<ChatInfosBean>>() {
            @Override
            public void onSuccess(List<ChatInfosBean> data) {
                if (mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }
                mView.showLiveRoom(data);
            }


            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void getSeachData(boolean isLoadMore, String searchKey, String mCircleid) {
        if (!isLoadMore) {
            searchStart = 0;
        } else {
            searchStart = searchStart + 15;
        }
        map.put("token", UserManager.getInstance().getToken());
        map.put("type", "2");
        map.put("start", searchStart + "");
        map.put("searchKey", searchKey);
        map.put("groupId", mCircleid);
        mSearchData.getSearch(map, new ApiCallBack<List<SearchBean>>() {
            @Override
            public void onSuccess(List<SearchBean> data) {
                if (mView == null) return;
                if (data == null || data.size() == 0) {
                    ToastUtil.showShort(MyApplication.getInstance(), "暂时还没搜到相关的数据");
                    return;
                }
                mView.showSearChLiveRoom(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showError(message);
            }
        });
    }

    private boolean loadmroeIng = false;

    @Override
    public void loadmoreDatas(HashMap<String, String> map) {
        if (loadmroeIng) {
            return;
        }
        loadmroeIng = true;
        start += 15;
        map.put("start", "" + start);
        source.getDatas(map, new ApiCallBack<List<ChatInfosBean>>() {
            @Override
            public void onSuccess(List<ChatInfosBean> data) {
                if (mView == null) return;
                mView.showLoadFinish();
                loadmroeIng = false;
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }
                mView.showLiveRoom(data);
            }


            @Override
            public void onError(String errorCode, String message) {
                loadmroeIng = false;
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void getUnauditGroup(HashMap<String, String> map) {
        source.getUnauditGroup(map, new ApiCallBack<List<ClassAndSeriseBean>>() {
            @Override
            public void onSuccess(List<ClassAndSeriseBean> data) {
                if (mView == null) return;
                mView.showUnauditGroup(data);
            }


            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }


    @Override
    public void searchClass(String searchKey,int mCircleid,boolean isRefresh) {
        if(isRefresh){
            searchStart = 0;
        }else {
            searchStart = searchStart + 15;
        }
        map .put("token",UserManager.getInstance().getToken()) ;
        map .put("start",searchStart+"") ;
        map .put("searchKey",searchKey) ;
        map .put("groupId",mCircleid+"") ;
        source.searchClass(map, new ApiCallBack<List<ClassAndSeriseBean>>() {
            @Override
            public void onSuccess(List<ClassAndSeriseBean> data) {
                if (mView == null) return;
                if (searchStart == 0 && (data == null || data.size() == 0)) {
                    ToastUtil.showShort(MyApplication.getInstance(), "暂时还没搜到相关的数据");
                    return;
                }
                mView.showSearData(data);
            }


            @Override
            public void onError(String errorCode, String message) {
               ToastUtil.showShort(MyApplication.getInstance(),message);
            }
        });
    }

}

package com.gxtc.huchuan.data;

        import com.gxtc.commlibrary.data.BaseRepository;
        import com.gxtc.huchuan.bean.ClassMyMessageBean;
        import com.gxtc.huchuan.bean.dao.User;
        import com.gxtc.huchuan.http.ApiCallBack;
        import com.gxtc.huchuan.http.ApiObserver;
        import com.gxtc.huchuan.http.ApiResponseBean;
        import com.gxtc.huchuan.http.service.MineApi;
        import com.gxtc.huchuan.ui.mine.classroom.MyClassRoomContract;

        import java.util.List;

        import rx.android.schedulers.AndroidSchedulers;
        import rx.schedulers.Schedulers;

/**
 * Created by ALing on 2017/3/9 0009.
 */

public class MyClassRoomRepository extends BaseRepository implements MyClassRoomContract.Source {
    @Override
    public void getData(final int start, final ApiCallBack<List<ClassMyMessageBean>> callBack) {
        /*addSub(MineApi.getInstance().getUserInfo(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<User>>(callBack)));*/

    }



}

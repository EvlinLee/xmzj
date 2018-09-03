package com.gxtc.huchuan.ui.common;

import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.SparseArray;

import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.NetworkUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.helper.ShareHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.service.DownloadService;
import com.gxtc.huchuan.utils.StringUtil;

import java.net.SocketTimeoutException;
import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/2/9.
 * 公共的视频详情页面控制器
 */
public class CommonVideoPresenter implements CommonVideoContract.Presenter{

    private CommonVideoContract.View mView;
    private DealSource               mDealData;
    private String cover;
    private String url;

    public CommonVideoPresenter(CommonVideoContract.View view, String url, String cover) {
        mView = view;
        mView.setVideoPrsenter(this);
        mDealData = new DealRepository();
        this.cover = cover;
        this.url = url;
    }

    @Override
    public void saveVideo(Context context) {
        DownloadService.startDownload(context, url, DownloadService.TYPE_VIDEO);
    }

    @Override
    public void shareVideoToFriends(Activity activity) {
        if(UserManager.getInstance().isLogin(activity)){
            ShareHelper.INSTANCE.getBuilder().videoUrl(url).videoCover(cover);
            ConversationListActivity.startActivity(activity, ConversationActivity.REQUEST_SHARE_VIDEO, Constant.SELECT_TYPE_SHARE);
        }
    }


    //收藏视频  妈个鸡 收藏还要传视频的封面
    @Override
    public void collectVideo(final Activity activity) {
        if(UserManager.getInstance().isLogin(activity)){
            String                 bizType = "11";
            String                 content = url;
            String                 token   = UserManager.getInstance().getToken();
            String name = UserManager.getInstance().getUserName();
            String userPic = UserManager.getInstance().getHeadPic();

            final HashMap<String,String> map = new HashMap<>();
            map.put("content",content);
            map.put("bizType",bizType);
            if(!TextUtils.isEmpty(userPic))    map.put("userPic",userPic);
            if(!TextUtils.isEmpty(token))    map.put("token",token);
            if(!TextUtils.isEmpty(name))    map.put("userName",name);

            EventBusUtil.post(new EventLoadBean(true));
            Subscription sub =
            Observable.just(url)
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<String, SparseArray<String>>() {
                        @Override
                        public SparseArray<String> call(String s) {
                            MediaMetadataRetriever retr = new MediaMetadataRetriever();
                            retr.setDataSource(s, new HashMap<String, String>());
                            String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
                            String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);   // 视频宽度
                            String time = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);       // 视频时长
                            SparseArray<String> temp = new SparseArray<String>();
                            temp.put(0, width);
                            temp.put(1, height);
                            temp.put(2, time);
                            return temp;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(new Func1<SparseArray<String>, Boolean>() {
                        @Override
                        public Boolean call(SparseArray<String> stringSparseArray) {
                            if(TextUtils.isEmpty(cover)){
                                ToastUtil.showShort(activity, "收藏视频失败");
                                EventBusUtil.post(new EventLoadBean(false));
                                return false;
                            }else{
                                return true;
                            }
                        }
                    })
                    .subscribe(new Subscriber<SparseArray<String>>() {
                        @Override
                        public void onCompleted() {}

                        @Override
                        public void onError(Throwable e) {
                            EventBusUtil.post(new EventLoadBean(false));
                            if(e instanceof SocketTimeoutException){
                                ToastUtil.showShort(activity, "网络连接超时，请检查网络设置");
                            }else if(!NetworkUtil.isConnected(activity)) {
                                ToastUtil.showShort(activity, "未发现网络连接，请检查网络设置");
                            }else{
                                ToastUtil.showShort(activity, "收藏视频失败");
                            }
                        }

                        @Override
                        public void onNext(SparseArray<String> param) {
                            EventBusUtil.post(new EventLoadBean(false));
                            if(mView != null && param != null){
                                int width = StringUtil.toInt(param.get(0));
                                int height = StringUtil.toInt(param.get(1));
                                int time = StringUtil.toInt(param.get(2));
                                String title;
                                if(cover.contains("?")){
                                    title = cover + "*" + time;
                                }else{
                                    title = cover + "?" + width + "*" + height + "*" + time;
                                }
                                map.put("title", title);
                                saveCollect(map);
                            }
                        }
                    });

            RxTaskHelper.getInstance().addTask(this, sub);
        }
    }


    //保存收藏
    private void saveCollect(HashMap<String, String> map){
        mDealData.saveCollect(map, new ApiCallBack<Object>() {

            @Override
            public void onSuccess(Object data) {
                ToastUtil.showShort(MyApplication.getInstance().getBaseContext(),"收藏成功");
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance().getBaseContext(),"收藏失败");
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mView = null;
        RxTaskHelper.getInstance().cancelTask(this);
    }
}

package com.gxtc.huchuan.ui.deal.deal.goodsDetailed;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.GoodsCommentBean;
import com.gxtc.huchuan.bean.GoodsDetailedBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;


public class GoodsDetailedPresenter implements GoodsDetailedContract.Presenter{

    private GoodsDetailedContract.View  mView;
    private DealSource                  mData;

    private int start = 0;

    public GoodsDetailedPresenter(GoodsDetailedContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new DealRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }

    @Override
    public void getData(final String infoId) {
        String token = UserManager.getInstance().getToken();
        mView.showLoad();
        mData.getGoodsDetailed(token,infoId, new ApiCallBack<GoodsDetailedBean>() {
            @Override
            public void onSuccess(GoodsDetailedBean data) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showData(data);
                getComments(infoId);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void getComments(String infoId) {
        String token = UserManager.getInstance().getToken();
        mData.getComments(token, infoId,"0",new ApiCallBack<List<GoodsCommentBean>>() {
            @Override
            public void onSuccess(List<GoodsCommentBean> data) {
                if(mView == null)   return;
                if(data == null || data.size() == 0){
                    mView.showNoComments();
                    return;
                }

                mView.showComments(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void submitComments(String infoId, String content) {
        String token = UserManager.getInstance().getToken();
        mData.submitComment(token, infoId, content, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showSubmitSuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void replyComments(String commentId, String content,String targetUserId) {
        String token = UserManager.getInstance().getToken();
        mData.replyComment(token, commentId, content,targetUserId ,new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showReplySuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMore(String id) {
        String token = UserManager.getInstance().getToken();
        start += 15;
        mData.getComments(token, id,start+"",new ApiCallBack<List<GoodsCommentBean>>() {
            @Override
            public void onSuccess(List<GoodsCommentBean> data) {
                if(mView == null)   return;
                if(data == null || data.size() == 0){
                    mView.showNoLoadMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showNoComments();
            }
        });
    }


    @Override
    public void collect(int id) {
        String token = UserManager.getInstance().getToken();
        HashMap<String,String> map = new HashMap<>();
        map.put("token",token);
        map.put("bizType","3");
        map.put("bizId",id + "");
        mData.saveCollect(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showCollectSuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void deletComment(final int id) {
        String token = UserManager.getInstance().getToken();
        mData.deleteComment(token, id, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showDeletCommentSuccess(id);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void DZ(final int id) {
        String token = UserManager.getInstance().getToken();
        mData.DzComment(token, id, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showDZSuccess(id);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showError(message);
            }
        });
    }
}

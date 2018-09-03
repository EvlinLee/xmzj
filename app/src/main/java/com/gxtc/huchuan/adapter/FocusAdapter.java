package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.FocusBean;
import com.gxtc.huchuan.bean.event.EventFocusBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/3/13 .
 * 2017/4/6 自媒体时显示自媒体名 好友时显示好友名 通过id确认 4是自媒体
 */

public class FocusAdapter extends BaseRecyclerAdapter<FocusBean> {

    private Context mContext;
    //    private OnFocusListener listener;
    private int id;
    private boolean isFollow = true;

    Subscription sub;

    public FocusAdapter(Context context, List<FocusBean> list, int itemLayoutId, int id,
                        boolean isFollow) {
        super(context, list, itemLayoutId);
        this.mContext = context;
        this.id = id;
        this.isFollow = isFollow;
    }

    @Override
    public void bindData(ViewHolder holder, final int position, final FocusBean focusBean) {

        //头像
        ImageView imageView = (ImageView) holder.getView(R.id.riv_item_focus);
        ImageHelper.loadRound(mContext, imageView, focusBean.getUserHeadPic(),4);

        //姓名
        TextView tvName = (TextView) holder.getView(R.id.tv_item_focus_name);
        tvName.setText(focusBean.getUserName());



        final ImageView ivStatus = (ImageView) holder.getView(R.id.iv_item_focus_status);
        final TextView addFrends = (TextView) holder.getView(R.id.add_frends);
        TextView tvIntroduce = (TextView) holder.getView(R.id.tv_item_focus_introduce);
        if(id == 1 || id == 2 || id == 4){
            //介绍
            if (!"".equals(focusBean.getNewsTitle()))
                tvIntroduce.setText(focusBean.getNewsTitle());
            else
                tvIntroduce.setText("该用户暂无动态");

            ivStatus.setVisibility(View.VISIBLE);
            ivStatus.setTag(focusBean);
            //未关注
            if ("0".equals(focusBean.getIsFollow())) {
                if(id == 4){
                    addFrends.setVisibility(View.VISIBLE);
                    addFrends.setText("添加");
                }else {
                    ivStatus.setImageResource(R.drawable.live_attention_normal);
                }
                ivStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FocusBean focusBean1 = (FocusBean) ivStatus.getTag();
                        if (UserManager.getInstance().isLogin()) {
                            sub = AllApi.getInstance().setUserFollow(UserManager.getInstance().getToken(), "3", focusBean1.getUserCode())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                                            @Override
                                            public void onSuccess(Object data) {
                                                ivStatus.setImageResource(R.drawable.live_attention_selected);
                                                EventBusUtil.post(new EventFocusBean(true));
                                            }

                                            @Override
                                            public void onError(String errorCode, String message) {
                                                ToastUtil.showShort(mContext, message);
                                            }
                                        }));
                        }

                    }
                });
                addFrends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FocusBean focusBean1 = (FocusBean) ivStatus.getTag();
                        if (UserManager.getInstance().isLogin()) {
                            sub = AllApi.getInstance().setUserFollow(UserManager.getInstance().getToken(), "3", focusBean1.getUserCode())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                                            @Override
                                            public void onSuccess(Object data) {
                                                addFrends.setText("已添加");
                                                addFrends.setBackgroundResource(0);
                                                addFrends.setTextColor(context.getResources().getColor(R.color.text_color_999));
                                                EventBusUtil.post(new EventFocusBean(true));
                                            }

                                            @Override
                                            public void onError(String errorCode, String message) {
                                                ToastUtil.showShort(mContext, message);
                                            }
                                        }));
                        }

                    }
                });

            //已关注
            } else {
                if ("3".equals(focusBean.getType())){
                    ivStatus.setImageResource(R.drawable.live_attention_normal);
                } else{
                    ivStatus.setImageResource(R.drawable.live_attention_selected);
                }
            }
        }


        if(id == 4){
            //添加朋友这里需要显示是否添加按钮
            if(isFollow){
                addFrends.setVisibility(View.GONE);
                ivStatus.setVisibility(View.VISIBLE);
                tvIntroduce.setVisibility(View.VISIBLE);
            }else{
                addFrends.setVisibility(View.VISIBLE);
                addFrends.setText("添加");
                addFrends.setBackgroundResource(R.drawable.shape_border_raido_accent);
                addFrends.setTextColor(context.getResources().getColor(R.color.colorAccent));
                ivStatus.setVisibility(View.GONE);
                tvIntroduce.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (sub != null && sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }

//    public void setOnFocusListener(OnFocusListener onFocusListener) {
//        this.listener = onFocusListener;
//    }


    //关注或者取消关注监听
//    public interface OnFocusListener {
//        void onFocus(int position, String userCode);
//    }

}

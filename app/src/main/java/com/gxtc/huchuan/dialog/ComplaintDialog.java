package com.gxtc.huchuan.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;


import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ComplaintAdapter;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.news.NewsCommentDialog;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by sjr on 2017/3/29.
 * 文章投诉
 */

public class ComplaintDialog extends Dialog implements View.OnClickListener {
    private final String mTitle;
    private final String mType;
    private TextView tvContent;
    private RecyclerView mRecyclerView;
    private TextView tvOk;

    String[] title = {"广告", "重复", "低俗", "标题夸张", "与事实不符", "内容质量差", "疑似抄袭"};
    private List<String> mDatas;
    private List<String> mSelectedDatas;
    private ComplaintAdapter mAdapter;

    private View view;
    private Context mContext;
    private Activity mActivity;
    private String id;
    Subscription sub;
    private TextView tvTitle;

    /**
     * @param activity
     * @param context
     * @param themeResId
     * @param id         文章id
     * @param title      标题
     * @param type       举报类型
     */
    public ComplaintDialog(Activity activity, @NonNull Context context, @StyleRes int themeResId,
                           String id, String title, String type) {
        super(context, R.style.dialog_Translucent);
        this.mActivity = activity;
        this.mContext = context;
        this.id = id;
        mTitle = title;
        mType = type;
        initView();
        initData();
    }

    private void initView() {
        view = LayoutInflater.from(mContext).inflate(R.layout.pop_complaint, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_complaint);
        tvContent = (TextView) view.findViewById(R.id.tv_complaint_content);
        tvOk = (TextView) view.findViewById(R.id.tv_complaint_ok);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        setContentView(view);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = mContext.getResources().getDisplayMetrics().widthPixels;
        view.setLayoutParams(layoutParams);
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mSelectedDatas = new ArrayList<>();
        for (String string : title)
            mDatas.add(string);
        tvTitle.setText(mTitle);
        mAdapter = new ComplaintAdapter(mContext, mDatas, R.layout.item_complaint);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnSelectedListener(new ComplaintAdapter.OnSelectedItemListener() {
            @Override
            public void onSelected(String msg, boolean isSelected) {
                if (isSelected)
                    mSelectedDatas.add(msg);
                else
                    mSelectedDatas.remove(msg);
            }
        });
        tvOk.setOnClickListener(this);
        tvContent.setOnClickListener(this);
    }

    NewsCommentDialog mCOmmentDialog;

    @Override
    public void onClick(View v) {
        mCOmmentDialog = new NewsCommentDialog(mContext);
        switch (v.getId()) {
            case R.id.tv_complaint_ok:
                if (mSelectedDatas.isEmpty() && mSelectedDatas.size() == 0) {
                    ComplaintDialog.this.dismiss();
                    return;
                }
                complaint(mSelectedDatas.toString());
                break;
            case R.id.tv_complaint_content:
                mCOmmentDialog.setOnSendListener(new NewsCommentDialog.OnSendListener() {
                    @Override
                    public void sendComment(String content) {
                        complaint(content);
                    }
                });
                mCOmmentDialog.show();
                break;
        }
    }

    /**
     * 投诉
     */
    private void complaint(String conten) {
        //到这里肯定是已经登录的

        sub = AllApi.getInstance().complaintArticle(UserManager.getInstance().getToken(), mType, id, conten)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(mContext, "投诉成功，我们将会对投诉内容进行审核");
                        ComplaintDialog.this.dismiss();
                        if (mCOmmentDialog.isShowing())
                            mCOmmentDialog.dismiss();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(mActivity, errorCode, message);
                    }
                }));
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (sub != null && sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }
}

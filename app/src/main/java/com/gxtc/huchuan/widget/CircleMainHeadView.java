package com.gxtc.huchuan.widget;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.StringUtil;

import rx.Subscription;

/**
 * Created by sjr on 2017/5/12.
 */

public class CircleMainHeadView extends LinearLayout {
    private com.gxtc.commlibrary.recyclerview.RecyclerView mRecyclerView;

    private MarqueeView mMarqueeView;
    private LinearLayout llNotice;
    private View line;
    private Context mContext;
    private TextView commission;

    private OnItemClickListener listener;

    private int circleId;
    private boolean isPreviews;
    private String notice;//圈子公告
    public View dotBest;
    public View dotChatInfo;
    public View dotClass;
    public View dotFile;

    public CircleMainHeadView(Context context, RecyclerView mRecyclerView, int circleId, String notice, boolean isPreviews) {
        super(context);
        this.mRecyclerView = mRecyclerView;
        this.mContext = context;
        this.circleId = circleId;
        this.notice = notice;
        this.isPreviews = isPreviews;

        initView();
    }

    public void ShowCommission(double com){
        if(com > 0) {
            String text1 = "本圈邀请好友有奖励," + "邀请一位获得" ;
            String text2 = StringUtil.formatMoney(2, com) + "元";
            SpannableString ss2 = new SpannableString(text1 + text2 + "佣金");
            TextAppearanceSpan dealSpan2 = new TextAppearanceSpan(mContext, R.style.circle_invite_text_color);
            ss2.setSpan(dealSpan2, text1.length(), text1.length()+text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            commission.setText(ss2);
        }
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.item_circle_main_head, mRecyclerView, false);

        mMarqueeView = (MarqueeView) view.findViewById(R.id.marquee_view);
        llNotice = (LinearLayout) view.findViewById(R.id.ll_notice);
        dotBest = view.findViewById(R.id.dragView_msg_best);
        dotChatInfo = view.findViewById(R.id.dragView_msg_chatinfo);
        dotClass = view.findViewById(R.id.dragView_msg_class);
        dotFile = view.findViewById(R.id.dragView_msg_file);
        line = view.findViewById(R.id.view_notice);
        commission=view.findViewById(R.id.tv_havecommission);
        //发表
        view.findViewById(R.id.ll_circle_main_head_fabiao).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastClick()) return;
                if(isPreviews){
                    ToastUtil.showShort(mContext,"加入圈子可以发表动态！");
                }else{
                    listener.onFaBiao();
                }
            }
        });

        //文章
        view.findViewById(R.id.ll_circle_main_head_article).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastClick()) return;
                if(isPreviews){
                    ToastUtil.showShort(mContext,"加入圈子可以查看更多内容哦！");
                }else{
                    listener.onArticle();
                }
            }
        });


        //课堂
        view.findViewById(R.id.ll_circle_main_head_classroom).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastClick()) return;
                if(isPreviews){
                    ToastUtil.showShort(mContext,"加入圈子可以查看更多内容哦！");
                }else{
                    listener.onClassRoom();
                }
            }
        });

        //文件
        view.findViewById(R.id.ll_circle_main_head_file).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastClick()) return;
                if(isPreviews){
                    ToastUtil.showShort(mContext,"加入圈子可以查看更多内容哦！");
                }else{
                    listener.onFile();
                }
            }
        });

        //邀请
        view.findViewById(R.id.btn_yaoqing).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastClick()) return;
                if(!isPreviews){
                    listener.onYaoQing();
                }
            }
        });

        initData();

        this.addView(view);
    }

    private void initData() {
        if (!TextUtils.isEmpty(notice)) {
            llNotice.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            TextView textView = new TextView(MyApplication.getInstance());
            textView.setTextColor(getResources().getColor(R.color.text_color_333));
            textView.setText(notice);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textSize_s));
            mMarqueeView.addViewInQueue(textView);
            mMarqueeView.setScrollSpeed(8);
            mMarqueeView.setScrollDirection(MarqueeView.RIGHT_TO_LEFT);
            mMarqueeView.setViewMargin(15);
            mMarqueeView.startScroll();

            //公告
            llNotice.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtil.isFastClick()) return;
                    listener.onNotice();
                }
            });

        } else {
            llNotice.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
    }

    public void removeRunable(){
        mMarqueeView.removeCallbacks(mMarqueeView); //把Runable干掉，不然造成内存泄漏
        mMarqueeView = null;
    }

    public void setOnItemCLickLisetner(OnItemClickListener itemListener) {
        this.listener = itemListener;
    }

    public interface OnItemClickListener {
        void onFaBiao();

        void onArticle();

        void onClassRoom();

        void onFile();

        void onNotice();

        void onYaoQing();
    }

    public void setNotice(String notice) {
        this.notice = notice;
        initData();
    }
}

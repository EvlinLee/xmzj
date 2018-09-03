package com.gxtc.huchuan.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.commlibrary.widget.MyRadioGroup;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.DealBannerAdapter;
import com.gxtc.huchuan.adapter.DealTabPageAdapter;
import com.gxtc.huchuan.bean.DealData;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.deal.deal.dealList.DealListActivity;
import com.gxtc.huchuan.ui.deal.guarantee.ApplyGuaranteeActivity;
import com.gxtc.huchuan.ui.live.search.NewSearchActivity;
import com.gxtc.huchuan.utils.AdClickUtil;
import com.gxtc.huchuan.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gubr on 2017/5/15.
 */

public class DealFragmentHeadView extends LinearLayout {

    private final ViewGroup mParentView;
    private final Context   mContext;

    @BindView(R.id.ll_deal_head_fbtz) LinearLayout     mLlCircleMainHeadFabiao;
    @BindView(R.id.ll_deal_head_wdtz) LinearLayout     mLlCircleMainHeadArticle;
    @BindView(R.id.ll_deal_head_jygl) LinearLayout     mLlCircleMainHeadClassroom;
    @BindView(R.id.ll_deal_head_jyhh) LinearLayout     mLlCircleMainHeadFile;
    @BindView(R.id.viewpager)         ViewPager        mViewpager;
    @BindView(R.id.radioGroup)        MyRadioGroup     mMyRadioGroup;
    @BindView(R.id.radiogroup_tab)    RadioGroup       mRadioGroup;
    @BindView(R.id.deal_radio_all)    RadioButton      mDealRadioAll;
    @BindView(R.id.radio_line1)       View             mRadioLine1;
    @BindView(R.id.deal_radio_sell)   RadioButton      mDealRadioSell;
    @BindView(R.id.radio_line2)       View             mRadioLine2;
    @BindView(R.id.deal_radio_buy)    RadioButton      mDealRadioBuy;
    @BindView(R.id.cb_deal_banner)    ConvenientBanner mCbDealBanner;
    @BindView(R.id.deal_count)        TextView         tvDealCount;
    @BindView(R.id.tv_info_count)     TextView         tvInfoCount;


    private DealTabPageAdapter tabAdapter;
    private List<NewsAdsBean>  mAdvertise;

    public DealFragmentHeadView(Context context, ViewGroup parentView) {
        super(context);
        mParentView = parentView;
        mContext = context;
        initView();
        initListener();
    }

    private void initListener() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.deal_radio_all:
                        mRadioLine1.setVisibility(GONE);
                        mRadioLine2.setVisibility(VISIBLE);
                        l.onCheckAll();
                        break;

                    case R.id.deal_radio_sell:
                        mRadioLine1.setVisibility(GONE);
                        mRadioLine2.setVisibility(GONE);
                        l.onCheckSell();
                        break;

                    case R.id.deal_radio_buy:
                        mRadioLine1.setVisibility(VISIBLE);
                        mRadioLine2.setVisibility(GONE);
                        l.onCheckBuy();
                        break;
                }
            }
        });
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.head_deal_tab, mParentView, false);
        ButterKnife.bind(this, view);
        LayoutParams params = (LayoutParams) mCbDealBanner.getLayoutParams();
        params.height = (int)WindowUtil.getScaleHeight(16f, 7.5f, getContext());
        addView(view);
    }


    @OnClick({R.id.ll_deal_head_fbtz, R.id.ll_deal_head_wdtz, R.id.ll_deal_head_jygl, R.id.ll_deal_head_jyhh, R.id.et_input_search, R.id.layout_danbao})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_deal_head_fbtz:
                l.onFBTZ();
                break;
            case R.id.ll_deal_head_wdtz:
                l.onWDTZ();
                break;
            case R.id.ll_deal_head_jygl:
                l.onJYGL();
                break;
            case R.id.ll_deal_head_jyhh:
                l.onYJHH();
                break;

            case R.id.et_input_search:
                NewSearchActivity.jumpToSearch(mContext, NewSearchActivity.TYPE_DEAL);
                break;

            //申请担保
            case R.id.layout_danbao:
                if(UserManager.getInstance().isLogin((Activity) mContext)){
                    Intent intent = new Intent(mContext, ApplyGuaranteeActivity.class);
                    intent.putExtra("createSource",ApplyGuaranteeActivity.CREATESOURCE_FROM_DEAL);
                    mContext.startActivity(intent);
                }
                break;
        }
    }

    public void setHeadDealTab(DealData data) {
        List<DealTypeBean> datas = data.getTypes();
        if (datas == null || datas.size() == 0) {
            LogUtil.i("交易tab数据为空");
            return;
        }
        //初始化tab数据
        List<List<DealTypeBean>> list  = new ArrayList<>();
        List<View>               views = new ArrayList<>();
        List<DealTypeBean>       arr   = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            if (i % 8 == 0) {
                arr = new ArrayList<>();
                arr.add(datas.get(i));

            } else {
                arr.add(datas.get(i));
                if (arr.size() == 8 || i == datas.size() - 1) {
                    list.add(arr);
                    views.add(View.inflate(getContext(), R.layout.page_head_tab, null));
                }
            }
        }

        tabAdapter = new DealTabPageAdapter(getContext(), views, list);
        mViewpager.setAdapter(tabAdapter);
        mMyRadioGroup.setCount(views.size(), mViewpager);

        tabAdapter.setOnItemClickListener(new DealTabPageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DealTypeBean bean, int position) {
                Intent intent = new Intent(getContext(), DealListActivity.class);
                intent.putExtra("title", bean.getTypeName());
                intent.putExtra(Constant.INTENT_DATA, bean);
                getContext().startActivity(intent);
            }
        });

        String dealCount = "";
        String infoCount = "";
        dealCount = StringUtil.formatMoney(2, StringUtil.Format10000(data.getTradeOrderNum() + "")) + "万";
        String s1 = "交易笔数 ";
        SpannableString ss1 = new SpannableString(s1 + dealCount);
        TextAppearanceSpan dealSpan = new TextAppearanceSpan(mContext, R.style.deal_tab_text_color);
        ss1.setSpan(dealSpan, s1.length(), s1.length() + dealCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDealCount.setText(ss1);

        infoCount = StringUtil.formatMoney(2, StringUtil.Format10000(data.getTradeInfoNum() + "")) + "万";
        String s2 = "帖子笔数 ";
        SpannableString ss2 = new SpannableString(s2 + infoCount);
        TextAppearanceSpan dealSpan2 = new TextAppearanceSpan(mContext, R.style.deal_tab_text_color);
        ss2.setSpan(dealSpan2, s2.length(), s2.length() + infoCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvInfoCount.setText(ss2);
    }


    public void setCbDealBanner(final List<NewsAdsBean> advertise) {
        mAdvertise = advertise;
        mCbDealBanner.startTurning(5000);
        mCbDealBanner.setPages(new CBViewHolderCreator<DealBannerAdapter>() {
            @Override
            public DealBannerAdapter createHolder() {
                return new DealBannerAdapter();
            }
        }, mAdvertise);
        mCbDealBanner.setPageIndicator(new int[]{R.drawable.news_icon_dot_small, R.drawable.news_icon_dot_big});
        mCbDealBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        mCbDealBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AdClickUtil.performClick((Activity) mContext, mAdvertise.get(position));
            }
        });
    }


    private DealHeadListener l;

    public void setDealHeadListener(DealHeadListener l) {
        this.l = l;
    }

    public interface DealHeadListener {
        void onFBTZ();

        void onWDTZ();

        void onJYGL();

        void onYJHH();

        void onCheckAll();

        void onCheckBuy();

        void onCheckSell();
    }

}

package com.gxtc.huchuan.ui.deal.deal.dealList;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.base.EmptyView;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.Deal1LevelAdapter;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.pop.PopDealType;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.mine.deal.issueDeal.IssueDealActivity;
import com.gxtc.huchuan.widget.AutoLinkTextView;
import com.zaaach.citypicker.CityPickerActivity;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.emoticon.AndroidEmoji;

/**
 * 交易列表
 */
public class DealListActivity extends BaseTitleActivity implements DealListContract.View,
        View.OnClickListener {

    private final int REQUEST_CODE = 1;

    private final int TAB_TYPE = 0x01;
    private final int TAB_UDEF = 0x02;
    private final int MAXLEN = 50;

    @BindView(R.id.btn_sub_type)    View               btnSubType;
    @BindView(R.id.btn_sub_udef)    View               btnSubUdef;
    @BindView(R.id.base_empty_area) View               layoutEmpty;
    @BindView(R.id.tv_sub_type)     TextView           tvSubType;
    @BindView(R.id.tv_sub_udef)     TextView           tvSubUdef;
    @BindView(R.id.rv_deallist)     RecyclerView       listView;
    @BindView(R.id.layout_tab)      LinearLayout       layoutTab;
    @BindView(R.id.swipe_deal)      SwipeRefreshLayout swipeLayout;

    private int              currTab;
    private TextView content;
    private ImageView        expandSatus;
    private boolean          isExpands;

    private PopDealType  typePop;
    private DealTypeBean typeBean;

    private EmptyView emptyView;
    private TextView  typeTextView;

    private Deal1LevelAdapter adapter;

    private DealListContract.Presenter mPresenter;
    private int correntPosition = -1;
    public String platDescripte;

    private List<DealTypeBean> subs;
    private List<DealTypeBean> udefs;
    private List<DealTypeBean> subsMap;

    private TimePickerView   timePickerView;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_list);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        String title = getIntent().getStringExtra("title");
        typeBean = (DealTypeBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);

        getBaseHeadView().showTitle(title);
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton("发布帖子",this);

        emptyView = new EmptyView(layoutEmpty);
        swipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);

        //这个适配器跟tab中的交易内容是一样的
        /*adapter = new DealListAdapter(this, new ArrayList<DealListBean>(),
                R.layout.deal_list_home_page);*/

        adapter = new Deal1LevelAdapter(this, new ArrayList<DealListBean>(),
                R.layout.deal_list_home_page);

        typePop = new PopDealType(this, R.layout.pop_deal_type);
    }

    @Override
    public void initListener() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.reLoadFinish();
                mPresenter.getData(true, typeBean.getId(),subsMap,udefs);
            }
        });
        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore(typeBean.getId(), subsMap,udefs);
            }
        });
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                correntPosition = position;
                Intent intent = new Intent(DealListActivity.this, GoodsDetailedActivity.class);
                intent.putExtra(Constant.INTENT_DATA, adapter.getList().get(position));
                startActivityForResult(intent,101);
            }
        });

    }

    @OnClick({R.id.btn_sub_type, R.id.btn_sub_udef})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            case R.id.headRightButton:
                if(!UserManager.getInstance().isLogin(this)) return;
                Intent intent = new Intent(this, IssueDealActivity.class);
                intent.putExtra("id",typeBean.getId());
                startActivityForResult(intent,99);
                break;

            case R.id.btn_sub_type:
                changeTab(TAB_TYPE);
                break;

            case R.id.btn_sub_udef:
                changeTab(TAB_UDEF);
                break;
        }
    }

    //筛选数据
    private void filterData() {
        mPresenter.filterData(typeBean.getId(),subsMap,udefs);
    }

    @Override
    public void initData() {
        new DealListPresenter(this);
        mPresenter.getPlateDescripte(typeBean.getId()+"");
        mPresenter.getType(typeBean.getId());
        mPresenter.getData(false, typeBean.getId(), subsMap,udefs);
    }

    @Override
    public void showData(List<DealListBean> datas) {
        adapter.notifyChangeData(datas);
        listView.setAdapter(adapter);
    }

    @Override
    public void showType(List<DealTypeBean> subs, List<DealTypeBean> udefs) {
        subsMap = new ArrayList<>();
        List<DealTypeBean.TypesBean> types = new ArrayList<>();
        DealTypeBean                 temp  = new DealTypeBean();
        temp.setName("类型");
        for (DealTypeBean bean : subs) {
            DealTypeBean.TypesBean typeBean = new DealTypeBean.TypesBean();
            typeBean.setTitle(bean.getTypeName());
            typeBean.setCode(bean.getId() + "");
            types.add(typeBean);
        }
        temp.setUdfType(6);
        temp.setTypes(types);
        if (subs != null && subs.size() != 0) {
            subsMap.add(temp);
        }

        for (DealTypeBean bean : udefs) {
            bean.setValues(bean.getValues());
        }

        this.subs = subs;
        this.udefs = udefs;

    }

    //下拉刷新
    @Override
    public void showRefreshFinish(List<DealListBean> datas) {
        emptyView.hideEmptyView();
        listView.notifyChangeData(datas, adapter);
    }

    //加载更多
    @Override
    public void showLoadMore(List<DealListBean> datas) {
        listView.changeData(datas, adapter);
    }

    //交易板块说明
    @Override
    public void showPlateDescripte(Object datas) {
        if(datas != null){
            platDescripte = (String) GsonUtil.getJsonValue(GsonUtil.objectToJson(datas),"platDescripte");
            if(!TextUtils.isEmpty(platDescripte)){
                View view = View.inflate(this,R.layout.activity_deal_list_header,null);
                listView.addHeadView(view);
                content = view.findViewById(R.id.content);
                expandSatus = view.findViewById(R.id.expand_satus);
                setContent();
                expandSatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //收起
                        if(isExpands){
                            expandSatus.setImageResource(R.drawable.deal_list_icon_xiangshang);
                            content.setEllipsize(TextUtils.TruncateAt.END);
                            isExpands = false;
                         //全部展开
                        }else {
                            expandSatus.setImageResource(R.drawable.deal_list_icon_xiangxia);
                            isExpands = true;
                        }
                        setContent();
                    }
                });
            }
        }
    }

    private void setContent(){
        content.setText(platDescripte);

        if (isExpands) {
            content.setFilters(new InputFilter[]{});
        } else {
            content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAXLEN)});
        }

        if (!TextUtils.isEmpty(platDescripte) && platDescripte.length() <= MAXLEN) {
            expandSatus.setVisibility(View.GONE);

        } else if (!TextUtils.isEmpty(platDescripte) && platDescripte.length() > MAXLEN) {
            expandSatus.setVisibility(View.VISIBLE);
            expandSatus.setImageResource(isExpands ? R.drawable.deal_list_icon_xiangshang:R.drawable.deal_list_icon_xiangxia);
            content.setText(platDescripte);
        } else{
            expandSatus.setVisibility(View.GONE);
        }
    }

    //没有加载更多数据
    @Override
    public void showNoMore() {
        listView.loadFinish();
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        swipeLayout.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        emptyView.showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_net_error));
    }


    @Override
    public void setPresenter(DealListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
                typeTextView.setText(city);
                for (DealTypeBean bean : udefs) {
                    int type = bean.getUdfType();
                    if (type == 1) {
                        //选择城市h
                        bean.setContent(city);
                        typePop.changeData(udefs);
                    }
                }
            }
        }

        if(requestCode == 99 && resultCode == Constant.ResponseCode.NORMAL_FLAG){
            listView.reLoadFinish();
            mPresenter.getData(true, typeBean.getId(),subsMap,udefs);
        }

        if(requestCode == 101 && resultCode == RESULT_OK){
            if(data != null){
                int commentCount = data.getIntExtra("commentCount",-1);
                int readCount = data.getIntExtra("readCount",-1);
                if(correntPosition != -1){
                    if(commentCount != -1){
                        adapter.getList().get(correntPosition).setLiuYan(commentCount+"");
                    }
                    if(readCount != -1){
                        adapter.getList().get(correntPosition).setRead(readCount+"");
                    }
                    listView.notifyItemChanged(correntPosition);
                }
            }
        }
    }

    private void showPop(List<DealTypeBean> beans) {
        if (typePop != null) {
            typePop.showPop(layoutTab);
            typePop.changeData(beans);
        }
    }

    /**
     * 点击筛选弹窗回调  更改选中状态
     * udefType = 0:文本，1：城市，2：时间，3，下拉，4，多选，5，单选
     */
    @Subscribe
    public void onEvent(EventClickBean bean) {
        String code = bean.action;

        if("-1".equals(code)){
            filterData();
            StringBuffer sb = new StringBuffer();
            for(DealTypeBean.TypesBean type : subsMap.get(0).getTypes()){
                if(type.isSelect()){
                    sb.append(type.getTitle()).append(",");
                }
            }
            if(sb.length() > 0){
                sb.delete(sb.length() - 1 ,sb.length());
            }else{
                sb.append("不限");
            }
            tvSubType.setText(sb.toString());
            return;
        }

        //选择自分类
        if (currTab == TAB_TYPE) {
            if (subsMap.size() <= 0) return;
            for (DealTypeBean.TypesBean type : subsMap.get(0).getTypes()) {
                if (type.getCode().equals(code)) {
                    if (type.isSelect()) {
                        type.setSelect(false);
                    } else {
                        type.setSelect(true);
                    }
                    break;
                }
            }
            typePop.changeData(subsMap);

        //商品自定义筛选
        } else {
            String s[] = code.split(",");
            code = s[0];
            String type = s[1];

            //编辑框
            if ("0".equals(type)) {
                EditText editText = (EditText) bean.bean;
                for (DealTypeBean typesBean : udefs) {
                    if (code.equals(typesBean.getTradeField())) {
                        typesBean.setContent(editText.getText().toString());
                        return;
                    }
                }
            }

            TextView v = (TextView) bean.bean;

            //选择时间
            if ("2".equals(type)) {
                typePop.closePop();
                typeTextView = v;
                showTimePop(type, code, v);
                return;
            }

            //选择城市
            if ("1".equals(type)) {
                typeTextView = v;
                startActivityForResult(new Intent(this, CityPickerActivity.class), REQUEST_CODE);
                return;
            }

            //多选
            for (int i = 0; i < udefs.size(); i++) {
                DealTypeBean typeBean = udefs.get(i);
                if (code.equals(typeBean.getTradeField())) {

                    //单选
                    if ("5".equals(type)) {
                        for (DealTypeBean.TypesBean typesBean : typeBean.getTypes()) {
                            if(typesBean.getCode().equals(code)) continue;
                            typesBean.setSelect(false);
                        }
                    }

                    for (DealTypeBean.TypesBean typesBean : typeBean.getTypes()) {
                        String text ;
                        try {
                            text = v.getText().toString().split(" ")[1];
                        }catch (Exception e){
                            text = v.getText().toString();
                        }
                        String temp = typesBean.getTitle();
                        //如果点击的是当前选中的item
                        if (temp.equals(text)) {
                            //选中状态改未选中
                            if (typesBean.isSelect()) {
                                typesBean.setSelect(false);

                            //未选中状态改选中
                            } else {
                                //取消掉其他选项
                                if("不限".equals(text)){
                                    for (DealTypeBean.TypesBean tempBean : typeBean.getTypes()) {
                                        tempBean.setSelect(false);
                                    }

                                //点其他选项则取消不限的选中状态
                                }else{
                                    if(typeBean.getTypes().get(0).isSelect()){
                                        typeBean.getTypes().get(0).setSelect(false);
                                    }
                                }
                                typesBean.setSelect(true);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            typePop.changeData(udefs);
        }
    }

    //显示选择时间弹窗
    private void showTimePop(final String type, final String code, final TextView textView) {
        if (timePickerView == null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            TimePickerView.Builder builder = new TimePickerView.Builder(this,
                    new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date, View v) {
                            String time = sdf.format(date);
                            typeTextView.setText(time);
                            for (DealTypeBean bean : udefs) {
                                if (code.equals(bean.getTradeField())) {
                                    bean.setContent(time);
                                    break;
                                }
                            }
                            changeTab(TAB_UDEF);
                        }
                    }).setType(TimePickerView.Type.YEAR_MONTH_DAY).setDate(
                    new Date()).setOutSideCancelable(true);

            timePickerView = new TimePickerView(builder);
        }
        timePickerView.show();
    }


    private void changeTab(int tab) {
        currTab = tab;
        Drawable unSelect = getResources().getDrawable(R.drawable.deal_icon_up);
        Drawable select   = getResources().getDrawable(R.drawable.deal_icon_down);
        unSelect.setBounds(0, 0, unSelect.getMinimumWidth(), unSelect.getMinimumHeight());
        select.setBounds(0, 0, select.getMinimumWidth(), select.getMinimumHeight());

        if (tab == -1) {
            tvSubType.setCompoundDrawables(null, null, unSelect, null);
            tvSubUdef.setCompoundDrawables(null, null, unSelect, null);
            tvSubType.setTextColor(getResources().getColor(R.color.text_color_333));
            tvSubUdef.setTextColor(getResources().getColor(R.color.text_color_333));
            return;
        }

        getBaseHeadView().getHeadRightButton().setVisibility(View.VISIBLE);

        if (tab == TAB_TYPE) {
            tvSubType.setCompoundDrawables(null, null, select, null);
            tvSubUdef.setCompoundDrawables(null, null, unSelect, null);
            tvSubType.setTextColor(getResources().getColor(R.color.colorAccent));
            tvSubUdef.setTextColor(getResources().getColor(R.color.text_color_333));
            showPop(subsMap);
        } else {
            tvSubUdef.setCompoundDrawables(null, null, select, null);
            tvSubType.setCompoundDrawables(null, null, unSelect, null);
            tvSubUdef.setTextColor(getResources().getColor(R.color.colorAccent));
            tvSubType.setTextColor(getResources().getColor(R.color.text_color_333));
            showPop(udefs);
        }

    }

}

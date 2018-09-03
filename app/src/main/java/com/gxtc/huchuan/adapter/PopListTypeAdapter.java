package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.widget.OtherGridView;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/26.
 */

public class PopListTypeAdapter extends BaseRecyclerTypeAdapter<DealTypeBean> {

    public PopListTypeAdapter(Context mContext, List<DealTypeBean> mDatas, int[] resId) {
        super(mContext, mDatas, resId);
    }

    //type   0:编辑框，1：城市，2：时间，3，下拉，4，多选，5，单选
    @Override
    protected int getViewType(int position) {
        DealTypeBean bean = getDatas().get(position);
        int type = bean.getUdfType();
        switch (type){
            case 0:
                return 0;

            case 1:
            case 2:
                return 1;

            case 3:
                return 1;

            case 4:
            case 5:
            case 6:
                return 2;

            default:
                return 2;
        }
    }

    @Override
    protected void bindData(RecyclerView.ViewHolder viewHolder, int position, int type, DealTypeBean bean) {
        BaseRecyclerTypeAdapter.ViewHolder holder = (ViewHolder) viewHolder;
        TextView tvName = (TextView) holder.getView(R.id.tv_name);
        OtherGridView gridView = (OtherGridView) holder.getView(R.id.gridview);

        String name = bean.getName();
        tvName.setText(name);


        //文本输入筺样式
        if(type == 0){
            EditText editText = (EditText) holder.getView(R.id.edit_content);
            editText.setTag(bean.getTradeField() + "," + bean.getUdfType());
            editText.addTextChangedListener(new EditTextWatcher(editText));
            String content = bean.getContent();
            if(TextUtils.isEmpty(content)){
                editText.setText("");
            }else{
                editText.setText(content);
            }
        }

        //按钮选择样式
        if(type == 1){
            TextView btn = (TextView) holder.getView(R.id.tv_content);
            String content = bean.getContent();
            if(TextUtils.isEmpty(content)){
                btn.setText("不限");
            }else{
                btn.setText(content);
            }

            btn.setTag(bean.getTradeField() + "," + bean.getUdfType());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String code = (String) v.getTag();
                    EventBusUtil.post(new EventClickBean(code,v));
                }
            });
        }

        //网格选择样式
        if(type == 2){
            List<DealTypeBean.TypesBean> types = bean.getTypes();
            PopGridTypeAdapter           adapter = new PopGridTypeAdapter(getmContext(),types,R.layout.item_grid_deal_type);
            gridView.setAdapter(adapter);
        }


    }


    private class EditTextWatcher implements TextWatcher{

        private EditText mEditText;

        public EditTextWatcher(EditText editText) {
            mEditText = editText;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            afterTextChanged(mEditText,s);
        }

        public void afterTextChanged(EditText edit, Editable s) {
            String code = (String) edit.getTag();
            EventBusUtil.post(new EventClickBean(code,edit));
        }

    }

}

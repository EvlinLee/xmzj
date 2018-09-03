package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gxtc.huchuan.R;

import java.util.List;


public class ListDialogAdapterV1 extends BaseAdapter{
    List<String> datas;
    int lacation = -1;
    int itemLayoutId ;
    Context mcontext;
     ChoosedListenner mChoosedListenner;
     ChoosedTextListenner mChoosedTextListenner;

    public void setmChoosedListenner(ChoosedListenner mChoosedListenner) {
        this.mChoosedListenner = mChoosedListenner;
    }
    public void setmChoosedTextListenner(ChoosedTextListenner mChoosedListenner) {
        this.mChoosedTextListenner = mChoosedListenner;
    }

    public ListDialogAdapterV1(Context context, List<String> datas, int itemLayoutId) {
        super();
        mcontext=context;
        this.itemLayoutId=itemLayoutId;
        this.datas=datas;
    }
    public interface ChoosedListenner{
        public void OnChooseItem(int position);
    }
    public interface ChoosedTextListenner{
        public void OnChooseTextItem(int position,String text);
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public void bindData(ViewHolder holder, final String s, final int position) {
        TextView             tv          = (TextView) holder.getView(R.id.tv_sync_issue_npublic);
        AppCompatRadioButton radioButton = (AppCompatRadioButton) holder.getView(R.id.cb_sync_issue_public);
        tv.setText(s);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChick(position);
                if(mChoosedListenner != null)
                mChoosedListenner.OnChooseItem(position);
                if(mChoosedTextListenner != null)
                mChoosedTextListenner.OnChooseTextItem(position,s);
            }
        });
        if(lacation != -1){
            if(lacation == position){
                radioButton.setChecked(true);
            }else {
                radioButton.setChecked(false);
            }
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder =null;
        if(convertView == null){
            convertView = LayoutInflater.from(mcontext).inflate(itemLayoutId, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        bindData(holder,datas.get(position),position);
        return convertView;
    }
    public class ViewHolder {

        private ArrayMap<Integer, View> viewMap;
        private View itemView;

        public ViewHolder(View rootView) {
            this.itemView = rootView;
            viewMap = new ArrayMap<Integer, View>();
        }
        /*
        * 从集合中获取view，没有则重新findId
        */
        public View getView(int viewId){
            View view = viewMap.get(viewId);
            if(view == null){
                view = itemView.findViewById(viewId);
                viewMap.put(viewId, view);
            }
            return view;
        }

        public View getItemView(){
            return itemView;
        }
    }

    public void setChick(int position){
        lacation=position;
        notifyDataSetChanged();
    }

}

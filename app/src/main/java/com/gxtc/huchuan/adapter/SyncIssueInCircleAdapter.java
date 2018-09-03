package com.gxtc.huchuan.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.event.EventCollectSelectBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.utils.DialogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/5/18.
 *
 */

public class SyncIssueInCircleAdapter extends BaseRecyclerAdapter<MineCircleBean> {

    public final static int MAX_SELECT = 2;     //最多可选择圈子数量

    private int id;

    private int type = -1;
    private String linkId ;

    private AlertDialog dialog;

    private LinkedList<MineCircleBean> checkeList;
    private List<Integer> checkeList2;
    private Activity activity;

    public SyncIssueInCircleAdapter(Activity context, List<MineCircleBean> list, int itemLayoutId, int id) {
        super(context, list, itemLayoutId);
        this.id = id;
        checkeList = new LinkedList<>();
        checkeList2 = new ArrayList<>();
        this.activity=context;
    }

    @Override
    public void bindData(ViewHolder holder, int position, MineCircleBean mineCircleBean) {
        TextView textView = (TextView) holder.getView(R.id.tv_sync_issue_name);
        textView.setText(mineCircleBean.getGroupName());
        TextView tvHit = (TextView) holder.getView(R.id.tv_hint);

        final CheckBox checkBox = (CheckBox) holder.getView(R.id.cb_sync_issue);
        checkBox.setTag(mineCircleBean);

        if(mineCircleBean.isNoCheck()){
            checkBox.setEnabled(false);
        }else{
            checkBox.setEnabled(true);
        }

        if(mineCircleBean.isCheck()){
            checkBox.setChecked(true);
        }else{
            checkBox.setChecked(false);
        }

        if(mineCircleBean.isFirst()){
            tvHit.setVisibility(View.VISIBLE);
        }else{
            tvHit.setVisibility(View.INVISIBLE);
        }

        if (id == mineCircleBean.getId()) {
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        } else {

            final MineCircleBean tempBean = mineCircleBean;
            final int tempPosition = position;
            final ViewHolder tempHolder = holder;

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tempBean.isCheck()) {
                        MineCircleBean mineCircleBean = (MineCircleBean) v.getTag();
                        if(checkeList2.contains(mineCircleBean.getId()) && type != -1)
                           removeTopicDialog(tempHolder,tempPosition,mineCircleBean);
                        else {
                            MineCircleBean bean = (MineCircleBean) v.getTag();
                            EventBusUtil.post(new EventCollectSelectBean(false, tempPosition, tempHolder));
                            bean.setCheck(false);
                            removeChckList(bean);
                        }
                    } else {
                        MineCircleBean bean = (MineCircleBean) v.getTag();
                        EventBusUtil.post(new EventCollectSelectBean(true, tempPosition, tempHolder));
                        bean.setCheck(true);
                        addChckList(bean);
                    }
                    checkCount();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void checkCount (){
        int count = 0 ;
        for(MineCircleBean bean : getList()){
            if(bean.isCheck()){
                count++;
            }
        }

        if(count >= MAX_SELECT){
            for(MineCircleBean bean : getList()){
                if(!bean.isCheck()){
                    bean.setNoCheck(true);
                }
            }

        }else{
            for(MineCircleBean bean : getList()){
                if(bean.isNoCheck()){
                    bean.setNoCheck(false);
                }
            }
        }

    }
    private void removeTopicDialog(final ViewHolder tempHolder, final int position, final MineCircleBean bean) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = DialogUtil.showInputDialog(activity, false, "", "确认取消该圈子同步？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.getInstance().isLogin()) {
                            CancelGroup(bean.getId(),tempHolder,position,bean);
                            EventBusUtil.post(new EventCollectSelectBean(false, position, tempHolder));
                            bean.setCheck(false);
                            removeChckList(bean);
                            checkCount();
                            notifyDataSetChanged();

                        } else {
                            Toast.makeText(getContext(), "登录后才能操作", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    private void removeChckList(MineCircleBean bean){
        if(checkeList.size() > 0){
            MineCircleBean fastBean = checkeList.get(0);

            //如果移出的是第一个选中的圈子，那么就把isFirst属性赋给下一个
            if(fastBean.equals(bean)){
                fastBean.setFirst(false);

                if(checkeList.size() >= 2){
                    checkeList.get(1).setFirst(true);
                }
            }
            checkeList.remove(bean);
        }
    }

    private void addChckList(MineCircleBean bean){
        if(checkeList.size() == 0){
            bean.setFirst(true);
        }
        checkeList.add(bean);
    }

    public void setIds(ArrayList<Integer> ids,int type,String linkId){

        if (ids != null && ids.size() > 0) {
            this.type = type;
            this.linkId = linkId;
            checkeList2 = ids;
            for (Integer integer : ids) {
                for (MineCircleBean bean : getList()) {
                    if (bean.getId() == integer) {
                        checkeList.add(bean);
                    }
                }
            }
        }
    }

    //取消圈子同步 同步类型1、文章；2、课堂；3、文件；6.系列课
    private void CancelGroup(final int groupids,final ViewHolder tempHolder, final int position, final MineCircleBean bean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("groupId", groupids+"");
        map.put("linkId", linkId + "");
        if(type == 1){
            map.put("type", "2");
        }
        else if(type == 2){
            map.put("type", "6");
        }
        else if(type == 3){
            map.put("type","1");
        }

        Subscription sub = MineApi.getInstance().deleteToGroup(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        checkeList2.remove((Integer) groupids);
                        ToastUtil.showShort(getContext(),"取消成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(getContext(), message);
                        EventBusUtil.post(new EventCollectSelectBean(true, position, tempHolder));
                        bean.setCheck(true);
                        addChckList(bean);
                        checkCount();
                        notifyDataSetChanged();
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }

}

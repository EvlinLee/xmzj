package com.gxtc.huchuan.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.BannedOrBlackUserBean;
import com.gxtc.huchuan.bean.ChatJoinBean;
import com.gxtc.huchuan.bean.SignUpMemberBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.pop.PopManagerMember;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.widget.MyActionSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gubr on 2017/4/2.
 */

public class MemeberManagerAdapter extends BaseRecyclerAdapter<ChatJoinBean.MemberBean> {
    private static final String TAG = "MemeberManagerAdapter";
    private final Activity context;
    private PopManagerMember popManagerMember;
    private HashMap<String, String> myBean;
    private RecyclerView recyclerView;


    public MemeberManagerAdapter(Activity context, List<ChatJoinBean.MemberBean> list, int itemLayoutId, HashMap<String, String> myBean, RecyclerView recyclerView) {
        super(context, list, itemLayoutId);
        this.context = context;
        this.myBean = myBean;
        this.recyclerView =recyclerView;
    }

    @Override
    public void bindData(ViewHolder holder, final int position, final ChatJoinBean.MemberBean bean) {
        ImageHelper.loadCircle(getContext(), holder.getImageView(R.id.iv_head), bean.getHeadPic());
        holder.setText(R.id.tv_name, bean.getName());

        TextView manage = holder.getViewV2(R.id.tv_role);
        switch (bean.getJoinType()){
            case "1":
                manage.setText("管理员");

                break;
            case "2":
                manage.setText("讲师");

                break;
            case "3":
                manage.setText("主持人");

                break;
            case "0":
                manage.setText("普通成员");
                break;
        }

        //隐藏管理按钮
        if (myBean.get("joinType") .equals( ChatJoinBean.ROLE_TEACHER) || myBean.get("joinType").equals(ChatJoinBean.ROLE_ORDINARY) || UserManager.getInstance().getUserCode() .equals( bean.getUserCode()) ||
        myBean.get("joinType") .equals(ChatJoinBean.ROLE_MANAGER) && (bean.getJoinType().equals( ChatJoinBean.ROLE_HOST) || bean.getJoinType() .equals(ChatJoinBean.ROLE_MANAGER))) {
            holder.getViewV2(R.id.tv_manage).setVisibility(View.GONE);
        } else {
            holder.getViewV2(R.id.tv_manage).setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowBottomMenu(bean, position);

                }
            });
        }
    }


    String flagStr = null;
    boolean flag = false;



    public void ShowBottomMenu(final ChatJoinBean.MemberBean bean, final int mPosition) {
        if (bean.getUserCode().equals(UserManager.getInstance().getUserCode())) return;
        final List<String> itemList = new ArrayList<>();
        if (!bean.getJoinType().equals(ChatJoinBean.ROLE_MANAGER) && bean.getJoinType().equals(ChatJoinBean.ROLE_HOST)) {
            itemList.add("升级为管理员");
        }else if(bean.getJoinType().equals(ChatJoinBean.ROLE_MANAGER) && bean.getJoinType().equals(ChatJoinBean.ROLE_HOST)){
            itemList.add("取消管理员");
        }
        if (!bean.getJoinType().equals(ChatJoinBean.ROLE_TEACHER)) {
            itemList.add("升级为讲师");
        }else{
            itemList.add("取消讲师");
        }
        if(bean.isProhibitSpeaking()) {
            itemList.add("解禁");
        }else{
            itemList.add("禁言");
        }
        if (bean.isBlacklist())
            itemList.add("取消黑名单");
        else
            itemList.add("加入黑名单");

        String[] s = new String[itemList.size()];

        if (itemList.size() == 0) return;
        final MyActionSheetDialog dialog = new MyActionSheetDialog(context,
                itemList.toArray(s), null);
        dialog.isTitleShow(false).titleTextSize_SP(14.5f).widthScale(1f).cancelMarginBottom(
                0).cornerRadius(0f).dividerHeight(1).itemTextColor(
                context.getResources().getColor(R.color.black)).cancelText("取消").show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    switch (itemList.get(position)) {
                        case "升级为管理员":
                            updateJoin(bean.getUserCode(), "1","升级管理员成功", mPosition);
                            break;
                        case "取消管理员":
                            updateJoin(bean.getUserCode(), "0","取消管理员成功", mPosition);
                            break;
                        case "升级为讲师":
                            updateJoin(bean.getUserCode(), "2","升级讲师成功", mPosition);
                            break;
                        case "取消讲师":
                            updateJoin(bean.getUserCode(), "0","取消讲师成功", mPosition);
                            break;
                        case "取消黑名单":
                            doJoinMemberBlacklistOrProhibitSpeaking(bean, "1", "0", mPosition,"已取消黑名单");
                            break;
                        case "加入黑名单":
                            doJoinMemberBlacklistOrProhibitSpeaking(bean, "1","1", mPosition,"已加入黑名单");
                            break;
                        case "禁言":
                            doJoinMemberBlacklistOrProhibitSpeaking(bean, "2","1", mPosition,"已禁言");
                            break;
                        case "解禁":
                            doJoinMemberBlacklistOrProhibitSpeaking(bean, "2","0", mPosition,"已解禁");
                            break;
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //修改成员身份
    private void updateJoin(String userCode, final String userType, final String msg, final int postion){
        HashMap<String, String> map = new HashMap<>();
        map.put("chatId", myBean.get("chatId"));
        map.put("type", myBean.get("type"));
        map.put("userCode", userCode);
        map.put("token", UserManager.getInstance().getToken());
        map.put("userType", userType);

        LiveApi.getInstance().updateJoinMemberJoinType(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new
                        ApiObserver<ApiResponseBean<Object>>
                        (new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                ChatJoinBean.MemberBean  bean = getList().get(postion);
                                bean.setJoinType(userType);
                                recyclerView.notifyItemChanged(postion);
                                ToastUtil.showShort(context, msg);
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(context, message);
                            }
                        }));
    }

    /**
     *
     *   拉黑/解除拉黑操作  禁言/解除禁言
     *  chatId 课程id
     *  chatType 1课程 2系列课
     *  userCode 目标用户新媒号
     *  token 当前操作人token
     *  type 1拉黑/解除拉黑操作  2禁言/解除禁言操作
     */
    public void doJoinMemberBlacklistOrProhibitSpeaking(final ChatJoinBean.MemberBean bean, final String type, String state, int position, final String msg){
        HashMap<String,String> map =new HashMap<>();
        map.put("chatId", myBean.get("chatId"));
        map.put("chatType", myBean.get("type"));
        map.put("userCode", bean.getUserCode());
        map.put("token", UserManager.getInstance().getToken());
        map.put("type",type);
        map.put("state",state);
        LiveApi.getInstance().doJoinMemberBlacklistOrProhibitSpeaking(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new
                        ApiObserver<ApiResponseBean<Object>>
                        (new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                if(type.equals("1")){ //黑名单
                                    bean.setBlacklist(bean.isBlacklist()? "0": "1");

                                }else{ //禁言
                                    bean.setProhibitSpeaking(bean.isProhibitSpeaking()? "0" : "1");
                                }
                                ToastUtil.showShort(context, msg);

                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(context, message);
                            }
                        }));
    }


}

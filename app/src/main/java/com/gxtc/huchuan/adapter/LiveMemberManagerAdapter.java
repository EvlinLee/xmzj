package com.gxtc.huchuan.adapter;

import android.content.Context;
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
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ChatJoinBean;
import com.gxtc.huchuan.bean.LiveManagerBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.widget.MyActionSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.gxtc.huchuan.bean.ChatInfosBean.ROLE_CREATER;
import static com.gxtc.huchuan.bean.ChatInfosBean.ROLE_MANAGER;
import static com.gxtc.huchuan.bean.ChatInfosBean.ROLE_STUDENT;
import static com.gxtc.huchuan.bean.ChatInfosBean.ROLE_TEACHER;

/**
 * Describe:获取直播间管理员 > 列表
 *
 */

public class LiveMemberManagerAdapter extends BaseRecyclerAdapter<ChatJoinBean.MemberBean>{

    private final int mCurrPosition;
    private HashMap<String, String> myBean;
    private RecyclerView recyclerView;
    private ManageResult listener;

    public interface ManageResult{
        void result();
    }

    public LiveMemberManagerAdapter(Context context, List<ChatJoinBean.MemberBean> list, int itemLayoutId,int currPosition, HashMap<String, String> myBean, RecyclerView recyclerView) {
        super(context, list, itemLayoutId);
        mCurrPosition = currPosition;
        this.myBean = myBean;
        this.recyclerView = recyclerView;
    }
    public void setManageResult(ManageResult listener){
        this.listener = listener;
    }

    @Override
    public void bindData(ViewHolder holder, final int position, final ChatJoinBean.MemberBean member) {
        TextView title = holder.getViewV2(R.id.title_type);
//        if (position == 0){
//            title.setVisibility(View.VISIBLE);
//            title.setText("课程管理");
//        }else if (position==mCurrPosition){
//            title.setVisibility(View.VISIBLE);
//            title.setText("历史嘉宾");
//        }else{
            title.setVisibility(View.GONE);
//        }

        //隐藏管理按钮
        if(myBean.get("joinType").equals(ROLE_TEACHER) || myBean.get("joinType").equals(ROLE_STUDENT) || UserManager.getInstance().getUserCode().equals(member.getUserCode()) ||
                (myBean.get("joinType").equals(ROLE_MANAGER) && (member.getJoinType().equals(ChatJoinBean.ROLE_HOST) || member.getJoinType().equals(ChatJoinBean.ROLE_MANAGER)))){
            holder.getViewV2(R.id.tv_manage).setVisibility(View.GONE);
        }else{
            holder.getViewV2(R.id.tv_manage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowBottomMenu(member, position);
                }
            });
        }

        ImageHelper.loadCircle(holder.getItemView().getContext(),
                holder.getImageView(R.id.iv_head),
                member.getHeadPic(),
                R.drawable.person_icon_head_120);

        holder.setText(R.id.tv_name,member.getName());

        String joinType = member.getJoinType();
        TextView tv_sub_name = (TextView) holder.getView(R.id.tv_sub_name);
        if ("3".equals(joinType)){
            tv_sub_name.setText("主持人");
        }else if ("1".equals(joinType)){
            tv_sub_name.setText("管理员");
        }else if ("2".equals(joinType)) {
            tv_sub_name.setText("讲师");
        }else {
            tv_sub_name.setText("学员");
        }

    }

    public void ShowBottomMenu(final ChatJoinBean.MemberBean member, final int mPostion) {
        if (member.getUserCode().equals(UserManager.getInstance().getUserCode())) return;
        final List<String> itemList = new ArrayList<>();
        if (!member.getJoinType().equals(ChatJoinBean.ROLE_MANAGER) && myBean.get("joinType").equals(ROLE_CREATER)) {
            itemList.add("升级为管理员");
        }else if(myBean.get("joinType").equals(ROLE_CREATER) && member.getJoinType().equals(ChatJoinBean.ROLE_MANAGER)){
            itemList.add("取消管理员");
        }
        if (!member.getJoinType().equals(ChatJoinBean.ROLE_TEACHER)) {
            itemList.add("升级为讲师");
        }else{
            itemList.add("取消讲师");
        }
        if(member.isProhibitSpeaking()) {
            itemList.add("解禁");
        }else{
            itemList.add("禁言");
        }
        if (member.isBlacklist())
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
                            updateJoin(member.getUserCode(), ChatJoinBean.ROLE_MANAGER, member.getJoinType(), mPostion, "升级管理员成功");
                            break;

                        case "取消管理员":
                            updateJoin(member.getUserCode(), ChatJoinBean.ROLE_ORDINARY , member.getJoinType(), mPostion, "取消管理员成功");
                            break;

                        case "升级为讲师":
                            updateJoin(member.getUserCode(), ChatJoinBean.ROLE_TEACHER , member.getJoinType(), mPostion, "升级讲师成功");
                            break;

                        case "取消讲师":
                            updateJoin(member.getUserCode(), ChatJoinBean.ROLE_ORDINARY , member.getJoinType(), mPostion, "取消讲师成功");
                            break;

                        case "禁言":
                            doJoinMemberBlacklistOrProhibitSpeaking(member, "2", "1", mPostion, "已禁言");
                            break;

                        case "解禁":
                            doJoinMemberBlacklistOrProhibitSpeaking(member, "2","0", mPostion,"已解禁");
                            break;

                        case "取消黑名单":
                            doJoinMemberBlacklistOrProhibitSpeaking(member, "1", "0",mPostion,"已取消黑名单");
                            break;

                        case "加入黑名单":
                            doJoinMemberBlacklistOrProhibitSpeaking(member, "1","1", mPostion,"已加入黑名单");
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
    private void updateJoin(String userCode, final String userType, final String oldJoinType, final int positoin, final String msg){
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

                                ToastUtil.showShort(context, msg);
                                if(!oldJoinType.equals(ChatJoinBean.ROLE_ORDINARY) && !userType.equals(ChatJoinBean.ROLE_ORDINARY)){
                                    //从讲师成为管理员或从管理员成为讲师
                                    getList().get(positoin).setJoinType(userType);
                                    recyclerView.notifyItemChanged(positoin);
                                }else {
                                    //从讲师/管理员成为普通成员
                                    recyclerView.removeData(LiveMemberManagerAdapter.this, positoin);
                                    if(listener != null){
                                        listener.result();
                                    }
                                }
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
     *  state 0.解除 1.拉黑/禁言
     */
    public void doJoinMemberBlacklistOrProhibitSpeaking(final ChatJoinBean.MemberBean member, final String type, String state, int position, final String msg){
        HashMap<String,String> map =new HashMap<>();
        map.put("chatId", myBean.get("chatId"));
        map.put("chatType", myBean.get("type"));
        map.put("userCode", member.getUserCode());
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
                                    member.setBlacklist(member.isBlacklist()? "0": "1");

                                }else{ //禁言
                                    member.setProhibitSpeaking(member.isProhibitSpeaking()? "0" : "1");
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

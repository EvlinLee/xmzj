package com.gxtc.huchuan.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.GoodsCommentBean;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.bean.event.EventCommentBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.DialogUtil;

import java.util.List;

import io.rong.imkit.emoticon.AndroidEmoji;


public class GoodsDetailedAdapter extends BaseRecyclerAdapter<GoodsCommentBean> implements
        View.OnClickListener {

    private String userCode;

    public GoodsDetailedAdapter(Context context, List<GoodsCommentBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        userCode = UserManager.getInstance().getUserCode();
    }

    @Override
    public void bindData(ViewHolder holder, int position, GoodsCommentBean bean) {
        ImageView imgIcon = (ImageView) holder.getView(R.id.img_icon);
        ImageView imgDele = (ImageView) holder.getView(R.id.img_delet);
        TextView tvName = (TextView) holder.getView(R.id.tv_name);
        TextView tvContent = (TextView) holder.getView(R.id.tv_content);
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        TextView tvComment = (TextView) holder.getView(R.id.tv_comment);
        TextView tvGoods = (TextView) holder.getView(R.id.tv_goods);
        LinearLayout layout = (LinearLayout) holder.getView(R.id.layout_sub_comment);

        ImageHelper.loadCircle(getContext(),imgIcon,bean.getUserPic(),R.drawable.circle_head_icon_120);
        imgIcon.setTag(R.id.tag_first,bean.getUserCode());
        imgIcon.setOnClickListener(this);

        tvName.setText(bean.getUserName());

        SpannableString sp = null;
        if(bean.getComment() != null){
            sp = new SpannableString(AndroidEmoji.ensure(bean.getComment()));
        }
        if (!TextUtils.isEmpty(bean.getComment()))
            tvContent.setText(sp);
        else
            tvContent.setVisibility(View.GONE);

        long time = bean.getCreatetime();
        String s = DateUtil.showTimeAgo(time + "");
        tvTime.setText(s);

        tvComment.setTag(bean);
        tvComment.setOnClickListener(this);

        Drawable d = null ;
        if(bean.getIsDZ() == 0){
            d = getContext().getResources().getDrawable(R.drawable.circle_home_icon_zan);
        }else{
            d = getContext().getResources().getDrawable(R.drawable.circle_home_icon_zan_select);
        }
        d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
        tvGoods.setCompoundDrawables(d,null,null,null);
        tvGoods.setText(bean.getDzNum() + "");
        tvGoods.setTag(bean);
        tvGoods.setOnClickListener(this);

        imgDele.setTag(bean);
        imgDele.setOnClickListener(this);
        if(!TextUtils.isEmpty(userCode) && userCode.equals(bean.getFbCode())){
            imgDele.setVisibility(View.VISIBLE);
        }else{
            imgDele.setVisibility(View.INVISIBLE);
        }

        if(layout.getChildCount() != 0){
            layout.removeAllViews();
        }

        List<GoodsCommentBean.ReplyVosBean> subs = bean.getReplyVos();
        for (int i = 0; i < subs.size(); i++) {
            GoodsCommentBean.ReplyVosBean subBean = subs.get(i);
            View view =  View.inflate(getContext(),R.layout.item_goods_comment_sub,null);
            ImageView subIcon = (ImageView) view.findViewById(R.id.img_icon);
            TextView subName = (TextView) view.findViewById(R.id.tv_name);
            TextView subContent = (TextView) view.findViewById(R.id.tv_content);
            TextView subTime = (TextView) view.findViewById(R.id.tv_time);
            View line = view.findViewById(R.id.line);

            ImageHelper.loadCircle(getContext(),subIcon,subBean.getUserPic(),R.drawable.circle_head_icon_120);
            subIcon.setOnClickListener(this);

            subName.setText(subBean.getUserName());

            String temp = "回复 ";
            String content = temp + subBean.getTargetUserName() + "：" + subBean.getComment();
            ForegroundColorSpan forSpan = new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent));

            SpannableString subSp = null;
            if(!TextUtils.isEmpty(content) && subBean.getTargetUserName() != null){
                subSp = new SpannableString(AndroidEmoji.ensure(content));
                subSp.setSpan(forSpan, temp.length() , temp.length()  + subBean.getTargetUserName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (!TextUtils.isEmpty(content))
                subContent.setText(subSp);
            else
                subContent.setVisibility(View.GONE);

            long sTime = subBean.getCreatetime();
            subTime.setText(DateUtil.showTimeAgo(sTime+""));

            if(i == subs.size() - 1){
                line.setVisibility(View.GONE);
            }else{
                line.setVisibility(View.VISIBLE);
            }

            view.setTag(subBean);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoodsCommentBean.ReplyVosBean temp = (GoodsCommentBean.ReplyVosBean) v.getTag();
                    EventBusUtil.post(new EventCommentBean(temp.getTradeCommentId()+"",temp.getUserName(),"1",temp.getUserId()+""));
                }
            });

            layout.addView(view);

        }

    }

    private AlertDialog dialog ;
    private View clickView;
    @Override
    public void onClick(View v) {
        clickView = v;
        switch (v.getId()){
            case R.id.tv_comment:
                GoodsCommentBean bean = (GoodsCommentBean) v.getTag();
                EventBusUtil.post(new EventCommentBean(bean.getId() + "", bean.getUserName(),
                        bean.getIsSelf() + "", bean.getUserId() + ""));
                break;

            case R.id.img_icon:
                String code = (String) v.getTag(R.id.tag_first);
                PersonalInfoActivity.startActivity(getContext(),code);
                break;

            case R.id.img_delet:
                if(dialog == null){
                    dialog = DialogUtil.showInputDialog((Activity) getContext(),false,null,"确认删除这条评论？",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EventBusUtil.post(new EventClickBean("",clickView));
                                    dialog.dismiss();
                                }
                            });
                }else{
                    dialog.show();
                }

                break;

            case R.id.tv_goods:
                EventBusUtil.post(new EventClickBean("",clickView));
                break;
        }


    }
}

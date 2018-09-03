package com.gxtc.huchuan.ui.mine.news;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AuditBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.editorarticle.ArticleResolveActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.news.applyauthor.ApplyAuthorActivity;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Describe: 我的 > 资讯
 * Created by ALing on 2017/3/13 .
 */

public class NewsFragment extends BaseTitleFragment {

    @BindView(R.id.tv_apply_author)         TextView    tvApplyAuthor;
    @BindView(R.id.tv_mine_article)         TextView    tvMineArticle;
    @BindView(R.id.tv_publish_articles)     TextView    tvPublishArticles;     //申请作者审核成功后显示，审核未成功进入审核页面
    @BindView(R.id.tv_apply_progress)       TextView    tvApplyProgress;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_mine_news, container, false);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        EventBusUtil.register(this);
        //作者隐藏作者申请页
        if (UserManager.getInstance().isLogin()) {
            if ("1".equals(UserManager.getInstance().getIsAuthor())) {
                tvApplyAuthor.setVisibility(View.GONE);
            } else if ("0".equals(UserManager.getInstance().getIsAuthor())) {

            }
        }

    }

    @OnClick({R.id.tv_apply_author,R.id.tv_publish_articles, R.id.tv_mine_article, R.id.tv_manager_comment})
    public void onClick(View view) {
        switch (view.getId()) {
            //申请作者
            case R.id.tv_apply_author:
                goToActivity(ApplyAuthorActivity.class);
                break;
            //发布文章
            case R.id.tv_publish_articles:
                goToActivity(ArticleResolveActivity.class);
                break;
            case R.id.tv_mine_article://个人发布文章
                goToActivity(MineArticleActivity.class);
//                if (UserManager.getInstance().isLogin()) {
//                    GotoUtil.goToActivity(this.getActivity(), MineArticleActivity.class);
//                } else {
//                    GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class, Constant.requestCode.NEWS_AUTHOR);
//                }
                break;
            case R.id.tv_manager_comment:

                break;

            /*case R.id.tv_news_collection:
                goToActivity(NewsCollectActivity.class);
                break;*/
        }
    }

    private void goToActivity(Class<?> toClass) {
        if (UserManager.getInstance().isLogin()) {
            GotoUtil.goToActivity(getActivity(), toClass);
        } else {
            GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class,
                    Constant.requestCode.NEWS_AUTHOR);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.requestCode.NEWS_AUTHOR && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
            initData();
        }
    }

    //申请作者、进度、个人文章
    @Subscribe
    public void onEvent(AuditBean bean) {
        /*if ("0".equals(bean.getAudit())){
            //新申请，未审核
            tvApplyAuthor.setVisibility(View.GONE);
            tvMineArticle.setVisibility(View.GONE);
            tvApplyAudit.setVisibility(View.VISIBLE);
        }else if ("1".equals(bean.getAudit())){
            //申请作者成功
            tvApplyAuthor.setVisibility(View.GONE);
            tvMineArticle.setVisibility(View.VISIBLE);
            tvApplyAudit.setVisibility(View.GONE);
        }else if("2".equals(bean.getAudit())){
            //申请不通过
            tvApplyAuthor.setVisibility(View.VISIBLE);
            tvMineArticle.setVisibility(View.GONE);
            tvApplyAudit.setVisibility(View.GONE);
        }*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }
}

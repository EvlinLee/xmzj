package com.gxtc.huchuan.ui.mine.homepage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.editorarticle.ArticleResolveActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import butterknife.OnClick;

/**
 * Created by ALing on 2017/3/10 .
 */

public class HomePageFragment extends BaseTitleFragment{
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        if (UserManager.getInstance().isLogin()){

        }else {

        }

    }

    @OnClick({/*R.id.tv_personal_edit,*/R.id.tv_my_wallet,R.id.tv_my_message,R.id.tv_reward_record})
    public void onClick(View view){
        switch (view.getId()){
            //个人编辑
//            case R.id.tv_personal_edit:
//                gotoArticle();
//                goToActivity(EditInfoActivity.class);
//                break;
            //我的钱包
            case R.id.tv_my_wallet:

                break;
            //消息通知
            case R.id.tv_my_message:

                break;
            //打赏记录
            case R.id.tv_reward_record:

                break;
        }
    }
    private void gotoArticle(){
        if(UserManager.getInstance().isLogin(getActivity())){
            GotoUtil.goToActivity(getActivity(),ArticleResolveActivity.class);
        }
    }
    private void goToActivity(Class<?> toClass) {
        if (UserManager.getInstance().isLogin()){
            GotoUtil.goToActivity(getActivity(), toClass);
        }else {
            GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
        }
    }
}

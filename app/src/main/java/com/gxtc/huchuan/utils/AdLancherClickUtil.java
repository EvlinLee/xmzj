package com.gxtc.huchuan.utils;

import android.app.Activity;
import android.content.Intent;

import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.ui.LaunchActivity;
import com.gxtc.huchuan.ui.MainActivity;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/22.
 * 通用app启动广告点击事件处理
 */

public class AdLancherClickUtil {

    public static void performClick(Activity activity,NewsAdsBean bean){
        if(bean != null){
            int type = bean.getContentType();
            Intent intentMain = new Intent(activity, MainActivity.class);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            switch (type){
                //广告
                case 0:
                    Intent adIntent = new Intent(activity, CommonWebViewActivity.class);
                    adIntent.putExtra("web_url", bean.getUrl()+"&from=android");
                    adIntent.putExtra("web_title", bean.getTitle());
                    adIntent.putExtra("web_cover", bean.getCover());
                    adIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivities(new Intent[]{intentMain,adIntent});
                    break;

                //新闻
                case 1:
                    Intent newsIntent = new Intent(activity, NewsWebActivity.class);
                    NewsAdsBean.NewsRespVoBean newsRespVo = bean.getNewsRespVo();
                    if(newsRespVo != null){
                        NewsBean tempBean = new NewsBean();
                        tempBean.setId(newsRespVo.getId());
                        tempBean.setSource(newsRespVo.getSource());
                        tempBean.setIsVideo(newsRespVo.getIsVideo());
                        tempBean.setVideoUrl(newsRespVo.getVideoUrl());
                        tempBean.setSourceicon(newsRespVo.getSourceicon());
                        tempBean.setCommentCount(newsRespVo.getCommentCount());
                        tempBean.setThumbsupCount(newsRespVo.getThumbsupCount());
                        tempBean.setIsThumbsup(newsRespVo.getIsThumbsup());
                        tempBean.setIsCollect(newsRespVo.getIsCollect());
                        tempBean.setCover(newsRespVo.getCover());
                        tempBean.setTitle(newsRespVo.getTitle());
                        tempBean.setDigest(newsRespVo.getDigest());
                        tempBean.setRedirectUrl(newsRespVo.getRedirectUrl());
                        tempBean.setUserCode(newsRespVo.getUserCode());
                        newsIntent.putExtra("data", tempBean);
                        newsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivities(new Intent[]{intentMain,newsIntent});
                    }

                    break;

                //交易
                case 4:
                    DealListBean dealBean = bean.getTradeInfoVo();
                    if(dealBean != null){
                        Intent intent = new Intent(activity,GoodsDetailedActivity.class);
                        intent.putExtra(Constant.INTENT_DATA,dealBean.getId());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivities(new Intent[]{intentMain,intent});
                    }
                    break;

                //课堂
                case 5:
                    ChatInfosBean chatBean = bean.getChatInfoRespVo();
                    if(chatBean != null){
                        CircleShareHandler mShareHandler = new CircleShareHandler(activity);
                        mShareHandler.getLiveInfoFromAd(chatBean.getId(),null);
                    }
                    break;
            }
        }

    }

}
